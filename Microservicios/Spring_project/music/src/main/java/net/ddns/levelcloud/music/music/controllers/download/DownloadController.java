package net.ddns.levelcloud.music.music.controllers.download;

import net.ddns.levelcloud.music.music.models.DTO.Download.DownloadRequestDTO;
import net.ddns.levelcloud.music.music.models.Enum.DownloadType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/music/download")
public class DownloadController {
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final DownloadProgressController downloadProgressController;

    public DownloadController(DownloadProgressController downloadProgressController) {
        this.downloadProgressController = downloadProgressController;
    }
    //private final DownloadProgressController progressController;
    //private final FtpService ftpService; // Servicio para interactuar con el servidor FTP local

    @RequestMapping("/request")
    public ResponseEntity<String> download(@RequestHeader(value = "Authorization",required = false) String auth,
                                           @RequestHeader(value = "DownloadOption",defaultValue = "Local") DownloadType downloadOption,
                                           @RequestBody DownloadRequestDTO request) {
        String downloadId = UUID.randomUUID().toString();
        String url = request.getData().getExternalUrl();

        executorService.submit(() -> {
            try {
                switch (downloadOption) {
                    case Local:
                        processDownload(downloadId, url);
                        break;
                    case LevelCloud:
                        break;
                    default:
                        throw new IllegalArgumentException("Opción de descarga no válida");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //ftpService.uploadFile(downloadId);
        return ResponseEntity.ok(downloadId);
    }

    private void processDownload(String downloadId, String url) {
        String tempDir = System.getProperty("java.io.tmpdir");
        String filePath = tempDir + File.separator + downloadId + ".mp3";

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "/env/bin/python3","-m",
                    "yt-dlp",
                    "-o", filePath,
                    "--extract-audio",
                    "--audio-format", "mp3",
                    url
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                int prueba = 0;
                while ((line = reader.readLine()) != null) {
                    int progress = extractProgressFromLine(line);
                    downloadProgressController.updateProgress(downloadId, prueba+=10); //cambiar prueba por progress
                }
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int extractProgressFromLine(String line) {
        // Lógica para extraer el progreso desde la salida de yt-dlp
        return 0; // Implementar lógica
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
    public String downloadFile(@RequestHeader("Authorization") String header) {
        return "Descarga exitosa";
    }
}
