package net.ddns.levelcloud.music.Features.Download.models.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ddns.levelcloud.music.Features.Download.models.Enum.DownloadType;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Excluye campos con valor null
public class DownloadRequestDTO {
    private String id;
    private DownloadRequestMusicData data;
    public DownloadType downloadType = DownloadType.Local;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL) // Excluye campos con valor null dentro de este objeto también
    @NoArgsConstructor
    public static class DownloadRequestMusicData {
        public String externalUrl;
        public String directoryPath;
        public Long downloadSyze;
        private int totalFiles;

        public void setTotalFiles(int totalFiles) {
            this.totalFiles = totalFiles == 0 ? 1 : totalFiles;
        }
        public DownloadRequestMusicData(@JsonProperty("externalUrl") String externalUrl, @JsonProperty("DirectoryPath") String DirectoryPath,Long downloadSyze,int totalFiles) {
            this.externalUrl = externalUrl;
            this.directoryPath = DirectoryPath;
            this.downloadSyze = downloadSyze;
            this.totalFiles = totalFiles;
        }

        public DownloadRequestMusicData(@JsonProperty("externalUrl") String externalUrl, @JsonProperty("DirectoryPath") String DirectoryPath,Long downloadSyze) {
            this.externalUrl = externalUrl;
            this.directoryPath = DirectoryPath;
            this.downloadSyze = downloadSyze;
            this.totalFiles = 1;
        }
    }
}