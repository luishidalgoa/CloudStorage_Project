package net.ddns.levelcloud.music.Features.Download.models.DTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DownloadRequestDTOTest {

    @Test
    void testDeserializeDownloadRequestDTO() throws Exception {
        // JSON de ejemplo
        String json = """
        {
          "downloadType": "Local",
          "data": {
            "externalUrl": "https://www.youtube.com/watch?v=QH2_TGUlwu4",
            "DirectoryPath": "/new/folder"
          }
        }
        """;

        // Crear el ObjectMapper de Jackson
        ObjectMapper objectMapper = new ObjectMapper();

        // Deserializar el JSON en un objeto DownloadRequestDTO
        DownloadRequestDTO request = objectMapper.readValue(json, DownloadRequestDTO.class);
        System.out.println(request);
        // Validaciones
        assertNotNull(request);
        assertNotNull(request.getData());
        assertEquals("Local", request.getDownloadType());
        assertEquals("https://www.youtube.com/watch?v=QH2_TGUlwu4", request.getData().getExternalUrl());
        assertEquals("/new/folder", request.getData().getDirectoryPath());
    }
}