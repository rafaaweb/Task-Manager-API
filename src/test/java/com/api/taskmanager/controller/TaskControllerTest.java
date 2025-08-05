package com.api.taskmanager.controller;

import com.api.taskmanager.dto.TaskRequestDTO;
import com.api.taskmanager.dto.TaskResponseDTO;
import com.api.taskmanager.model.Status;
import com.api.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;



import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void shouldListTasksPaginated() throws Exception {
        TaskResponseDTO task = new TaskResponseDTO(
                1L,
                "Task 1",
                "Description task 1",
                Status.IN_PROGRESS,
                LocalDate.now().plusDays(1)
        );

        Page<TaskResponseDTO> page = new PageImpl<>(List.of(task), PageRequest.of(0, 20), 1);
        when(taskService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/tasks")
                        .param("page", "0")
                        .param("size", "20")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Task 1"))
                .andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS"));
    }

    @Test
    void shouldGetTaskById() throws Exception {
        TaskResponseDTO task = new TaskResponseDTO(
                1L,
                "Task 1",
                "Description task 1",
                Status.IN_PROGRESS,
                LocalDate.now().plusDays(1)
        );

        when(taskService.findById(1L)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/1")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldCreateTask() throws Exception {
        TaskRequestDTO requestDTO = new TaskRequestDTO(
                "New Task",
                "Description for task",
                LocalDate.now().plusDays(1)
        );

        TaskResponseDTO responseDTO = new TaskResponseDTO(
                1L,
                requestDTO.title(),
                requestDTO.description(),
                Status.IN_PROGRESS,
                requestDTO.dueDate()
        );

        when(taskService.create(any(TaskRequestDTO.class))).thenReturn(responseDTO);

        String jsonRequest = """
            {
              "title": "New Task",
              "description": "Description for task",
              "status": "IN_PROGRESS",
              "dueDate": "%s"
            }
            """.formatted(requestDTO.dueDate());

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .with(jwt()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("Description for task"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.dueDate").value(requestDTO.dueDate().toString()));
    }

    @Test
    void shouldUpdateTask() throws Exception {
        TaskRequestDTO requestDTO = new TaskRequestDTO(
                "Updated Task",
                "Updated description",
                LocalDate.now().plusDays(5)
        );

        TaskResponseDTO responseDTO = new TaskResponseDTO(
                1L,
                requestDTO.title(),
                requestDTO.description(),
                Status.IN_PROGRESS,
                requestDTO.dueDate()
        );

        when(taskService.update(any(Long.class), any(TaskRequestDTO.class))).thenReturn(responseDTO);

        String jsonRequest = """
            {
              "title": "Updated Task",
              "description": "Updated description",
              "dueDate": "%s"
            }
            """.formatted(requestDTO.dueDate());

        mockMvc.perform(patch("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        // No return needed for delete, only verify status 204
        mockMvc.perform(delete("/api/tasks/1")
                        .with(jwt()))
                .andExpect(status().isNoContent());
    }
}
