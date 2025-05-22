package net.ddns.levelcloud.music.Features.Download.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import net.ddns.levelcloud.music.Features.Download.Services.DownloadService;
import net.ddns.levelcloud.music.Features.Download.models.DTO.DownloadRequestDTO;
import net.ddns.levelcloud.music.Features.Download.models.DTO.LocalUploadDTO;
import net.ddns.levelcloud.music.Features.Download.models.Enum.DownloadType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/music/download")
@AllArgsConstructor
public class DownloadController {

    private DownloadService downloadService;

    @PostMapping("/request")
    @Operation(summary = "Inciar proceso de descargar", description = "Genera un proceso de descarga internamente en el servidor. Devuelve un identificador del seguimiento de la consulta",responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Devuelve el identificador de seguimiento del proceso abierto internamente en el servidor"
            )
    })
    public ResponseEntity<DownloadRequestDTO> download(@RequestHeader(value = "Authorization", required = false) String auth,
                                                       @RequestParam(value = "DownloadType", required = false, defaultValue = "Local") DownloadType downloadType,
                                                       @RequestBody DownloadRequestDTO request) {

        request.setId(UUID.randomUUID().toString());

        request.setDownloadType(downloadType);
        request = this.downloadService.download(request);

        return ResponseEntity.ok(request);  // Devuelve un JSON con el ID
    }

    /**
     * Si la descarga el usuario la solicito mediante el servicio cloud. El metodo le enviara al
     * microservicio nextcloud, el enlace de descarga via FTP, para que el servicio Nextcloud lo
     * almacene y devolveremos en enlace de donde se almacena en nextcloud. Si el usuario lo solicito
     * en local, el servidor se limitara a enviar el enlace FTP
     *
     * @param header (opcional) Token de autorización
     * @return Enlace de descarga
     */
    @GetMapping("/{id}")
    @Operation(summary = "Enviar recursos al cliente", description = "")
    @ApiResponse(
            responseCode = "200",
            description = "Devuelve un fichero o inicia la compresión de un directorio, devolviendo los recursos al cliente",
            content = {
                    @Content(mediaType = "application/x-rar-compressed", schema = @Schema(implementation = InputStreamResource.class)),
                    @Content(mediaType = "application/json", schema = @Schema(implementation = DownloadRequestDTO.class))
            }
    )
    public ResponseEntity<InputStreamResource> uploadFile(@RequestHeader(value = "Authorization", required = false) String header, @RequestParam(value = "DownloadType", required = false, defaultValue = "Local") DownloadType downloadType, @PathVariable String id) {

        LocalUploadDTO resource = this.downloadService.upload(id);

        try {
            if (!resource.getResource().exists())
                return ResponseEntity.notFound().build();

            HttpHeaders headers = new HttpHeaders();
            String safeFileName = URLEncoder.encode(resource.getFileChildrenName(), StandardCharsets.UTF_8).replace("+", "%20");
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*="+safeFileName);
            headers.set(HttpHeaders.CONTENT_TYPE, "application/x-rar-compressed");
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

            if (header != null && header.contains("Range")) {
                HttpRange range = HttpRange.parseRanges(header).get(0);
                long start = range.getRangeStart(0);
                long end = range.getRangeEnd(resource.getResource().contentLength() - 1);

                headers.set("Content-Range", "bytes " + start + "-" + end + "/" + resource.getResource().getFile().length());
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(resource.getResource().getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Cancelar descarga", description = "Cancela la descarga de un recurso")
    @ApiResponse(
            responseCode = "200",
            description = "Cancela la descarga de un recurso"
    )
    @GetMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable String id) {
        if (this.downloadService.cancel(id))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }
}

