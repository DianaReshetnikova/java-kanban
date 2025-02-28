package ru.yandex.practicum.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    TaskManager inMemoryTaskManager;
    HistoryManager inMemoryHistoryManager;

    @Test
    void getDefault() {
        inMemoryTaskManager = Managers.getDefault();

        Task task1 = new Task("Task 1", "Task 1 description", Status.NEW);
        inMemoryTaskManager.createTask(task1);

        Task savedTask = inMemoryTaskManager.getTaskById(task1.getId());

        assertNotNull(savedTask, "Задача не возвращается");
        assertEquals(task1, savedTask, "Задачи не совпадают");
    }

    @Test
    void getDefaultHistory() {
        inMemoryHistoryManager = Managers.getDefaultHistory();

    }
}