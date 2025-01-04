package net.ddns.levelcloud.music.Features.Download.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DownloadIdNotFoundException extends RuntimeException{
    public DownloadIdNotFoundException(String id) {
        super("Error 404: El id de descarga no existe - " + id);
    }
}
