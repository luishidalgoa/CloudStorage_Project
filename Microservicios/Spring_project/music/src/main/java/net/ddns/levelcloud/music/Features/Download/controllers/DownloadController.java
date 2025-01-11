package net.ddns.levelcloud.music.Features.Download.controllers;

import lombok.AllArgsConstructor;
import net.ddns.levelcloud.music.Features.Download.Services.DownloadService;
import net.ddns.levelcloud.music.Features.Download.models.DTO.DownloadRequestDTO;
import net.ddns.levelcloud.music.Features.Download.models.DTO.LocalUploadDTO;
import net.ddns.levelcloud.music.Features.Download.models.Enum.DownloadType;
import net.ddns.levelcloud.music.util.ThrottledInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/music/download")
@AllArgsConstructor
public class DownloadController {

    private DownloadService downloadService;

    @PostMapping("/request")
    public ResponseEntity<DownloadRequestDTO> download(@RequestHeader(value = "Authorization", required = false) String auth,
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
     *
     * @param header (opcional) Token de autorización + DownloadType
     * @return Enlace de descarga
     */
    @RequestMapping("/{id}")
    public ResponseEntity<InputStreamResource> uploadFile(@RequestHeader(value = "Authorization", required = false) String header, @RequestHeader(value = "DownloadType", required = false, defaultValue = "Local") DownloadType downloadType, @PathVariable String id) {

        LocalUploadDTO resource = this.downloadService.upload(id);

        ThrottledInputStream throttledStream;
        try {
            if (!resource.getResource().exists())
                return ResponseEntity.notFound().build();

            throttledStream = new ThrottledInputStream(
                    resource.getResource().getInputStream()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFileChildrenName());
            headers.set(HttpHeaders.CONTENT_TYPE, "application/x-rar-compressed");
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

            if (header != null && header.contains("Range")) {
                HttpRange range = HttpRange.parseRanges(header).get(0);
                long start = range.getRangeStart(0);
                long end = range.getRangeEnd(resource.getResource().contentLength() - 1);

                throttledStream.skip(start);
                headers.set("Content-Range", "bytes " + start + "-" + end + "/" + resource.getResource().getFile().length());
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(throttledStream));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable String id) {
        if (this.downloadService.cancel(id))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }
}

