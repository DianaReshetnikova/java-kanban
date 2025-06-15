package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManagerUserHistory {

    public static void main(String[] args) {

        try {
            Path filePath = Paths.get("TasksList.csv");
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(filePath);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            } else {
                fileBackedTaskManager = FileBackedTaskManager.loadFromFile(filePath);
                System.out.println("Данные, полученные из файла:");
                printAllTasks(fileBackedTaskManager);
            }

            Task task1 = new Task("Task 1", "Task 1 description", Status.NEW);
            task1 = fileBackedTaskManager.createTask(task1);

            Epic epic1 = new Epic("Epic 1", "Epic 1 description");
            epic1 = fileBackedTaskManager.createEpic(epic1);

            SubTask subTask1 = new SubTask("SubTask 1", "SubTask 1 description", Status.DONE, epic1.getId());
            subTask1 = fileBackedTaskManager.createSubTask(subTask1);
            SubTask subTask2 = new SubTask("SubTask 2", "SubTask 2 description", Status.NEW, epic1.getId());
            subTask2 = fileBackedTaskManager.createSubTask(subTask2);

            System.out.println("\n_______Данные, получнные из файла и новые добавленные задачи_______\n");
            printAllTasks(fileBackedTaskManager);

        } catch (IOException e) {
            System.out.println("Произошла ошибка во время создания файла");
        }
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("_____Задачи_____");
        for (Task task : manager.getAllTasksList()) {
            System.out.println(task);
        }
        System.out.println("_____Эпики_____");
        for (Task epic : manager.getAllEpicsList()) {
            System.out.println(epic);

            for (Task task : manager.getSubTasksOfEpicById(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("_____Подзадачи_____");
        for (Task subtask : manager.getAllSubTasksList()) {
            System.out.println(subtask);
        }
    }
}
