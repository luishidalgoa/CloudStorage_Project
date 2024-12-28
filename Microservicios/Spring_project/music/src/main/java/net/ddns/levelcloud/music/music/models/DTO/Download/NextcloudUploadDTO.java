package net.ddns.levelcloud.music.music.models.DTO.Download;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NextcloudUploadDTO {
    private String path;
    private long size;
}