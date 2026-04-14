package com.citybus.platform.modules.task.controller;

import com.citybus.platform.config.security.SecurityConfig;
import com.citybus.platform.modules.auth.security.JwtFilter;
import com.citybus.platform.modules.task.dto.TaskTransitionRequest;
import com.citybus.platform.modules.task.dto.TaskDto;
import com.citybus.platform.modules.task.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    @WithMockUser(roles = {"DISPATCHER"})
    void approveShouldTransitionTask() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(taskService.transitionTask(
                org.mockito.ArgumentMatchers.eq(taskId),
                org.mockito.ArgumentMatchers.any(TaskTransitionRequest.class),
                org.mockito.ArgumentMatchers.any(UUID.class),
                org.mockito.ArgumentMatchers.eq("trace-1")))
                .thenReturn(new TaskDto(taskId, "CHECKIN", "APPROVED", null));

        mockMvc.perform(post("/api/v1/tasks/{id}/approve", taskId)
                        .header("X-Trace-Id", "trace-1")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskTransitionRequest("ignored", null, "ok"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }

    @Test
    @WithMockUser(roles = {"DISPATCHER"})
    void branchShouldReturnConflictForInvalidTransition() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(taskService.transitionTask(
                org.mockito.ArgumentMatchers.eq(taskId),
                org.mockito.ArgumentMatchers.any(TaskTransitionRequest.class),
                org.mockito.ArgumentMatchers.any(UUID.class),
                org.mockito.ArgumentMatchers.isNull()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Transition action is not allowed from current state"));

        mockMvc.perform(post("/api/v1/tasks/{id}/branch", taskId)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TaskTransitionRequest("branch", "TARGET", "reason"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("CONFLICT"));
    }
}
