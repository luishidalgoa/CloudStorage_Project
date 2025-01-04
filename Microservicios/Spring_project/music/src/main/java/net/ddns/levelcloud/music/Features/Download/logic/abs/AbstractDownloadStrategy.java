package net.ddns.levelcloud.music.Features.Download.logic.abs;

import lombok.Data;
import net.ddns.levelcloud.music.Features.Download.controllers.DownloadProgressController;
import net.ddns.levelcloud.music.Features.Download.models.DTO.DownloadRequestDTO;
import net.ddns.levelcloud.music.Features.Download.models.DTO.ProgressDto;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public abstract class AbstractDownloadStrategy<T> {
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final DownloadProgressController downloadProgressController;


    protected AbstractDownloadStrategy(DownloadProgressController downloadProgressController) {
        this.downloadProgressController = downloadProgressController;
    }

    /**
     * Metodo principal de la clase abstracta. Este metodo se encargara de ejecutar la descarga de la musica
     * ademas de actualizar el progreso de la descarga y finalmente calcular el tamaño de los ficheros descargados
     * @param request objeto con informacion esencial de la descarga
     */
    public void execute(DownloadRequestDTO request){
        String tempDir = System.getProperty("java.io.tmpdir");
        // Directorio raiz donde se descargaran todas las sesiones de descargas
        String rootPath = tempDir + File.separator + "MusicDownload";
        // Directorio donde se guardaran los ficheros de la sesion actual
        String directoryPath = rootPath + File.separator + request.getId();
        //creamos el directorio MusicDownload
        {
            File root = new File(rootPath);
            if (!root.exists())
                root.mkdirs();
        }
        // Creamos el directorio temporal de la descarga
        {
            File directory = new File(directoryPath);
            directory.mkdirs();
        }
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "yt-dlp",
                    "-f", "m4a",
                    "-o", directoryPath + File.separator + "%(title)s.%(ext)s",
                    "--embed-thumbnail",
                    "--add-metadata",
                    request.getData().getExternalUrl()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            executorService.submit(() -> {

                downloadProgressController.setProcess(request.getId(), process,request);

                handleDownloadProcess(process,request,directoryPath);
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @deprecated 
     * @param process
     * @param request
     * @param directoryPath
     */
    private void handleDownloadProcess(Process process,DownloadRequestDTO request,String directoryPath){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            double progressFile = 0;
            boolean isStarted=false;
            boolean lastComplete=false;
            ProgressDto progressDto = downloadProgressController.getProgress(request.getId());
            while ((line = reader.readLine()) != null && progressDto.getProgress()<100) {
                double tempProgress=extractProgressFromLine(line); //nos aseguraremos de que despues del ultimo 100% tengamos un -1
                if (lastComplete && tempProgress==100)
                    tempProgress=-1;
                if (!isStarted) {
                    if(tempProgress>-1){
                        isStarted=true;
                        progressFile=tempProgress;
                        handleFileProgress(progressDto,progressFile);
                    }
                    //si el proceso ya existia pero ahora devuelve -1, significa que ha terminado 1 de los ficheros
                }else {
                    if (tempProgress ==-1) {
                        if (progressFile>0)
                            progressDto.setCurrentIndex(progressDto.getCurrentIndex()+1);
                        progressFile = 0;
                    }else {
                        progressFile=tempProgress;
                        handleFileProgress(progressDto,progressFile);
                    }
                    //significa que el progreso del fichero actual ha empezado o continua);
                }
                if (progressFile>=100){
                    lastComplete=true;
                    progressFile=0;
                }

            }

            //calculamos el total del tamaño de la descarga para guardarlo en la sesion
            calculateTotalSize(request,directoryPath);

            endDownload(request,directoryPath);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Este metodo se encargara de comprobar si el proceso de descarga ha cambiado de fichero
     * @deprecated
     */
    private void handleFileProgress(ProgressDto progressDto,double progressFile){
        // significa que ha finalizado un fichero y salta al siguiente
        if (progressDto.getCurrentIndex() < progressDto.getRequest().getData().getTotalFiles()-1) {
            downloadProgressController.updateProgress(progressDto.getRequest().getId(), progressFile);
            if (progressFile>=100)
                progressDto.setCurrentIndex(progressDto.getCurrentIndex()+1);
        }else { //significa que va por el ultimo fichero
            downloadProgressController.updateProgress(progressDto.getRequest().getId(), progressFile);
        }

    }

    /**
     * Este metodo tiene el objetivo de almacenar en el progressMap el tamaño total de la descarga
     * @param request
     */
    private void calculateTotalSize(DownloadRequestDTO request,String directoryPath){

        ProgressDto progress=downloadProgressController.getProgress(request.getId());
        progress.getRequest().getData().setDownloadSyze(getTotalSize(new File(directoryPath)));
        downloadProgressController.setProgressDto(request.getId(),progress);
    }

    private double extractProgressFromLine(String line) {
        if (line.contains("[download]") && !line.contains("[download] Destination:")) {
            String regex = "\\d+\\.\\d+%|\\d+%"; // Captura valores como 100%, 99.9%, etc.
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) { // Si hay una coincidencia
                String match = matcher.group(); // Obtiene el valor con %
                double progress = Double.parseDouble(match.replace("%", ""));
                return progress; // Retorna el valor como entero
            }
        }
        return -1; // Retorna 0 si no se cumple la condición
    }

    public abstract T upload(String id);

    public abstract T getOnlyFile(File root,File file);

    public abstract T getZipFile(File root);

    /**
     * Metodo que calculara el tamaño total de la descarga del directorio donde se han descargado los ficheros durante la sesion
     * @param directory
     * @return
     */
    public abstract Long getTotalSize(File directory);

    /**
     * usado por el metodo execute. Es un metodo abstracto que ejecutara la logica que el usuario desee cuando
     * la descarga de la sesión haya finalizado
     * @param request objeto con informacion esencial de la descarga
     * @param directoryPath directorio donde se han descargado los ficheros
     */
    public abstract void endDownload(DownloadRequestDTO request,String directoryPath);

    private boolean cancelHandle(String id){
        if (downloadProgressController.getProgress(id)!=null){
            this.downloadProgressController.getProgress(id).getProcess().destroy();
            this.downloadProgressController.remove(id);
            this.cancelProcess(id);
            return true;
        }
        return false;
    }

    /**
     * Lo que sucedera despues de cancelarse el proceso de descarga. Por defecto no hace falta encargarse de destruir el
     * proceso ni borrarlo de la memoria interna ya que el metodo cancelHandle se encargara de ello. Este metodo sera llamado
     * despues de que se haya limpiado la memoria interna
     * @param id id de la descarga
     * @return true si se ha cancelado correctamente
     */
    public abstract boolean cancelProcess(String id);
}
