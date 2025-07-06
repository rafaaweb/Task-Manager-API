package com.api.taskmanager.repository;

import com.api.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskRepository extends JpaRepository<Task, Long>{

}
