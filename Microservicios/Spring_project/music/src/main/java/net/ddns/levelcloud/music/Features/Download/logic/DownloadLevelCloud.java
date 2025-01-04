package net.ddns.levelcloud.music.Features.Download.logic;

import net.ddns.levelcloud.music.Features.Download.controllers.DownloadProgressController;
import net.ddns.levelcloud.music.Features.Download.logic.abs.AbstractDownloadStrategy;
import net.ddns.levelcloud.music.Features.Download.models.DTO.DownloadRequestDTO;
import net.ddns.levelcloud.music.Features.Download.models.DTO.NextcloudUploadDTO;

import java.io.File;

public class DownloadLevelCloud extends AbstractDownloadStrategy<NextcloudUploadDTO> {
    protected DownloadLevelCloud(DownloadProgressController downloadProgressController) {
        super(downloadProgressController);
    }

    @Override
    public NextcloudUploadDTO upload(String id) {
        return null;
    }

    @Override
    public NextcloudUploadDTO getOnlyFile(File root, File file) {
        return null;
    }

    @Override
    public NextcloudUploadDTO getZipFile(File root) {
        return null;
    }

    /**
     * Bytes totales que se descargara el usuario
     * @param directory directorio temporal donde se ubican los ficheros
     * @return tamaño total en bytes
     */
    @Override
    public Long getTotalSize(File directory) {
        return directory.length();
    }

    @Override
    public void endDownload(DownloadRequestDTO request, String directoryPath) {

    }

    @Override
    public boolean cancelProcess(String id) {
        return false;
    }

    /**
     * Checkea si en el servicio en la nube hay espacio suficiente para subir el archivo
     * @param directoryPath
     * @param totalSize
     * @return
     */
    public boolean freeSpaceCheck(String directoryPath, Long totalSize) {
        return false;
    }
}
