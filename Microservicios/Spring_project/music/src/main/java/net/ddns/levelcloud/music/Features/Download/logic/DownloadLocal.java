package net.ddns.levelcloud.music.Features.Download.logic;

import net.ddns.levelcloud.music.Features.Download.controllers.DownloadProgressController;
import net.ddns.levelcloud.music.Features.Download.logic.abs.AbstractDownloadStrategy;
import net.ddns.levelcloud.music.Features.Download.models.DTO.DownloadRequestDTO;
import net.ddns.levelcloud.music.Features.Download.models.DTO.LocalUploadDTO;
import net.ddns.levelcloud.music.util.ZipFile;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class DownloadLocal extends AbstractDownloadStrategy<LocalUploadDTO> {

    public DownloadLocal(DownloadProgressController downloadProgressController) {
        super(downloadProgressController);
    }

    @Override
    public LocalUploadDTO upload(String id) {
        String tempPath = System.getProperty("java.io.tmpdir") + File.separator + "MusicDownload" + File.separator + id;
        try {
            File root = new File(tempPath);

            if (!root.exists() || root.listFiles() == null)
                throw new IllegalArgumentException("No existe el ID de descarga en el servidor o el directorio está vacío.");

            File[] children = Objects.requireNonNull(root.listFiles());

            if (children.length == 1) {
                // Si hay un único archivo, devuelve el archivo directamente
                return this.getOnlyFile(root, children[0]);
            } else {
                // Si hay varios archivos, devuelve un archivo ZIP comprimido
                return this.getZipFile(root);
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("No se encontraron archivos en el directorio de descarga.");
        }
    }

    @Override
    public LocalUploadDTO getOnlyFile(File root, File file) {
        try {
            // Abrimos el FileInputStream
            FileInputStream fileInputStream = new FileInputStream(file);

            // Creamos el InputStreamResource para enviar al cliente
            InputStreamResource resource = new InputStreamResource(fileInputStream);

            //eliminamos el directorio
            super.cleanMemory(root);

            return LocalUploadDTO.builder()
                    .fileChildrenName(file.getName())
                    .resource(resource)
                    .build();

        } catch (FileNotFoundException e) {
            throw new RuntimeException("No se encontró el archivo: " + file.getPath(), e);
        }
    }

    @Override
    public LocalUploadDTO getZipFile(File root) {
        try {
            InputStreamResource zip=ZipFile.zip(root);

            if (!super.cleanMemory(root))
                LoggerFactory.getLogger(DownloadLocal.class).error("No se pudo eliminar el directorio temporal.");

            return LocalUploadDTO.builder()
                    .fileChildrenName(root.getName()+".zip")
                    .resource(zip)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Bytes totales que se descargara el usuario
     * @param directory directorio temporal donde se ubican los ficheros
     * @return tamaño total en bytes
     */
    @Override
    public Long getTotalSize(File directory) {
        if (!directory.exists()) {
            throw new IllegalArgumentException("El directorio no existe: " + directory.getAbsolutePath());
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("No es un directorio: " + directory.getAbsolutePath());
        }

        long size = 0;
        File[] files = directory.listFiles();

        if (files != null) { // Puede ser null si no se tienen permisos
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length(); // Tamaño del archivo
                } else if (file.isDirectory()) {
                    size += this.getTotalSize(file); // Llamada recursiva
                }
            }
        }
        return size;
    }

    @Override
    public void endDownload(DownloadRequestDTO request, String directoryPath) {

    }

    @Override
    public boolean cancelProcess(String id) {
        return super.cancelProcess(id);
    }




}

