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
    void createTasks() {
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

    //Проверяет добавление задач, запрошенных по getTaskById, getEpicById, getSubTaskById, в список истории
    // и корректность последовательности вывода истории просмотров
    @Test
    void shouldFillHistoryTasksHashMap() {
        var arr = taskManager.getHistory();
        assertEquals(0, arr.size(), "Список истории задач должен быть пустым");
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        arr = taskManager.getHistory();
        assertNotNull(arr, "Список истории задач не должен быть = null");

        assertEquals(3, arr.size(), "Список должен состоять из 3 элементов");

        assertEquals(task1, arr.getFirst(), "Задачи не совпадают");
        assertEquals(task2, arr.get(1), "Задачи не совпадают");
        assertEquals(task3, arr.getLast(), "Задачи не совпадают");
    }

    //Если какую-либо задачу посещали несколько раз, то в истории должен остаться только её последний просмотр.
    // Предыдущий должен быть удалён.
    @Test
    void shouldRemoveOldTaskEntrуInHistory() {
        var arr = taskManager.getHistory();
        assertEquals(0, arr.size(), "Список истории задач должен быть пустым");

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());
        //в списке должен быть порядок: task1, task2. Затем последний вызов task1 удаляет первую запись task1
        //Итого: task2, task1

        arr = taskManager.getHistory();
        assertNotNull(arr, "Список истории задач не должен быть = null");

        assertEquals(2, arr.size(), "Список должен состоять из 2 элементов");

        assertEquals(task2, arr.getFirst(), "Первым должен идти task2");
        assertEquals(task1, arr.getLast(), "Последним должен идти task1");
    }
}