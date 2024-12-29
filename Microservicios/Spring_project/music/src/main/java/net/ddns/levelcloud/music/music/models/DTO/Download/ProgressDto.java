package net.ddns.levelcloud.music.music.models.DTO.Download;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgressDto{
    private Integer progress;
    private Process process;
}
