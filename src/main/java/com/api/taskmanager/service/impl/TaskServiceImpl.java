package com.api.taskmanager.service.impl;

import com.api.taskmanager.dto.TaskRequestDTO;
import com.api.taskmanager.dto.TaskResponseDTO;
import com.api.taskmanager.dto.TaskStatusUpdateDTO;
import com.api.taskmanager.exception.TaskNotFoundException;
import com.api.taskmanager.model.Task;
import com.api.taskmanager.repository.TaskRepository;
import com.api.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

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
    @Caching(evict = {
            @CacheEvict(value = "tasks", allEntries = true)
    })
    public TaskResponseDTO create(TaskRequestDTO dto) {
        Task task = toEntity(dto);
        Task saved = repository.save(task);
        return toResponseDTO(saved);
    }

    @Override
    @Caching(
            put = @CachePut(value = "task", key = "#id"),
            evict = @CacheEvict(value = "tasks", allEntries = true)
    )
    public TaskResponseDTO update(Long id, TaskRequestDTO dto) {
        Task existing = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        existing.setTitle(dto.title());
        existing.setDescription(dto.description());
        existing.setStatus(dto.status());
        existing.setDueDate(dto.dueDate());

        Task updated = repository.save(existing);
        return toResponseDTO(updated);
    }

    @Override
    @Caching(
            put = @CachePut(value = "task", key = "#id"),
            evict = @CacheEvict(value = "tasks", allEntries = true)
    )
    public TaskResponseDTO updateStatus(Long id, TaskStatusUpdateDTO dto){
        Task existing = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        existing.setStatus(dto.status());

        Task updated = repository.save(existing);
        return toResponseDTO(updated);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "task", key = "#id"),
            @CacheEvict(value = "tasks", allEntries = true)
    })
    public void delete(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        repository.delete(task);
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
        task.setStatus(dto.status());
        task.setDueDate(dto.dueDate());
        return task;
    }
}
