package net.ddns.levelcloud.music.music.controllers.download;

import net.ddns.levelcloud.music.music.Exceptions.DownloadIdNotFoundException;
import net.ddns.levelcloud.music.music.models.Enum.DownloadType;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/music/download")
public class DownloadProgressController {
    private final Map<String, Integer> progressMap = new ConcurrentHashMap<>();
    /**
     * Crea una conexion unidireccional SSE que devuelve continuamente el estado del
     * progreso al cliente que lo consume
     * @param auth (opcional) Token de autorización
     * @param downloadType Tipo de descarga
     * @return
     */
    @RequestMapping(value = "/progress/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter progress(@RequestHeader(value = "Authorization", required = false) String auth,
                               @RequestHeader(value = "DownloadOption", required = false, defaultValue = "Local") DownloadType downloadType,
                               @PathVariable("id") String id) {

        if (!progressMap.containsKey(id)) {
            throw new DownloadIdNotFoundException(id);
        }

        SseEmitter emitter = new SseEmitter();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                while (progressMap.getOrDefault(id, 0) < 100) {
                    int progress = progressMap.getOrDefault(id, 0);
                    emitter.send(SseEmitter.event().name("progress").data(progress + "%"));
                    Thread.sleep(500);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    public void updateProgress(String id, int progress) {
        progressMap.put(id, progress);
    }
}
