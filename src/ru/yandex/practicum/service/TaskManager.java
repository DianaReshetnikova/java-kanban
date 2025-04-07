package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import java.util.List;

/* TaskManager - интерфейс, содержащий список методов,
 позволяющих управлять различными типами задач (создание, хранение, измнение, удаление и пр.),
 которые должны быть у любого объекта-менеджера.
 Хранение информации осуществляется в классе, который реализует TaskManager. */

public interface TaskManager {
    Task createTask(Task newTask);

    Epic createEpic(Epic newEpic);

    SubTask createSubTask(SubTask newSubTask);


    Task updateTask(Task newTask);

    Epic updateEpic(Epic newEpic);

    SubTask updateSubTask(SubTask newSubTask);


    List<Task> getAllTasksList();

    List<Epic> getAllEpicsList();

    List<SubTask> getAllSubTasksList();


    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();


    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    SubTask getSubTaskById(int subTaskId);


    void deleteTaskById(int id);

    void deleteEpicById(int epicId);

    void deleteSubTaskById(int subTaskId);

    List<SubTask> getSubTasksOfEpicById(int epicId);

    List<Task> getHistory();
}
