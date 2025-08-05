package com.api.taskmanager.handler;

import com.api.taskmanager.exception.TaskNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleTaskNotFoundException() {
        Long taskId = 1L;
        String message = "Task not found with id: " + taskId;
        TaskNotFoundException ex = new TaskNotFoundException(taskId);

        ProblemDetail response = handler.handleNotFound(ex);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getTitle()).isEqualTo("Task Not Found");
        assertThat(response.getDetail()).isEqualTo(message);
    }

    @Test
    void shouldHandleValidationErrors() {
        // Simula os erros de validação
        FieldError fieldError1 = new FieldError("task", "title", "must not be blank");
        FieldError fieldError2 = new FieldError("task", "description", "must be at least 10 characters");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // Executa o handler
        ProblemDetail response = handler.handleValidation(ex);

        // Asserções
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getTitle()).isEqualTo("Validation Error");
        assertThat(response.getDetail()).isEqualTo("One or more fields are invalid.");

    }
}

