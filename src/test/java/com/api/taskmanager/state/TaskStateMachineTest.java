package com.api.taskmanager.state;

import com.api.taskmanager.exception.InvalidTaskStateException;
import com.api.taskmanager.model.Status;
import com.api.taskmanager.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class TaskStateMachineTest {


    private final TaskStateMachine machine = new TaskStateMachine();

    @Test
    void shouldStartTaskWhenStatusIsPending() {
        Task task = new Task();
        task.setStatus(Status.PENDING);

        machine.start(task);

        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    void shouldDoneTaskWhenStatusIsInProgress(){
        Task task = new Task();
        task.setStatus(Status.IN_PROGRESS);

        machine.done(task);

        assertEquals(Status.DONE, task.getStatus());
    }

    @Test
    void shouldCancelTaskWhenStatusIsPending(){
        Task task = new Task();
        task.setStatus(Status.PENDING);

        machine.cancel(task);

        assertEquals(Status.CANCELED, task.getStatus());
    }

    @Test
    void shouldCancelTaskWhenStatusInProgress(){
        Task task = new Task();
        task.setStatus(Status.IN_PROGRESS);

        machine.cancel(task);

        assertEquals(Status.CANCELED, task.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenStartingTaskNotIsPending() {
        Task task = new Task();
        task.setStatus(Status.DONE);

        InvalidTaskStateException exception = assertThrows(
                InvalidTaskStateException.class,
                () -> machine.start(task)
        );

        assertEquals("Task cannot be started with status: DONE", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDoneTaskNotInProgress() {
        Task task = new Task();
        task.setStatus(Status.DONE);

        InvalidTaskStateException exception = assertThrows(
                InvalidTaskStateException.class,
                () -> machine.done(task)
        );

        assertEquals("Task cannot be done with status: DONE", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCancelTaskNotInProgressOrIsPending() {
        Task task = new Task();
        task.setStatus(Status.DONE);

        InvalidTaskStateException exception = assertThrows(
                InvalidTaskStateException.class,
                () -> machine.cancel(task)
        );

        assertEquals("Task cannot be cancel with status: DONE", exception.getMessage());
    }
}
