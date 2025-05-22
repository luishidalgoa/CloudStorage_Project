package net.ddns.levelcloud.music.Features.Download.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import net.ddns.levelcloud.music.Features.Download.Exceptions.DownloadIdNotFoundException;
import net.ddns.levelcloud.music.Features.Download.models.DTO.DownloadRequestDTO;
import net.ddns.levelcloud.music.Features.Download.models.DTO.ProgressDto;
import net.ddns.levelcloud.music.Features.Download.models.Enum.DownloadType;
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
    @GetMapping(value = "/progress/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Inicia una conexión SSE para recibir el progreso de la descarga",
            description = "Crea una conexión unidireccional SSE que devuelve continuamente el estado del progreso al cliente que lo consume. Devolviendo el progress y el total de archivos descargados")
    @ApiResponse(responseCode = "200", description = "Conexión SSE iniciada")
    public SseEmitter progress(@RequestHeader(value = "Authorization", required = false) String auth,
                               @RequestParam(value = "DownloadType", required = false, defaultValue = "Local") DownloadType downloadType,
                               @PathVariable("id") String id) {

        if (!progressMap.containsKey(id)) {
            throw new DownloadIdNotFoundException(id);
        }

        SseEmitter emitter = new SseEmitter();
        Executors.newSingleThreadExecutor().execute(() -> {
            ProgressDto object = progressMap.get(id);
            if (object == null)
                throw new DownloadIdNotFoundException(id);

            try {
                int lastIndex = -1;
                while (object.getProgress() < 100) {
                    if (object.getCurrentIndex() >= object.getRequest().getData().getTotalFiles())
                        break;

                    emitter.send(SseEmitter.event().name("progress").data(object.getProgress()));
                    if (lastIndex != object.getCurrentIndex()) {
                        emitter.send(SseEmitter.event().name("total").data(object.getCurrentIndex()+" / "+object.getRequest().getData().getTotalFiles()));
                        lastIndex = object.getCurrentIndex();
                    }
                    Thread.sleep(500);
                }
                object.getProcess().waitFor();
                emitter.send(SseEmitter.event().name("progress").data(100));
                emitter.send(SseEmitter.event().name("total").data(object.getCurrentIndex()+" / "+object.getRequest().getData().getTotalFiles()));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    public boolean remove(String id) {
        progressMap.remove(id);
        return progressMap.remove(id) == null;
    }

    public void updateProgress(String id, double progress) {
        progressMap.get(id).calculateProgress(progress);
    }

    public void setProcess(String id, Process process, DownloadRequestDTO request) {
        progressMap.put(id, new ProgressDto(process,request));
    }

    public void setProgressDto(String id, ProgressDto progressDto) {
        progressMap.put(id, progressDto);
    }
    public ProgressDto getProgress(String id) {
        return progressMap.get(id);
    }
}

