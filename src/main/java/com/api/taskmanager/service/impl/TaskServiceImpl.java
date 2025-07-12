package com.api.taskmanager.service.impl;

import com.api.taskmanager.dto.TaskRequestDTO;
import com.api.taskmanager.dto.TaskResponseDTO;
import com.api.taskmanager.dto.TaskStatusUpdateDTO;
import com.api.taskmanager.exception.TaskNotFoundException;
import com.api.taskmanager.model.Status;
import com.api.taskmanager.model.Task;
import com.api.taskmanager.repository.TaskRepository;
import com.api.taskmanager.service.TaskService;
import com.api.taskmanager.state.TaskStateMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskStateMachine state;

    @Override
    public Page<TaskResponseDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    @Override
    @Cacheable(value = "taskById", key = "#id")
    public TaskResponseDTO findById(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return toResponseDTO(task);
    }

    @Override
    public TaskResponseDTO create(TaskRequestDTO dto) {
        Task task = toEntity(dto);
        task.setStatus(Status.PENDING);
        Task saved = repository.save(task);
        return toResponseDTO(saved);
    }

    @Override
    @CachePut(value = "taskById", key = "#id")
    public TaskResponseDTO update(Long id, TaskRequestDTO dto) {
        Task existing = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        existing.setTitle(dto.title());
        existing.setDescription(dto.description());
        existing.setDueDate(dto.dueDate());

        Task updated = repository.save(existing);
        return toResponseDTO(updated);
    }

    @Override
    @CacheEvict(value = "taskById", key = "#id")
    public void delete(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        repository.delete(task);
    }

    @CachePut(value = "taskById", key = "#id")
    public TaskResponseDTO done(Long id){
        var task = repository.findById(id)
                        .orElseThrow(() -> new TaskNotFoundException(id));
        state.done(task);
        return toResponseDTO(repository.save(task));
    }

    @CachePut(value = "taskById", key = "#id")
    public TaskResponseDTO start(Long id){
        var task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        state.start(task);
        return toResponseDTO(repository.save(task));
    }

    @CachePut(value = "taskById", key = "#id")
    public TaskResponseDTO cancel(Long id){
        var task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        state.cancel(task);
        return toResponseDTO(repository.save(task));
    }

    private TaskResponseDTO toResponseDTO(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate()
        );
    }

    private Task toEntity(TaskRequestDTO dto) {
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setDueDate(dto.dueDate());
        return task;
    }
}
