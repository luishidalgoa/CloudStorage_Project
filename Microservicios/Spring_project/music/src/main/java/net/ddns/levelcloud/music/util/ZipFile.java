package net.ddns.levelcloud.music.util;

import org.springframework.core.io.InputStreamResource;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFile {

    /**
     * Recibimos el nombre del directorio del cual queremos crear un archivo zip
     * @param directoryFile
     * @return
     */
    public static InputStreamResource zip(File directoryFile) throws IOException {
        if (!directoryFile.isDirectory()) {
            throw new IllegalArgumentException("El parámetro debe ser un directorio.");
        }

        // Crear un stream en memoria para el archivo ZIP
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            // Comprimir los archivos del directorio
            File[] files = directoryFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        try (FileInputStream fis = new FileInputStream(file)) {
                            // Crear una entrada ZIP para cada archivo
                            zos.putNextEntry(new ZipEntry(file.getName()));
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }
                            zos.closeEntry();
                        }
                    }
                }
            }
        }

        // Retornar el recurso InputStreamResource
        return new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
    }

    public static boolean deleteOtherFilesDirectory(File directoryFile) {
        File[] filesToDelete = directoryFile.listFiles();
        if (filesToDelete != null) {
            for (File file : filesToDelete) {
                if (file.isFile() && !file.getName().equals(directoryFile.getName())) {
                    if (!file.delete()) {
                        System.err.println("No se pudo eliminar el archivo: " + file.getName());
                    }
                }
            }
        }
        return directoryFile.exists();
    }
}
