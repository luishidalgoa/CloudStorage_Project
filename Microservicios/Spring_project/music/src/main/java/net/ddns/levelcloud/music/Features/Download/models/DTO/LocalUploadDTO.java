package net.ddns.levelcloud.music.Features.Download.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class LocalUploadDTO {
    private String fileChildrenName;
    private InputStreamResource resource;
}
