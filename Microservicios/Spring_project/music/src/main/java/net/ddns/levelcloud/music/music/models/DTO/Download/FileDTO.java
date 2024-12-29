package net.ddns.levelcloud.music.music.models.DTO.Download;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

import java.io.File;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FileDTO{
    private String directoryPath;
    private File fileChildren;
    private InputStreamResource resource;
}
