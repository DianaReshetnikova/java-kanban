package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import java.util.ArrayList;


public interface TaskManager {
    Task createTask(Task newTask);

    Epic createEpic(Epic newEpic);

    SubTask createSubTask(SubTask newSubTask);


    Task updateTask(Task newTask);

    Epic updateEpic(Epic newEpic);

    SubTask updateSubTask(SubTask newSubTask);


    ArrayList<Task> getAllTasksList();

    ArrayList<Epic> getAllEpicsList();

    ArrayList<SubTask> getAllSubTasksList();


    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();


    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    SubTask getSubTaskById(int subTaskId);


    void deleteTaskById(int id);

    void deleteEpicById(int epicId);

    void deleteSubTaskById(int subTaskId);

    ArrayList<SubTask> getSubTasksOfEpicById(int epicId);

    ArrayList<Task> getHistory();
}
