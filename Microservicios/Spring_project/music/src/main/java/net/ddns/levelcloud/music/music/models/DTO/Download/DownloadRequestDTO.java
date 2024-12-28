package net.ddns.levelcloud.music.music.models.DTO.Download;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DownloadRequestDTO {
    private DownloadDataDTO data;

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