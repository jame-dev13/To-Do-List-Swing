package jame.dev.repository;

import jame.dev.models.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ITaskRepo {
    int createTable();
    void save(Task task);
    void update(Task task);
    void updateStatus(UUID uuid);
    Optional<Task> findTaskByUuid(UUID uuid);
    void deleteTaskByUuid(UUID uuid);
    List<Task> findAll();
}
