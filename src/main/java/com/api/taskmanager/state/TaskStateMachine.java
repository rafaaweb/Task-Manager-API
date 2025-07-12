package com.api.taskmanager.state;

import com.api.taskmanager.exception.InvalidTaskStateException;
import com.api.taskmanager.model.Status;
import com.api.taskmanager.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskStateMachine {

    public void start(Task task){
        if (task.getStatus() != Status.PENDING){
            throw new InvalidTaskStateException("Task cannot be started with status: " + task.getStatus());
        }
        task.setStatus(Status.IN_PROGRESS);
    }

    public void done(Task task){
        if (task.getStatus() != Status.IN_PROGRESS){
            throw new InvalidTaskStateException("Task cannot be done with status: " + task.getStatus());
        }
        task.setStatus(Status.DONE);
    }

    public void cancel(Task task){
        if (task.getStatus() != Status.IN_PROGRESS && task.getStatus() != Status.PENDING ){
            throw new InvalidTaskStateException("Task cannot be cancel with status: " + task.getStatus());
        }
        task.setStatus(Status.CANCELED);
    }
}
