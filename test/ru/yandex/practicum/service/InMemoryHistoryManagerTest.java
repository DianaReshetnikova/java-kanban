package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();
    Task task1, task2, task3;
    Epic epic1, epic2, epic3;
    SubTask subTask1, subTask2, subTask3, subTask4;

    @BeforeEach
    void createTasks(){
        task1 = new Task("Task 1", "Task 1 description", Status.NEW);
        task1 = taskManager.createTask(task1);

        task2 = new Task("Task 2", "Task 2 description", Status.NEW);
        task2 = taskManager.createTask(task2);

        task3 = new Task("Task 3", "Task 3 description", Status.NEW);
        task3 = taskManager.createTask(task3);


        epic1 = new Epic("Epic 1", "Epic 1 description");
        epic1 = taskManager.createEpic(epic1);

        epic2 = new Epic("Epic 2", "Epic 2 description");
        epic2 = taskManager.createEpic(epic2);

        epic3 = new Epic("Epic 3", "Epic 3 description");
        epic3 = taskManager.createEpic(epic3);


        subTask1 = new SubTask("SubTask 1", "SubTask 1 description", Status.DONE, epic1.getId());
        subTask1 = taskManager.createSubTask(subTask1);

        subTask2 = new SubTask("SubTask 2", "SubTask 2 description", Status.NEW, epic1.getId());
        subTask2 = taskManager.createSubTask(subTask2);

        subTask3 = new SubTask("SubTask 3", "SubTask 3 description", Status.IN_PROGRESS, epic2.getId());
        subTask3 = taskManager.createSubTask(subTask3);

        subTask4 = new SubTask("SubTask 4", "SubTask 4 description", Status.IN_PROGRESS, epic2.getId());
        subTask4 = taskManager.createSubTask(subTask4);
    }


    @Test
    void shouldPrintFirst10Tasks() {
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getEpicById(epic3.getId());

        taskManager.getSubTaskById(subTask1.getId());
        taskManager.getSubTaskById(subTask2.getId());
        taskManager.getSubTaskById(subTask3.getId());
        taskManager.getSubTaskById(subTask4.getId());

        var arr = taskManager.getHistory();
        assertNotNull(arr, "Список истории задач не возвращается");
        //проверяем первый элемент в списке и последний
        assertEquals(task1.getTitle(), arr.getFirst().getTitle(), "Задачи не совпадают");
        assertEquals(subTask4.getTitle(), arr.getLast().getTitle(), "Задачи не совпадают");
    }

    @Test
    void shouldPrint10TasksAfterAdding11Task_GetHistory(){
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getEpicById(epic3.getId());

        taskManager.getSubTaskById(subTask1.getId());
        taskManager.getSubTaskById(subTask2.getId());
        taskManager.getSubTaskById(subTask3.getId());
        taskManager.getSubTaskById(subTask4.getId());

        //добавляем 11 задачу
        SubTask subTask5 = new SubTask("SubTask 5", "SubTask 5 description", Status.IN_PROGRESS, epic2.getId());
        subTask5 = taskManager.createSubTask(subTask5);

        taskManager.getSubTaskById(subTask5.getId());

        var arr = taskManager.getHistory();
        assertNotNull(arr, "Список истории задач не возвращается");
        //проверяем первый элемент в списке, последний и эпик1
        assertEquals(task2.getTitle(), arr.getFirst().getTitle(), "Задачи не совпадают");
        assertEquals(subTask5.getTitle(), arr.getLast().getTitle(), "Задачи не совпадают");
    }
}