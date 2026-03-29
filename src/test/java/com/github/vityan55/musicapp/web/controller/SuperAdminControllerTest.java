package com.github.vityan55.musicapp.web.controller;

import com.github.vityan55.musicapp.web.superadmin.dto.UpdateRoleRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SuperAdminControllerTest extends AbstractWebTest{
    @Test
    void updateRole_success() throws Exception {
        String token = loginAndGetToken("superamin@mail.com", "Pass1234");

        var request = new UpdateRoleRequest("ADMIN");

        mockMvc.perform(patch("/super-admin/users/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateRole_userNotFound() throws Exception {
        String token = loginAndGetToken("superamin@mail.com", "Pass1234");

        var request = new UpdateRoleRequest("ADMIN");

        mockMvc.perform(patch("/super-admin/users/999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRole_forbiddenSuperAdmin() throws Exception {
        String token = loginAndGetToken("superamin@mail.com", "Pass1234");

        var request = new UpdateRoleRequest("USER");

        mockMvc.perform(patch("/super-admin/users/5")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateRole_invalidRole() throws Exception {
        String token = loginAndGetToken("superamin@mail.com", "Pass1234");

        var request = new UpdateRoleRequest("INVALID_ROLE");

        mockMvc.perform(patch("/super-admin/users/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRole_unauthorized_user() throws Exception {
        String token = loginAndGetToken("user1@mail.com", "Pass1234");

        var request = new UpdateRoleRequest("ADMIN");

        mockMvc.perform(patch("/super-admin/users/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
