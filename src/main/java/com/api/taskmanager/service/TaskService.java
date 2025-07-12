package com.api.taskmanager.service;

import com.api.taskmanager.dto.TaskRequestDTO;
import com.api.taskmanager.dto.TaskResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    Page<TaskResponseDTO> findAll(Pageable pageable);
    TaskResponseDTO findById(Long id);
    TaskResponseDTO create(TaskRequestDTO dto);
    TaskResponseDTO update(Long id, TaskRequestDTO dto);
    TaskResponseDTO done(Long id);
    TaskResponseDTO cancel(Long id);
    TaskResponseDTO start(Long id);
    void delete(Long id);

}
