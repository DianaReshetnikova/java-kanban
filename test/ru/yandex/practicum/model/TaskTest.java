package ru.yandex.practicum.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.service.Status;


class TaskTest {

    @Test
    void shouldBeTheSameTasksWithEqualsId(){
        Task task1 = new Task("Task 1", "Task 1 description", Status.NEW, 1);
        Task task2 = new Task("Task 2", "Task 2 description", Status.NEW, 1);
        assertEquals(task1, task2, "Задачи не совпадают");
    }
}