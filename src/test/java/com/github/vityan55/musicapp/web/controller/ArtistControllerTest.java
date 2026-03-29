package com.github.vityan55.musicapp.web.controller;

import com.github.vityan55.musicapp.web.artist.dto.UpdateArtistRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ArtistControllerTest extends AbstractWebTest {

    @Test
    void getArtistById_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(get("/artists/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artistId").value(1))
                .andExpect(jsonPath("$.artistName").value("Artist One"));
    }

    @Test
    void getArtistById_notFound() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(get("/artists/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Artist not found"));
    }

    @Test
    void updateArtist_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        UpdateArtistRequest request = new UpdateArtistRequest("Updated Name", "Updated Desc");

        mockMvc.perform(patch("/artists/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artistName").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated Desc"));
    }

    @Test
    void updateArtist_forbidden() throws Exception {
        String token = loginAndGetToken("user2@mail.com", "Pass1234");

        UpdateArtistRequest request = new UpdateArtistRequest("Hack Name", "Hack Desc");

        mockMvc.perform(patch("/artists/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("User doesn't have permissions to update this artist"));
    }

    @Test
    void updateArtist_notFound() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        UpdateArtistRequest request = new UpdateArtistRequest("Name", "Desc");

        mockMvc.perform(patch("/artists/999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Artist not found"));
    }

    @Test
    void deleteArtist_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(delete("/artists/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteArtist_forbidden() throws Exception {
        String token = loginAndGetToken("user2@mail.com", "Pass1234");

        mockMvc.perform(delete("/artists/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("User doesn't have permissions to delete this artist"));
    }

    @Test
    void deleteArtist_notFound() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(delete("/artists/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Artist not found"));
    }

    @Test
    void getAllArtists_pagination_firstPage() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(get("/artists")
                        .param("pageSize", "1")
                        .param("pageNumber", "0")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data[0].artistId", notNullValue()));
    }

    @Test
    void getAllArtists_pagination_secondPage() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(get("/artists")
                        .param("pageSize", "1")
                        .param("pageNumber", "1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data[0].artistId", notNullValue()));
    }

    @Test
    void getAllArtists_pagination_emptyPage() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(get("/artists")
                        .param("pageSize", "10")
                        .param("pageNumber", "9990")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)));
    }

    @Test
    void filterArtists_byName() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(get("/artists/filter")
                        .param("text", "Artist One")
                        .param("pageSize", "10")
                        .param("pageNumber", "0")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", not(empty())))
                .andExpect(jsonPath("$.data[0].artistName").value("Artist One"));
    }

    @Test
    void filterArtists_noMatches() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(get("/artists/filter")
                        .param("text", "Nonexistent Artist")
                        .param("pageSize", "10")
                        .param("pageNumber", "0")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }
}