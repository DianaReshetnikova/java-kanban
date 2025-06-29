package ru.yandex.practicum.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.model.Task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager>{
    private TaskManager taskManager = Managers.getDefault();
    private Task task1;
    private Task task2;

    @Test
    void doesTasksOverlapOrNot() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        task1 = new Task(
                "Task 1",
                "Task 1 description",
                Status.NEW,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)),
                Duration.ofHours(2));
        inMemoryTaskManager.createTask(task1);

        task2 = new Task("Task 2",
                "Task 2 description",
                Status.NEW,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)),
                Duration.ofHours(1));
        inMemoryTaskManager.createTask(task2);

        assertFalse(inMemoryTaskManager.isTaskOverlapWithAnySavedTask(task1), "Первая созданная задача не должна иметь пересечений");
        assertEquals(task1, inMemoryTaskManager.getTaskById(task1.getId()), "Должна быть возвращена задача task1");

        assertTrue(inMemoryTaskManager.isTaskOverlapWithAnySavedTask(task2), "Вторая задача пересекается с первой");
        assertNull(inMemoryTaskManager.getTaskById(task2.getId()), "После попытки добавления задачи task2 с пересечением" +
                " должна быть возвращена только задача task1");


    }




}
