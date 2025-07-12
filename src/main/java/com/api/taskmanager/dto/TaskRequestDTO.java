package com.api.taskmanager.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskRequestDTO(
    @NotBlank(message = "Title cannot be empty")
    String title,

    @NotBlank(message = "Description cannot be empty")
    String description,

    @NotNull(message = "The Date must not be null")
    @FutureOrPresent
    LocalDate dueDate
){}
