package com.github.vityan55.musicapp.web.controller;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LikesControllerTest extends AbstractWebTest {

    @Test
    void getLikedArtists_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(get("/likes/artists")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    void getLikedTracks_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(get("/likes/tracks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countOfLikes", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.tracks", notNullValue()));
    }

    @Test
    void likeArtist_success() throws Exception {
        String token = loginAndGetToken("user3@mail.com", "Pass1234");

        mockMvc.perform(post("/likes/artists/2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
    }

    @Test
    void likeArtist_userNotFound() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(post("/likes/artists/9999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Artist not found"));
    }

    @Test
    void likeArtist_alreadyLiked() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(post("/likes/artists/2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Like already exists"));
    }

    @Test
    void likeTrack_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(post("/likes/tracks/2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
    }

    @Test
    void likeTrack_trackNotFound() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(post("/likes/tracks/9999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Track not found"));
    }

    @Test
    void likeTrack_alreadyLiked() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(post("/likes/tracks/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Like already exists"));
    }

    @Test
    void deleteLikeArtist_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(delete("/likes/artists/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteLikeTrack_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(delete("/likes/tracks/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}