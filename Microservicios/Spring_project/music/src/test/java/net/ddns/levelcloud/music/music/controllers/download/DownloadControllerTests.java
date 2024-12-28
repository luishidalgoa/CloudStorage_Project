package net.ddns.levelcloud.music.music.controllers.download;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.levelcloud.music.music.controllers.prueba;
import net.ddns.levelcloud.music.music.models.DTO.Download.DownloadRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {DownloadController.class, prueba.class})
class DownloadControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void uploadOffline() throws Exception {
		// Body para la solicitud
		DownloadRequestDTO.DownloadDataDTO downloadDataDTO = DownloadRequestDTO.DownloadDataDTO.builder()
				.externalUrl("https://www.youtube.com/watch?v=QH2_TGUlwu4")
				.build();
		String requestBody = DownloadRequestDTO.builder()
				.data(downloadDataDTO)
				.build().toString();

		// Ejecutar la solicitud
		mockMvc.perform(post("/api/music/download/request")
						.content(requestBody)
						.contentType(MediaType.APPLICATION_JSON)
						.header("DownloadOption", "Local"))
				.andExpect(status().isOk())
				.andExpect(content().string("\"downloadId\":"));
	}

	@Value("${test.api.nextcloud.username}")
	private String nextcloudUsername;
	@Value("${test.api.nextcloud.password}")
	private String nextcloudPassword;

	@Test
	void uploadNextcloud() throws Exception {
		String username = nextcloudUsername;
		String password = nextcloudPassword;
		String basicAuth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());


		// Body para la solicitud
		DownloadRequestDTO.DownloadDataDTO downloadDataDTO = DownloadRequestDTO.DownloadDataDTO.builder()
				.externalUrl("https://www.youtube.com/watch?v=QH2_TGUlwu4")
				.DirectoryPath("/new/folder")
				.build();
		String requestBody = DownloadRequestDTO.builder()
				.data(downloadDataDTO)
				.build().toString();

		// Ejecutar la solicitud
		mockMvc.perform(post("/api/music/download/request")
						.content(requestBody)
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", basicAuth)
						.header("DownloadOption", "Local"))
				.andExpect(status().isOk())
				.andExpect(content().string("\"downloadId\":"));
	}

	@Test
	void testPruebaEndpoint() throws Exception {
		// Construir el encabezado Authorization (Base64)
		String username = nextcloudUsername;
		String password = nextcloudPassword;
		String basicAuth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

		// Ejecutar la solicitud
		mockMvc.perform(get("/prueba/")
						.header("Authorization", basicAuth))
				.andExpect(status().isOk())
				.andExpect(content().string("Hola mundo"));
	}

}
