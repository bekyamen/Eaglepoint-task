package com.citybus.platform.modules.task.controller;

import com.citybus.platform.config.security.SecurityConfig;
import com.citybus.platform.modules.auth.security.JwtFilter;
import com.citybus.platform.modules.task.dto.TaskDto;
import com.citybus.platform.modules.task.service.TaskService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void listShouldReturnUnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"PASSENGER"})
    void listShouldReturnForbiddenForPassengerRole() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"DISPATCHER"})
    void listShouldReturnPayloadForDispatcherRole() throws Exception {
        when(taskService.listTasks(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getShouldReturnOneTaskForAdminRole() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(taskService.getTask(taskId)).thenReturn(new TaskDto(taskId, "CHECKIN", "PENDING", null));

        mockMvc.perform(get("/api/v1/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(taskId.toString()))
                .andExpect(jsonPath("$.data.type").value("CHECKIN"));
    }
}
