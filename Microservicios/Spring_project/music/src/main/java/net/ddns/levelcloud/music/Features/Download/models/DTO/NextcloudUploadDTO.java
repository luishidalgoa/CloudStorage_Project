package net.ddns.levelcloud.music.Features.Download.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NextcloudUploadDTO {
    private String path;
    private long size;
}