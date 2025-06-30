package ru.yandex.practicum.service;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private static final Path path = Paths.get("FileBackedTaskManagerTest.csv");
    private static FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(path);
    private Task task1;
    private Epic epic1;
    private SubTask subTask1;
    private SubTask subTask2;

    @BeforeAll
    static void shouldCreateTestFile() {
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
                assertTrue(Files.exists(path));
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время создания файла");
        }
    }

    @BeforeEach
    void createTestEnvironment() {
        task1 = new Task("Task 1", "Task 1 description", Status.NEW);
        task1 = fileBackedTaskManager.createTask(task1);

        epic1 = new Epic("Epic 1", "Epic 1 description");
        epic1 = fileBackedTaskManager.createEpic(epic1);

        subTask1 = new SubTask("SubTask 1", "SubTask 1 description", Status.DONE, epic1.getId());
        subTask1 = fileBackedTaskManager.createSubTask(subTask1);

        subTask2 = new SubTask("SubTask 2", "SubTask 2 description", Status.NEW, epic1.getId());
        subTask2 = fileBackedTaskManager.createSubTask(subTask2);
    }

    @Test
    void shouldWriteTasksAndUploadFromFile() {
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(path);
        assertEquals(1, fileBackedTaskManager.tasks.size());
        assertEquals(1, fileBackedTaskManager.epics.size());
        assertEquals(2, fileBackedTaskManager.subTasks.size());
        assertEquals(2, fileBackedTaskManager.epics.get(epic1.getId()).getSubTaskIds().size());
    }

    @Test
    void shouldFileContainEmptyLists() {
        fileBackedTaskManager.deleteAllTasks();
        fileBackedTaskManager.deleteAllSubTasks();
        fileBackedTaskManager.deleteAllEpics();

        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(path);
        assertEquals(0, fileBackedTaskManager.tasks.size());
        assertEquals(0, fileBackedTaskManager.epics.size());
        assertEquals(0, fileBackedTaskManager.subTasks.size());

        if (fileBackedTaskManager.epics.get(epic1.getId()) != null)
            assertEquals(0, fileBackedTaskManager.epics.get(epic1.getId()).getSubTaskIds().size());
    }

    @AfterAll
    static void shouldDeleteTestFile() {
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                assertFalse(Files.exists(path));
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время удаления файла");
        }
    }
}