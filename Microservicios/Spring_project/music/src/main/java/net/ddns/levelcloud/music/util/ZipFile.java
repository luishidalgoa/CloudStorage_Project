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

        File[] files = directoryFile.listFiles();
        long totalSize = 0;
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    totalSize += file.length();
                }
            }
        }

        File tempZip = File.createTempFile("temp-", ".zip");

        try (FileOutputStream fos = new FileOutputStream(tempZip);
             ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))) {

            byte[] buffer = new byte[8192];
            long totalRead = 0;
            long logStep = totalSize / 20;
            if (logStep == 0) logStep = 1024 * 1024;
            long nextLogThreshold = logStep;

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        try (FileInputStream fis = new FileInputStream(file)) {
                            zos.putNextEntry(new ZipEntry(file.getName()));

                            int len;
                            while ((len = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                                totalRead += len;

                                if (totalRead >= nextLogThreshold) {
                                    logProgress(totalRead, totalSize, nextLogThreshold, logStep);
                                    nextLogThreshold += logStep;
                                }
                            }
                            zos.closeEntry();
                        }
                    }
                }
            }
        }

        FileInputStream fis = new FileInputStream(tempZip);
        tempZip.deleteOnExit();

        return new InputStreamResource(fis);
    }

    private static void logProgress(long totalRead, long totalSize, long nextLogThreshold, long logStep) {
        if (totalRead >= nextLogThreshold) {
            int progressPercent = (int)((totalRead * 100) / totalSize);
            System.out.println("Progreso de compresión: " + progressPercent + "%");
        }
    }




    /**
     * Esta función sirve para eliminar todos los archivos que no sean el zip
     * @param directoryFile
     * @return
     */
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
