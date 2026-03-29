package com.github.vityan55.musicapp.web.controller.minio;

import com.github.vityan55.musicapp.web.track.dto.CreateTrackRequest;
import com.github.vityan55.musicapp.web.track.dto.CreateTrackUploadUrlRequest;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TrackMinioTest extends AbstractMinioWebTest {

    @Test
    void fullTrackFlow_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        var uploadRequest = new CreateTrackUploadUrlRequest("test.mp3", 1000L);

        var response = mockMvc.perform(post("/tracks/upload-url")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(uploadRequest)))
                .andExpect(status().isOk())
                .andReturn();

        var json = objectMapper.readTree(response.getResponse().getContentAsString());

        String fileKey = json.get("fileKey").asText();
        String url = json.get("url").asText();

        uploadFile(url);

        var createRequest = new CreateTrackRequest(
                "Test Track",
                fileKey,
                null,
                null
        );

        mockMvc.perform(post("/tracks")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Track"));

        mockMvc.perform(get("/tracks/1/stream")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url", notNullValue()));
    }

    private void uploadFile(String presignedUrl) throws Exception {
        URL url = new URL(presignedUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");

        conn.getOutputStream().write("fake-audio".getBytes());

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Upload failed");
        }
    }
}