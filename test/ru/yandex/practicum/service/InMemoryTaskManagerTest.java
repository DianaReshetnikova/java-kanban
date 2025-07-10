package ru.yandex.practicum.service;

import exception.NotFoundException;
import exception.TaskOverlapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryTaskManagerTest {
    private TaskManager taskManager = Managers.getDefault();
    private Task task1;
    private Epic epic1;
    private SubTask subTask1;
    private SubTask subTask2;

    @BeforeEach
    void createTestEnvironment() {
        task1 = new Task("Task 1", "Task 1 description", Status.NEW);
        taskManager.createTask(task1);

        epic1 = new Epic("Epic 1", "Epic 1 description");
        taskManager.createEpic(epic1);

        subTask1 = new SubTask("SubTask 1", "SubTask 1 description", Status.NEW, epic1.getId());
        taskManager.createSubTask(subTask1);

        subTask2 = new SubTask("SubTask 2", "SubTask 2 description", Status.NEW, epic1.getId());
        taskManager.createSubTask(subTask2);
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

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                taskManager.getTaskById(task1.getId()));

        assertNotNull(exception, "Задача была не удалена");
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
    /*
    Для расчёта статуса Epic. Граничные условия:
     a. Все подзадачи со статусом NEW.
     b. Все подзадачи со статусом DONE.
     c. Подзадачи со статусами NEW и DONE.
     d. Подзадачи со статусом IN_PROGRESS.
    */
    @Test
    void shouldUpdateEpicStatus() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubTasks();

        epic1 = new Epic("Epic 1", "Epic 1 description");
        taskManager.createEpic(epic1);

        subTask1 = new SubTask("SubTask 1", "SubTask 1 description", Status.NEW, epic1.getId());
        taskManager.createSubTask(subTask1);

        subTask2 = new SubTask("SubTask 2", "SubTask 2 description", Status.NEW, epic1.getId());
        taskManager.createSubTask(subTask2);

        epic1 = taskManager.getEpicById(epic1.getId());//получить обновленное актуальное значение из эпика
        assertEquals(Status.NEW, epic1.getStatus());
        assertEquals(Status.NEW, subTask1.getStatus());
        assertEquals(Status.NEW, subTask2.getStatus());

        //
        SubTask taskDone1 = new SubTask(subTask1.getTitle(), subTask1.getDescription(), Status.DONE, epic1.getId(), subTask1.getId());
        taskManager.updateSubTask(taskDone1);

        SubTask taskDone2 = new SubTask(subTask2.getTitle(), subTask2.getDescription(), Status.DONE, epic1.getId(), subTask2.getId());
        taskManager.updateSubTask(taskDone2);

        epic1 = taskManager.getEpicById(epic1.getId());
        assertEquals(Status.DONE, epic1.getStatus());
        assertEquals(Status.DONE, taskDone1.getStatus());
        assertEquals(Status.DONE, taskDone2.getStatus());


        //
        taskDone1 = new SubTask(subTask1.getTitle(), subTask1.getDescription(), Status.NEW, epic1.getId(), subTask1.getId());
        taskManager.updateSubTask(taskDone1);

        taskDone2 = new SubTask(subTask2.getTitle(), subTask2.getDescription(), Status.DONE, epic1.getId(), subTask2.getId());
        taskManager.updateSubTask(taskDone2);

        epic1 = taskManager.getEpicById(epic1.getId());
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
        assertEquals(Status.NEW, taskDone1.getStatus());
        assertEquals(Status.DONE, taskDone2.getStatus());

        //
        taskDone1 = new SubTask(subTask1.getTitle(), subTask1.getDescription(), Status.IN_PROGRESS, epic1.getId(), subTask1.getId());
        taskManager.updateSubTask(taskDone1);

        taskDone2 = new SubTask(subTask2.getTitle(), subTask2.getDescription(), Status.IN_PROGRESS, epic1.getId(), subTask2.getId());
        taskManager.updateSubTask(taskDone2);

        epic1 = taskManager.getEpicById(epic1.getId());
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
        assertEquals(Status.IN_PROGRESS, taskDone1.getStatus());
        assertEquals(Status.IN_PROGRESS, taskDone2.getStatus());
    }

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

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                taskManager.getEpicById(epic1.getId()));

        assertNotNull(exception, "Эпик был не удален");
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
        assertEquals(1, savedEpic.getSubTaskIds().size(), "Списки подазадач не совпадают");
    }


    //SubTask
    @Test
    void shouldCreateSubTaskAndGetById() {
        SubTask savedSubTask = taskManager.getSubTaskById(subTask1.getId());
        assertNotNull(savedSubTask, "Подзадача не возвращается");
        assertEquals(subTask1, savedSubTask, "Подзадачи не совпадают");

        List<SubTask> arrSubTasks = taskManager.getAllSubTasksList();
        assertNotNull(arrSubTasks, "Список подзадач не возвращается");
        assertEquals(2, arrSubTasks.size(), "Количество созданных подзадач для epic1 должно быть = 2");
    }

    @Test
    void shouldDeleteSubTaskById() {
        taskManager.deleteSubTaskById(subTask1.getId());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                taskManager.getSubTaskById(subTask1.getId()));

        assertNotNull(exception, "Подзадача была не удалена");
    }

    @Test
    void shouldDeleteAllSubTasks() {
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

        assertEquals(2, subTasksOfEpic1.size(), "У эпик1 должно быть две подзадачи");

        assertNotNull(subTasksOfEpic1, "У эпик1 должен быть список подзадач != null");

        assertEquals(subTask1, subTasksOfEpic1.getFirst(), "У эпик1 первая должна быть подзадача subTask1");
        assertEquals(subTask2, subTasksOfEpic1.getLast(), "У эпик1 вторая должна быть подзадача subTask2");


        taskManager.deleteSubTaskById(subTask1.getId());
        taskManager.deleteSubTaskById(subTask2.getId());

        subTasksOfEpic1 = taskManager.getSubTasksOfEpicById(epic1.getId());
        assertEquals(0, subTasksOfEpic1.size(), "У эпик1 не должно быть подзадач");
    }


    @Test
    void twoTasksShouldOverlapOnIntervals() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        Task task1 = new Task(
                "Task 1",
                "Task 1 description",
                Status.NEW,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)),
                Duration.ofHours(2));
        inMemoryTaskManager.createTask(task1);

        Task task2 = new Task("Task 2",
                "Task 2 description",
                Status.NEW,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)),
                Duration.ofHours(1));


        TaskOverlapException exception = assertThrows(TaskOverlapException.class, () -> inMemoryTaskManager.createTask(task2));
        assertNotNull(exception, "Задачи пересекаются");
    }

    @Test
    void twoTaskShouldOverlapOnStartTime() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        Task task1 = new Task(
                "Task 1",
                "Task 1 description",
                Status.NEW,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)),
                Duration.ofHours(2));
        inMemoryTaskManager.createTask(task1);

        Task task2 = new Task("Task 2",
                "Task 2 description",
                Status.NEW,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)),
                null);
        ;

        TaskOverlapException exception = assertThrows(TaskOverlapException.class, () -> inMemoryTaskManager.createTask(task2));
        assertNotNull(exception, "Задачи пересекаются");
    }

    @Test
    void twoTaskShouldNotOverlap() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        Task task1 = new Task(
                "Task 1",
                "Task 1 description",
                Status.NEW,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)),
                Duration.ofHours(2));
        inMemoryTaskManager.createTask(task1);

        Task task2 = new Task("Task 2",
                "Task 2 description",
                Status.NEW,
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 1)),
                Duration.ofHours(1));
        inMemoryTaskManager.createTask(task2);

        assertEquals(task1, inMemoryTaskManager.getTaskById(task1.getId()));
        assertEquals(task2, inMemoryTaskManager.getTaskById(task2.getId()));

        assertFalse(inMemoryTaskManager.isTaskOverlapWithAnySavedTask(task1));
        assertFalse(inMemoryTaskManager.isTaskOverlapWithAnySavedTask(task2));
    }
}