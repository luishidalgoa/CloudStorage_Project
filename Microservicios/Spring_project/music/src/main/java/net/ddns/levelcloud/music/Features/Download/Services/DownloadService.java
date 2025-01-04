package net.ddns.levelcloud.music.Features.Download.Services;

import lombok.AllArgsConstructor;
import net.ddns.levelcloud.music.Features.Download.controllers.DownloadProgressController;
import net.ddns.levelcloud.music.Features.Download.logic.DownloadLocal;
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
    private long extractFileSize(String line) {
        // Expresión regular para buscar el tamaño del archivo en formato MiB o KiB
        String regex = "\\s(\\d+(\\.\\d+)?[KM]?iB)\\s"; // Captura valores como 1.10MiB, 49k, etc.
        if (line.contains("audio only") && line.contains("webm") && line.contains("251")) {
            // Buscar el tamaño de archivo usando la expresión regular
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                // El tamaño del archivo estará en el primer grupo
                return fileSizeToBytes(matcher.group(1));
            }
        }
        return -1;
    }

    private long fileSizeToBytes(String fileSize) {
        // Expresión regular para buscar el tamaño del archivo en formato MiB o KiB
        String regex = "(\\d+(\\.\\d+)?)([KM])?iB"; // Captura valores como 1.10MiB, 49k, etc.
        // Buscar el tamaño de archivo usando la expresión regular
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileSize);

        if (matcher.find()) {
            // El tamaño del archivo estará en el primer grupo
            double size = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(3);

            switch (unit) {
                case "K":
                    return (long) (size * 1024);
                case "M":
                    return (long) (size * 1024 * 1024);
                default:
                    return (long) size;
            }
        }
        return 0;
    }


    // ------------------------ Upload file

    public LocalUploadDTO upload(String id) {
        return new DownloadLocal(downloadProgressController).upload(id);
    }

    private boolean deleteDirectory(String directoryPath) {
        if (!new File(directoryPath).exists())
            throw new IllegalArgumentException("No existe el ID de descarga en el servidor.");

        ProcessBuilder pb = new ProcessBuilder(
                "rm",
                "-rf",
                directoryPath
        );

        try {
            Process process = pb.start();
            int exitCode = process.waitFor(); // Espera a que el proceso termine y obtiene el código de salida

            // Si el código de salida es 0, el comando se ejecutó correctamente
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("No se pudo eliminar el directorio temporal en el servidor.");
    }
}
