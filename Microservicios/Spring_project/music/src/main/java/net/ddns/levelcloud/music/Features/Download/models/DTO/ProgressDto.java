package net.ddns.levelcloud.music.Features.Download.models.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class ProgressDto{
    private double progress=0;
    private Integer currentIndex=0;
    private Process process;
    private DownloadRequestDTO request;

    public ProgressDto(Process process, DownloadRequestDTO request){
        this.process = process;
        this.request = request;
    }
    /**
     * Algoritmo para conocer cual es el progreso actual de la descarga en base al total de ficheros
     * @param progressFile
     */
    public double calculateProgress(double progressFile) {
        double totalFiles= request.getData().getTotalFiles();

        double progressPerFile = 100 / totalFiles; // Porcentaje maximo por archivo
        this.progress = (progressPerFile * currentIndex) + (progressPerFile * progressFile / 100);
        return this.progress;
    }
}