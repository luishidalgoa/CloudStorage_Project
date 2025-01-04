package net.ddns.levelcloud.music.Features.Download.controllers;

import lombok.AllArgsConstructor;
import net.ddns.levelcloud.music.Features.Download.Services.DownloadService;
import net.ddns.levelcloud.music.Features.Download.models.DTO.DownloadRequestDTO;
import net.ddns.levelcloud.music.Features.Download.models.DTO.LocalUploadDTO;
import net.ddns.levelcloud.music.Features.Download.models.DTO.NextcloudUploadDTO;
import net.ddns.levelcloud.music.Features.Download.models.Enum.DownloadType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/api/music/download")
@AllArgsConstructor
public class DownloadController {

    private DownloadService downloadService;

    @PostMapping("/request")
    public ResponseEntity<DownloadRequestDTO> download(@RequestHeader(value = "Authorization",required = false) String auth,
                                                       @RequestBody DownloadRequestDTO request) {

        request.setId(UUID.randomUUID().toString());

        request = this.downloadService.download(request);

        return ResponseEntity.ok(request);  // Devuelve un JSON con el ID
    }

    /**
     * Si la descarga el usuario la solicito mediante el servicio cloud. El metodo le enviara al
     * microservicio nextcloud, el enlace de descarga via FTP, para que el servicio Nextcloud lo
     * almacene y devolveremos en enlace de donde se almacena en nextcloud. Si el usuario lo solicito
     * en local, el servidor se limitara a enviar el enlace FTP
     * @param header (opcional) Token de autorización + DownloadType
     * @return Enlace de descarga
     */
    @RequestMapping("/{id}")
    public ResponseEntity<?> uploadFile(@RequestHeader(value = "Authorization",required = false) String header,@RequestHeader(value = "DownloadType",required = false,defaultValue = "Local") DownloadType downloadType, @PathVariable String id) {

        LocalUploadDTO resource = this.downloadService.upload(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFileChildrenName())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION) // Exponer el encabezado
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource.getResource());
    }

    @RequestMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable String id) {
        if (this.downloadService.cancel(id))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }
}

