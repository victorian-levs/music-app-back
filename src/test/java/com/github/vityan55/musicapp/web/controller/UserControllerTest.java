package com.github.vityan55.musicapp.web.controller;

import com.github.vityan55.musicapp.web.user.dto.*;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends AbstractWebTest {

    @Test
    void getProfile_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        mockMvc.perform(get("/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    void getProfile_unauthorized() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProfile_notFound() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");
        userRepository.deleteById(1L);

        mockMvc.perform(get("/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePersonal_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");
        var request = new UpdatePersonalRequest("newUsername");

        mockMvc.perform(patch("/profile/personal")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newUsername"));

        assertThat(userRepository.findById(1L).get().getUsername()).isEqualTo("newUsername");
    }

    @Test
    void updatePersonal_userNotFound() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");
        userRepository.deleteById(1L);
        var request = new UpdatePersonalRequest("newUsername");

        mockMvc.perform(patch("/profile/personal")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePassword_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");
        var request = new UpdatePasswordRequest("NewPass123", "Pass1234");

        var result = mockMvc.perform(patch("/profile/credentials")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("refresh_token");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isNotEmpty();
    }

    @Test
    void updatePassword_userNotFound() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");
        userRepository.deleteById(1L);
        var request = new UpdatePasswordRequest("NewPass123", "Pass1234");

        mockMvc.perform(patch("/profile/credentials")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePassword_invalidOldPassword() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");
        var request = new UpdatePasswordRequest("NewPass123", "WrongOldPass");

        mockMvc.perform(patch("/profile/credentials")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updatePassword_sameAsOldPassword() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");
        var request = new UpdatePasswordRequest("Pass1234", "Pass1234");

        mockMvc.perform(patch("/profile/credentials")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProfile_success() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        var response = mockMvc.perform(delete("/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(userRepository.findById(1L)).isEmpty();
        String setCookie = response.getResponse().getHeader("Set-Cookie");
        assertThat(setCookie).contains("Max-Age=0");
    }

    @Test
    void deleteProfile_userNotFound() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");
        userRepository.deleteById(1L);

        mockMvc.perform(delete("/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteProfile_unauthorized() throws Exception {
        mockMvc.perform(delete("/profile"))
                .andExpect(status().isUnauthorized());
    }
}