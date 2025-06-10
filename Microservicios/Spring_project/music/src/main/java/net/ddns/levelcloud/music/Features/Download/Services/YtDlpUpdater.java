package net.ddns.levelcloud.music.Features.Download.Services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Service
public class YtDlpUpdater {

    @Scheduled(fixedRate = 5400000) // 1 hora y 30 minutos en milisegundos
    public void updateYtDlp() {
        System.out.println("Iniciando verificación de actualización de yt-dlp...");
        ProcessBuilder processBuilder = new ProcessBuilder("yt-dlp", "-U");
        processBuilder.redirectErrorStream(true); // Mezcla stderr con stdout

        try {
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[yt-dlp] " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Actualización de yt-dlp completada correctamente.");
            } else {
                System.err.println("Falló la actualización de yt-dlp. Código de salida: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error al intentar actualizar yt-dlp:");
            e.printStackTrace();
        }
    }

}
