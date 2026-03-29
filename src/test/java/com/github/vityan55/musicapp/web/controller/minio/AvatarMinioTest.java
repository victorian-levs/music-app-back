package com.github.vityan55.musicapp.web.controller.minio;

import com.github.vityan55.musicapp.web.user.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AvatarMinioTest extends AbstractMinioWebTest {

    @Test
    void uploadAndConfirmAvatar_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        var uploadRequest = new CreateAvatarUploadUrlRequest("avatar.png", 1000L);

        var uploadResponse = mockMvc.perform(post("/profile/avatar/upload-url")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(uploadRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileKey", notNullValue()))
                .andExpect(jsonPath("$.url", notNullValue()))
                .andReturn();

        var json = objectMapper.readTree(uploadResponse.getResponse().getContentAsString());
        String fileKey = json.get("fileKey").asText();
        String presignedUrl = json.get("url").asText();

        uploadFile(presignedUrl, "fake-image-content");

        var confirmRequest = new ConfirmAvatarRequest(fileKey);

        mockMvc.perform(post("/profile/avatar/confirm")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(confirmRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url", notNullValue()));
    }

    private void uploadFile(String presignedUrl, String content) throws Exception {
        URL url = new URL(presignedUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("PUT");

        conn.getOutputStream().write(content.getBytes(StandardCharsets.UTF_8));

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Upload failed: " + responseCode);
        }
    }
}