package net.ddns.levelcloud.music.Features.Download.Services;

import lombok.AllArgsConstructor;
import net.ddns.levelcloud.music.Features.Download.controllers.DownloadProgressController;
import net.ddns.levelcloud.music.Features.Download.logic.DownloadLocal;
import net.ddns.levelcloud.music.Features.Download.logic.abs.AbstractDownloadStrategy;
import net.ddns.levelcloud.music.Features.Download.models.DTO.DownloadRequestDTO;
import net.ddns.levelcloud.music.Features.Download.models.DTO.LocalUploadDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class DownloadService {
    DownloadProgressController downloadProgressController;

    public DownloadRequestDTO download(DownloadRequestDTO request) {

        request = this.downloadCalculate(request);

        try {
            switch (request.downloadType) {
                case Local:
                    new DownloadLocal(downloadProgressController).execute(request);
                    break;
                case LevelCloud:
                    break;
                default:
                    throw new IllegalArgumentException("Opción de descarga no válida");
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }


    public boolean cancel(String id){
        switch (downloadProgressController.getProgress(id).getRequest().getDownloadType()){
            case Local:
                return new DownloadLocal(downloadProgressController).cancelProcess(id);
            case LevelCloud:
                break;
        }
        return false;
    }

    // ------------------------ downloadCalculate

    /**
     * Calcula el total de ficheros a descargar
     * @param request
     * @return
     */
    public DownloadRequestDTO downloadCalculate(DownloadRequestDTO request) {
        ProcessBuilder pb = new ProcessBuilder(
                "yt-dlp",
                "-F", request.getData().externalUrl
        );
        int totalItems = 0;
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            List<String> outputLines = new ArrayList<>();
            Thread stdoutThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        synchronized (outputLines) {
                            if (line.contains("download") && line.contains("item") && line.contains("of")) {
                                outputLines.add(line);
                                process.destroy();
                                break;
                            }
                            outputLines.add(line);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error reading stdout", e);
                }
            });
            stdoutThread.start();
            process.waitFor();
            stdoutThread.join();

            for (String line : outputLines) {
                if (totalItems == 0) {
                    totalItems = totalItems(line);
                }
            }

            request.getData().setTotalFiles(totalItems);

            return request;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private int totalItems(String line){
        if(line.contains("download") && line.contains("item") && line.contains("of")){
            Pattern pattern = Pattern.compile("of (\\d+)");
            Matcher matcher = pattern.matcher(line);

            if(matcher.find()){
                return Integer.parseInt(matcher.group(1));
            }
        }
        return 0;
    }


    // ------------------------ Upload file

    public LocalUploadDTO upload(String id) {
        return new DownloadLocal(downloadProgressController).upload(id);
    }
}
