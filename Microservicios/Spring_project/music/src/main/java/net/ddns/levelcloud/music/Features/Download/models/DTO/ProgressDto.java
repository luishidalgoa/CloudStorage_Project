package net.ddns.levelcloud.music.Features.Download.models.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgressDto{
    private Integer progress;
    private Process process;
}
