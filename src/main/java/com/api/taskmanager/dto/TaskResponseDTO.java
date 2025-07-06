package com.api.taskmanager.dto;

import com.api.taskmanager.model.Status;

import java.time.LocalDate;

public record TaskResponseDTO(
    Long id,
    String title,
    String description,
    Status status,
    LocalDate dueDate
){}
