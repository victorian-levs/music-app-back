package com.github.vityan55.musicapp.web.controller;

import com.github.vityan55.musicapp.web.track.dto.UpdateTrackRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TrackControllerTest extends AbstractWebTest {

    @Test
    void updateTrack_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        UpdateTrackRequest request = new UpdateTrackRequest("Updated Track Name");

        mockMvc.perform(patch("/tracks/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Track Name"));
    }

    @Test
    void updateTrack_notFound() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        UpdateTrackRequest request = new UpdateTrackRequest("Updated Track Name");

        mockMvc.perform(patch("/tracks/999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Track not found"));
    }

    @Test
    void updateTrack_wrongArtist() throws Exception {
        String token = loginAndGetToken("user2@mail.com", "Pass1234");

        UpdateTrackRequest request = new UpdateTrackRequest("Hack Track");

        mockMvc.perform(patch("/tracks/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Track not found"));
    }

    @Test
    void deleteTrack_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(delete("/tracks/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTrack_notFound() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(delete("/tracks/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Track not found"));
    }

    @Test
    void deleteTrack_wrongArtist() throws Exception {
        String token = loginAndGetToken("user2@mail.com", "Pass1234");

        mockMvc.perform(delete("/tracks/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Track not found"));
    }

    @Test
    void getAllTracks_pagination() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(get("/tracks")
                        .param("pageSize", "2")
                        .param("pageNumber", "0")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.totalPages", greaterThan(0)));
    }

    @Test
    void filterTracks_noMatches() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(get("/tracks/search")
                        .param("text", "Nonexistent Track")
                        .param("pageSize", "10")
                        .param("pageNumber", "0")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void filterTracks_withText() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(get("/tracks/search")
                        .param("text", "Track One")
                        .param("pageSize", "10")
                        .param("pageNumber", "0")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", not(empty())));
    }
}