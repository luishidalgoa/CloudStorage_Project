package net.ddns.levelcloud.music.music.services;

import lombok.AllArgsConstructor;
import net.ddns.levelcloud.music.music.controllers.download.DownloadProgressController;
import net.ddns.levelcloud.music.music.models.DTO.Download.DownloadRequestDTO;
import net.ddns.levelcloud.music.music.models.DTO.Download.FileDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class DownloadService {
    private final DownloadProgressController downloadProgressController;

    public void download(DownloadRequestDTO request) {
        String url = request.getData().externalUrl;
        String downloadId = request.getId();

        // tamaño total de la descarga
        long filesize= this.fileSizeCalculate(url);
        if (filesize == -1)
            throw new IllegalArgumentException("Could not calculate file size");

        try {
            switch (request.downloadType) {
                case Local:
                    processDownload(downloadId, url);
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
    }

    private void processDownload(String downloadId, String url) {
        String tempDir = System.getProperty("java.io.tmpdir");
        // Directorio raiz donde se descargaran todas las sesiones de descargas
        String rootPath = tempDir+File.separator+"MusicDownload";
        // Directorio donde se guardaran los ficheros de la sesion actual
        String directoryPath = rootPath+ File.separator + downloadId;
        //creamos el directorio MusicDownload
        {
            File root = new File(rootPath);
            if (!root.exists())
                root.mkdirs();
        }
        // Creamos el directorio temporal de la descarga
        {
            File directory = new File(directoryPath);
            directory.mkdirs();
        }
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "yt-dlp",
                    "-o", directoryPath+File.separator+"%(title)s.%(ext)s",
                    "--extract-audio",
                    "--audio-format", "mp3",
                    url
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int progress = extractProgressFromLine(line);
                    downloadProgressController.updateProgress(downloadId, progress);
                }
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int extractProgressFromLine(String line) {
        return 0;
    }

    // ------------------------ FileSize
    public long fileSizeCalculate(String url){
        ProcessBuilder pb = new ProcessBuilder(
                "yt-dlp",
                "-F", url
        );

        try {
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                long result = -1;
                while ((line = reader.readLine()) != null && result == -1) {
                    result = extractFileSize(line);
                }
                process.waitFor();
                return result;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    //private final FtpService ftpService; // Servicio para interactuar con el servidor FTP local
    public FileDTO upload(String downloadId, String directoryPath) {
        try {
            File root = new File(directoryPath);

            if (!root.exists() || root.listFiles() == null)
                throw new IllegalArgumentException("No existe el ID de descarga en el servidor o el directorio está vacío.");

            File[] children = Objects.requireNonNull(root.listFiles());

            if (children.length == 1) {
                // Si hay un único archivo, devuelve el archivo directamente
                return this.getOnlyFile(root, children[0]);
            } else {
                // Si hay varios archivos, devuelve un archivo ZIP comprimido
                return this.getZipFile(root);
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("No se encontraron archivos en el directorio de descarga.");
        }
    }

    private FileDTO getOnlyFile(File root, File file) {
        try {
            // Abrimos el FileInputStream
            FileInputStream fileInputStream = new FileInputStream(file);

            // Creamos el InputStreamResource para enviar al cliente
            InputStreamResource resource = new InputStreamResource(fileInputStream);

            return FileDTO.builder()
                    .directoryPath(root.getAbsolutePath())
                    .fileChildren(file)
                    .resource(resource)
                    .build();

        } catch (FileNotFoundException e) {
            throw new RuntimeException("No se encontró el archivo: " + file.getPath(), e);
        }
    }
    private FileDTO getZipFile(File root) {
        return null;
    }

    private boolean deleteDirectory(String directoryPath) {
        if(!new File(directoryPath).exists())
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
