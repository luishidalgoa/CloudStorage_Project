package net.ddns.levelcloud.music.music.controllers.download;

import lombok.AllArgsConstructor;
import net.ddns.levelcloud.music.music.models.DTO.Download.DownloadRequestDTO;
import net.ddns.levelcloud.music.music.models.DTO.Download.FileDTO;
import net.ddns.levelcloud.music.music.models.Enum.DownloadType;
import net.ddns.levelcloud.music.music.services.download.DownloadService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/music/download")
@AllArgsConstructor
public class DownloadController {

    private DownloadService downloadService;

    @PostMapping("/request")
    public ResponseEntity<Map<String,String>> download(@RequestHeader(value = "Authorization",required = false) String auth,
                                                       @RequestBody DownloadRequestDTO request) {

        request.setId(UUID.randomUUID().toString());

        this.downloadService.download(request);

        Map<String, String> response = new HashMap<>();
        response.put("downloadId", request.getId());

        return ResponseEntity.ok(response);  // Devuelve un JSON con el ID
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
    public ResponseEntity<InputStreamResource> uploadFile(@RequestHeader(value = "Authorization",required = false) String header,@RequestHeader(value = "DownloadType",required = false,defaultValue = "Local") DownloadType downloadType, @PathVariable String id) {
        String directoryPath = System.getProperty("java.io.tmpdir") + File.separator + "MusicDownload" + File.separator + id;

        FileDTO resource = this.downloadService.upload(id, directoryPath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFileChildrenName())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION) // Exponer el encabezado
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource.getResource());
    }
}

