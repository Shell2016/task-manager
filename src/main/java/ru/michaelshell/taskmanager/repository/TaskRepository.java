package ru.michaelshell.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.michaelshell.taskmanager.model.entity.Task;

public interface TaskRepository extends JpaRepository<Task,Long> {
}
