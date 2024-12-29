package net.ddns.levelcloud.music.music.controllers.download;

import net.ddns.levelcloud.music.music.Exceptions.DownloadIdNotFoundException;
import net.ddns.levelcloud.music.music.models.DTO.Download.ProgressDto;
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
    private final Map<String, ProgressDto> progressMap = new ConcurrentHashMap<>();
    /**
     * Crea una conexion unidireccional SSE que devuelve continuamente el estado del
     * progreso al cliente que lo consume
     * @param auth (opcional) Token de autorización
     * @param downloadType Tipo de descarga
     * @return
     */
    @RequestMapping(value = "/progress/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter progress(@RequestHeader(value = "Authorization", required = false) String auth,
                               @RequestHeader(value = "DownloadType", required = false, defaultValue = "Local") DownloadType downloadType,
                               @PathVariable("id") String id) {

        if (!progressMap.containsKey(id)) {
            throw new DownloadIdNotFoundException(id);
        }

        SseEmitter emitter = new SseEmitter();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ProgressDto object = progressMap.getOrDefault(id,ProgressDto.builder().progress(0).build());
                while (object.getProgress() < 100) {
                    emitter.send(SseEmitter.event().name("progress").data(object.getProgress() + "%"));
                    Thread.sleep(500);
                }
                object.getProcess().waitFor();
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    public void updateProgress(String id, int progress) {
        progressMap.get(id).setProgress(progress);
    }

    public void setProcess(String id, Process process) {
        progressMap.put(id, ProgressDto.builder().progress(0).process(process).build());
    }
}

