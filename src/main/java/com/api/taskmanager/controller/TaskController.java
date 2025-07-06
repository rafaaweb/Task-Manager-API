package com.api.taskmanager.controller;

import com.api.taskmanager.dto.TaskRequestDTO;
import com.api.taskmanager.dto.TaskResponseDTO;
import com.api.taskmanager.dto.TaskStatusUpdateDTO;
import com.api.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    @GetMapping
    public Page<TaskResponseDTO> list(Pageable pageable){
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> get(@PathVariable Long id){
        var task = service.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@RequestBody @Valid TaskRequestDTO dto){
        var task = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable Long id, @RequestBody @Valid TaskRequestDTO dto){
        var updatedTask = service.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateStatus(@PathVariable Long id, @RequestBody @Valid TaskStatusUpdateDTO dto){
        var updatedStatusTask = service.updateStatus(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedStatusTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
