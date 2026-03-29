package com.github.vityan55.musicapp.web.controller;

import com.github.vityan55.musicapp.web.artist.dto.CreateArtistRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AdminControllerTest extends AbstractWebTest {

    @Test
    void createArtist_success() throws Exception {
        String token = loginAndGetToken("admin@mail.com", "Pass1234");

        var request = new CreateArtistRequest(3L, "New Artist", "Description");

        mockMvc.perform(post("/admin/artists")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artistName").value("New Artist"))
                .andExpect(jsonPath("$.userId").value(3));
    }

    @Test
    void createArtist_unauthorized_user() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        var request = new CreateArtistRequest(3L, "New Artist", "Description");

        mockMvc.perform(post("/admin/artists")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createArtist_userNotFound() throws Exception {
        String token = loginAndGetToken("admin@mail.com", "Pass1234");

        var request = new CreateArtistRequest(999L, "New Artist", "Description");

        mockMvc.perform(post("/admin/artists")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}