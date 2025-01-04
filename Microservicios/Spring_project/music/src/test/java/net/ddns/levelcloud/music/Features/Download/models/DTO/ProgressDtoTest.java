package net.ddns.levelcloud.music.Features.Download.models.DTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProgressDtoTest {

    private ProgressDto progressDto;
    private DownloadRequestDTO.DownloadRequestMusicData musicData;
    private DownloadRequestDTO request;

    @BeforeEach
    void setUp() {
        // Configuración base
        musicData = new DownloadRequestDTO.DownloadRequestMusicData("http://example.com", "/path/to/download", 1024L, 12);
        request = new DownloadRequestDTO(null, musicData, null);
        progressDto = new ProgressDto(null, request); // Reemplazar con una implementación válida de Process si es necesario
    }

    @Test
    void testProgressMinimum() {
        // Caso mínimo: totalFiles = 1, currentIndex = 1, progressFile = 0
        musicData.setTotalFiles(1);
        progressDto.setCurrentIndex(0);
        assertEquals(100,progressDto.calculateProgress(100) , 0.01, "El progreso mínimo debe ser 0%");
    }

    @Test
    void testProgressNormal() {
        // Caso normal: totalFiles = 12, currentIndex = 3, progressFile = 70
        musicData.setTotalFiles(12);
        progressDto.setCurrentIndex(3);

        assertEquals(30.83333333333333, progressDto.calculateProgress(70), 1, "El progreso normal debe coincidir");
    }

    @Test
    void testProgressMaximum() {
        // Caso máximo: totalFiles = 12, currentIndex = 12, progressFile = 100
        musicData.setTotalFiles(12);
        progressDto.setCurrentIndex(11);

        assertEquals(100, progressDto.calculateProgress(100) , 1, "El progreso máximo debe ser 100%");
    }
}
// 100 / 12 = 8,33 progreso por cada fichero || Min= 100 * 1 = 100
// 8,333333333333333 * 3 = 25 || Max= 8,333333333333333 * 11 = 91,66666666666666 || Min= 100 * 1 = 100
// (8,333333333333333 * 70) / 100 = 5,833333333333333 || Max= (8,333333333333333 * 100 / 100) = 8,333333333333333 || Min= 100 * 100 /100 = 100
// 25 + 5,833333333333333 = 30,83333333333333 || Max= 91,66666666666666 + 8,333333333333333 = 100 || Min= 100 + 0 = 100