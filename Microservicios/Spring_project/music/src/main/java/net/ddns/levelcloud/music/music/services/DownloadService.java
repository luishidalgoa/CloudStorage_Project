package net.ddns.levelcloud.music.music.services;

import lombok.AllArgsConstructor;
import net.ddns.levelcloud.music.music.controllers.download.DownloadProgressController;
import net.ddns.levelcloud.music.music.models.DTO.Download.DownloadRequestDTO;
import net.ddns.levelcloud.music.music.models.Enum.DownloadType;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Service
@AllArgsConstructor
public class DownloadService {
    private final DownloadProgressController downloadProgressController;

    public void download(DownloadRequestDTO request, DownloadType downloadOption) {
        try {
            switch (downloadOption) {
                case Local:
                    processDownload(request.getId(), request.getData().getExternalUrl());
                    break;
                case LevelCloud:
                    break;
                default:
                    throw new IllegalArgumentException("Opción de descarga no válida");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processDownload(String downloadId, String url) {
        String tempDir = System.getProperty("java.io.tmpdir");
        String filePath = tempDir + File.separator + downloadId + ".mp3";

        try {
            ProcessBuilder pb = new ProcessBuilder(
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
        String tempPath = System.getProperty("java.io.tmpdir");
        ProcessBuilder pb = new ProcessBuilder(
                "rm",
                "-rf",
                tempPath+File.separator+"MusicDownload"+File.separator+"*"
        );
        return 0;
    }


    // ------------------------ Upload file
}
