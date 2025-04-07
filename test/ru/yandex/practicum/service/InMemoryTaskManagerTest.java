package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager = Managers.getDefault();
    private Task task1;
    private Epic epic1;
    private SubTask subTask1;

    @BeforeEach
    void createTestEnvironment() {
        task1 = new Task("Task 1", "Task 1 description", Status.NEW);
        taskManager.createTask(task1);

        epic1 = new Epic("Epic 1", "Epic 1 description");
        taskManager.createEpic(epic1);

        subTask1 = new SubTask("SubTask 1", "SubTask 1 description", Status.NEW, epic1.getId());
        taskManager.createSubTask(subTask1);
    }

    //Task
    @Test
    void shouldCreateTaskAndGetById() {
        Task savedTask = taskManager.getTaskById(task1.getId());
        assertNotNull(savedTask, "Задача не возвращается");
        assertEquals(task1, savedTask, "Задачи не совпадают");

        List<Task> arrTasks = taskManager.getAllTasksList();
        assertNotNull(arrTasks, "Список задач не возвращается");
        assertEquals(1, arrTasks.size(), "Количество созданных задач должно быть = 1");
    }

    @Test
    void shouldDeleteTaskById() {
        taskManager.deleteTaskById(task1.getId());
        Task returnedTask = taskManager.getTaskById(task1.getId());
        assertNull(returnedTask, "Задача была не удалена");
    }

    @Test
    void shouldDeleteAllTasks() {
        Task task2 = new Task("Task 2", "Task 2 description", Status.NEW);
        taskManager.createTask(task2);

        List<Task> arrTasks = taskManager.getAllTasksList();
        assertNotNull(arrTasks, "Список задач не возвращается");
        assertEquals(2, arrTasks.size(), "Количество задач не равно двум");

        taskManager.deleteAllTasks();
        arrTasks = taskManager.getAllTasksList();
        assertEquals(0, arrTasks.size(), "Задачи не были удалены");
    }

    @Test
    void shouldTaskNotChangeAfterAddingToManager() {
        Task task = new Task("Task", "Task description", Status.NEW);
        task = taskManager.createTask(task);

        Task savedTask = taskManager.getTaskById(task.getId());
        assertEquals(task.getTitle(), savedTask.getTitle(), "Названия задач не совпадают");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описание задач не совпадают");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статусы задач не совпадают");
        assertEquals(task.getId(), savedTask.getId(), "Идентификаторы задач не совпадают");
    }

    //Epic
    @Test
    void shouldCreateEpicAndGetById() {
        Epic savedEpic = taskManager.getEpicById(epic1.getId());
        assertNotNull(savedEpic, "Эпик не возвращается");
        assertEquals(epic1, savedEpic, "Эпики не совпадают");

        List<Epic> arrEpics = taskManager.getAllEpicsList();
        assertNotNull(arrEpics, "Список эпиков не возвращается");
        assertEquals(1, arrEpics.size(), "Количество созданных эпиков должно быть = 1");
    }

    @Test
    void shouldDeleteEpicById() {
        taskManager.deleteEpicById(epic1.getId());
        Epic returnedEpic = taskManager.getEpicById(epic1.getId());
        assertNull(returnedEpic, "Эпик был не удален");
    }

    @Test
    void shouldDeleteAllEpics() {
        Epic epic2 = new Epic("Epic 2", "Epic 2 description");
        taskManager.createEpic(epic2);

        List<Epic> arrEpics = taskManager.getAllEpicsList();
        assertNotNull(arrEpics, "Список эпиков не возвращается");
        assertEquals(2, arrEpics.size(), "Количество эпиков не равно двум");

        taskManager.deleteAllEpics();
        arrEpics = taskManager.getAllEpicsList();
        assertEquals(0, arrEpics.size(), "Эпики не были удалены");
    }

    @Test
    void shouldEpicNotChangeAfterAddingToManager() {
        Epic epic = new Epic("Epic", "Epic description");
        taskManager.createEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "SubTask 1 description", Status.NEW, epic.getId());
        taskManager.createSubTask(subTask1);

        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(epic.getTitle(), savedEpic.getTitle(), "Названия эпиков не совпадают");
        assertEquals(epic.getDescription(), savedEpic.getDescription(), "Описание эпиков не совпадают");
        assertEquals(epic.getStatus(), savedEpic.getStatus(), "Статусы эпиков не совпадают");
        assertEquals(epic.getId(), savedEpic.getId(), "Идентификаторы эпиков не совпадают");
        assertArrayEquals(epic.getSubTaskIds().toArray(), savedEpic.getSubTaskIds().toArray(), "Списки подазадач не совпадают");
    }


    //SubTask
    @Test
    void shouldCreateSubTaskAndGetById() {
        SubTask savedSubTask = taskManager.getSubTaskById(subTask1.getId());
        assertNotNull(savedSubTask, "Подзадача не возвращается");
        assertEquals(subTask1, savedSubTask, "Подзадачи не совпадают");

        List<SubTask> arrSubTasks = taskManager.getAllSubTasksList();
        assertNotNull(arrSubTasks, "Список подзадач не возвращается");
        assertEquals(1, arrSubTasks.size(), "Количество созданных подзадач должно быть = 1");
    }

    @Test
    void shouldDeleteSubTaskById() {
        taskManager.deleteSubTaskById(subTask1.getId());
        SubTask returnedSubTask = taskManager.getSubTaskById(subTask1.getId());
        assertNull(returnedSubTask, "Подзадача была не удалена");
    }

    @Test
    void shouldDeleteAllSubTasks() {
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask 2 description", Status.NEW, epic1.getId());
        taskManager.createSubTask(subTask2);

        List<SubTask> arrSubTasks = taskManager.getAllSubTasksList();
        assertNotNull(arrSubTasks, "Список подзадач не возвращается");
        assertEquals(2, arrSubTasks.size(), "Количество подзадач не равно двум");

        taskManager.deleteAllSubTasks();
        arrSubTasks = taskManager.getAllSubTasksList();
        assertEquals(0, arrSubTasks.size(), "Подзадачи не были удалены");
    }

    @Test
    void shouldDeleteFromEpics_DeletedSubTasksId() {
        var subTasksOfEpic1 = taskManager.getSubTasksOfEpicById(epic1.getId());

        assertEquals(1, subTasksOfEpic1.size(), "У эпик1 должна быть одна подзадача");

        assertNotNull(subTasksOfEpic1.getFirst(), "У эпик1 должна быть подзадача != null");

        assertEquals(subTask1, subTasksOfEpic1.getFirst(), "У эпик1 должна быть подзадача subTask1");


        taskManager.deleteSubTaskById(subTask1.getId());
        subTasksOfEpic1 = taskManager.getSubTasksOfEpicById(epic1.getId());
        assertEquals(0, subTasksOfEpic1.size(), "У эпик1 не должно быть подзадачи");
    }

}