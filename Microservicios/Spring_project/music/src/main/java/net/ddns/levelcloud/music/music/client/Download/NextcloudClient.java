package net.ddns.levelcloud.music.music.client.Download;

import net.ddns.levelcloud.music.music.models.DTO.Download.DownloadRequestDTO;
import net.ddns.levelcloud.music.music.models.DTO.Download.NextcloudUploadDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "nextcloud", url = "lb://nextcloud:8082")
public interface NextcloudClient {

    @PostMapping("/api/nextcloud/upload/status")
    boolean getUploadStatus(@RequestHeader("Authorization") String header, @RequestBody DownloadRequestDTO response);

    @PostMapping("/api/nextcloud/upload")
    String getUpload(@RequestHeader("Authorization") String header, @RequestBody NextcloudUploadDTO response);

}