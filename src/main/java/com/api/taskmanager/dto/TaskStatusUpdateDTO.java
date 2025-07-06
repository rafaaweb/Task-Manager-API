package com.api.taskmanager.dto;

import com.api.taskmanager.model.Status;
import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateDTO(
        @NotNull(message = "Status must not be null")
        Status status
){}
