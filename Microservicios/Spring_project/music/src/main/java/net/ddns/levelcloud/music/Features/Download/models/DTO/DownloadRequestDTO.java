package net.ddns.levelcloud.music.Features.Download.models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ddns.levelcloud.music.Features.Download.models.Enum.DownloadType;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DownloadRequestDTO {
    private String id;
    private DownloadDataDTO data;
    public DownloadType downloadType = DownloadType.Local;

    @Data
    @Builder
    public static class DownloadDataDTO {
        public String externalUrl;
        public String DirectoryPath;

        public DownloadDataDTO(@JsonProperty("externalUrl") String externalUrl, @JsonProperty("DirectoryPath") String DirectoryPath) {
            this.externalUrl = externalUrl;
            this.DirectoryPath = DirectoryPath;
        }
    }
}