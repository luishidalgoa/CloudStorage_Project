package net.ddns.levelcloud.music.Features.Download.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.Executors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(DownloadProgressController.class)
public class DownloadProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DownloadProgressController downloadProgressController;

    @Test
    public void testProgressEndpoint() throws Exception {
        String id = "test-id";

        // Inicia un hilo para simular la actualización del progreso
        Executors.newSingleThreadExecutor().execute(() -> {
            for (int progress = 0; progress <= 100; progress += 10) {
                downloadProgressController.updateProgress(id, progress);
                try {
                    Thread.sleep(500); // Simula un retardo de actualización
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Realiza la solicitud y valida que el endpoint responde correctamente
        mockMvc.perform(get("/api/music/download/progress/{id}", id)
                        .header("Authorization", "Basic test-token")
                        .header("DownloadOption", "Local")
                        .contentType(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertThat(responseBody).contains("10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%");
                });
    }
}
