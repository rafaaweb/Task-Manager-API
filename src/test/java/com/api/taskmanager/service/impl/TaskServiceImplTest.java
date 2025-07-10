package com.api.taskmanager.service.impl;

import com.api.taskmanager.dto.TaskRequestDTO;
import com.api.taskmanager.dto.TaskResponseDTO;
import com.api.taskmanager.dto.TaskStatusUpdateDTO;
import com.api.taskmanager.exception.TaskNotFoundException;
import com.api.taskmanager.model.Status;
import com.api.taskmanager.model.Task;
import com.api.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskServiceImpl service;

    private Task task;

    @BeforeEach
    void setup(){
        task = new Task();
        task.setId(1L);
        task.setTitle("Test");
        task.setDescription("Description test");
        task.setDueDate(LocalDate.now().plusDays(2));
        task.setStatus(Status.IN_PROGRESS);
    }

    @Test
    void shouldReturnAllTasksPaginated(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(task));

        when(repository.findAll(pageable)).thenReturn(taskPage);

        Page<TaskResponseDTO> result = service.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void shouldFindById(){
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponseDTO result = service.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowWhenNotFoundById(){
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void shouldCreateTask(){
        TaskRequestDTO dto = new TaskRequestDTO("Title", "Description test", Status.IN_PROGRESS, LocalDate.now());
        when(repository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponseDTO response = service.create(dto);

        assertThat(response.title()).isEqualTo(dto.title());
        assertThat(response.description()).isEqualTo(dto.description());
        assertThat(response.dueDate()).isEqualTo(dto.dueDate());
        verify(repository).save(any(Task.class));
    }

    @Test
    void shouldUpdateTask(){
        TaskRequestDTO dto = new TaskRequestDTO("Updated title", "Updated description", Status.DONE, LocalDate.now().plusDays(5));

        Task existing = new Task();
        existing.setId(2L);
        existing.setTitle("Old Title");
        existing.setDescription("Old Desc");
        existing.setStatus(Status.IN_PROGRESS);
        existing.setDueDate(LocalDate.now().plusDays(2));

        when(repository.findById(2L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponseDTO response = service.update(2L, dto);

        assertThat(response.title()).isEqualTo(dto.title());
        assertThat(response.description()).isEqualTo(dto.description());
        assertThat(response.status()).isEqualTo(dto.status());
        assertThat(response.dueDate()).isEqualTo(dto.dueDate());
        verify(repository).findById(2L);
        verify(repository).save(any(Task.class));
    }

    @Test
    void shouldUpdateStatus(){
        TaskStatusUpdateDTO dto = new TaskStatusUpdateDTO(Status.DONE);

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO response = service.updateStatus(1L, dto);

        assertThat(response.status()).isEqualTo(dto.status());
        verify(repository).findById(1L);
        verify(repository).save(task);
    }

    @Test
    void shouldDeleteTask(){
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        service.delete(1L);

        verify(repository).findById(1L);
        verify(repository).delete(task);
    }

    @Test
    void shouldReturnEmptyPageWhenNoTasks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> emptyPage = new PageImpl<>(List.of());

        when(repository.findAll(pageable)).thenReturn(emptyPage);

        Page<TaskResponseDTO> result = service.findAll(pageable);

        assertThat(result.getContent()).isEmpty();
        verify(repository).findAll(pageable);
    }

    @Test
    void shouldThrowWhenUpdateNotFound() {
        TaskRequestDTO dto = new TaskRequestDTO("Title", "Description test", Status.IN_PROGRESS, LocalDate.now());
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, dto))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUpdateStatusNotFound() {
        TaskStatusUpdateDTO dto = new TaskStatusUpdateDTO(Status.DONE);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(99L, dto))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).delete(any());
    }

    @Test
    void shouldUpdateStatusSuccessfully() {
        TaskStatusUpdateDTO dto = new TaskStatusUpdateDTO(Status.DONE);

        Task existing = new Task();
        existing.setId(1L);
        existing.setStatus(Status.IN_PROGRESS);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        TaskResponseDTO response = service.updateStatus(1L, dto);

        assertThat(response.status()).isEqualTo(Status.DONE);
        verify(repository).save(existing);
    }

}
