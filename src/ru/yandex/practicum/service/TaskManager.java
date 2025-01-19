package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/*Класс TaskManager отвечает за хранение списков задач разных типов и работу с ними*/
public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;

    private int counterId;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        counterId = 0;
    }


    public Task createTask(Task newTask) {
        if (newTask != null) {
            if (tasks.containsKey(newTask.getId()))
                return newTask;

            //Задаем уникальный Id новой задаче
            newTask.setId(++counterId);
            tasks.put(newTask.getId(), newTask);
        }

        return newTask;
    }

    public Epic createEpic(Epic newEpic) {
        if (newEpic != null) {
            if (epics.containsKey(newEpic.getId()))
                return newEpic;

            newEpic.setId(++counterId);
            epics.put(newEpic.getId(), newEpic);
        }

        return newEpic;
    }

    public SubTask createSubTask(SubTask newSubTask) {
        if (newSubTask != null) {
            //Если сабтаск с Id уже сущ-ет в мапе, или указан эпик которого нет в мапе эпиков
            //то возврат сабтаски
            if (subTasks.containsKey(newSubTask.getId()) ||
                    !epics.containsKey(newSubTask.getEpicId()))
                return newSubTask;

            newSubTask.setId(++counterId);
            subTasks.put(newSubTask.getId(), newSubTask);

            Epic epic = epics.get(newSubTask.getEpicId());
            epic.setSubTask(newSubTask.getId());
            updateEpicStatus(epic);
        }

        return newSubTask;
    }


    public Task updateTask(Task newTask) {
        if (newTask != null) {
            if (!tasks.containsKey(newTask.getId()))
                return newTask;

            tasks.put(newTask.getId(), newTask);
        }

        return newTask;
    }

    public Epic updateEpic(Epic newEpic) {
        if (newEpic != null) {
            if (!epics.containsKey(newEpic.getId()))
                return newEpic;

            updateEpicStatus(newEpic);
            epics.put(newEpic.getId(), newEpic);
        }

        return newEpic;
    }

    public SubTask updateSubTask(SubTask newSubTask) {
        if (newSubTask != null) {
            if (!subTasks.containsKey(newSubTask.getId()) ||
                    !epics.containsKey(newSubTask.getEpicId()))
                return newSubTask;

            subTasks.put(newSubTask.getId(), newSubTask);

            Epic epic = epics.get(newSubTask.getEpicId());
            epic.removeSubTaskById(newSubTask.getId());
            epic.setSubTask(newSubTask.getId());
            updateEpicStatus(epic);
        }

        return newSubTask;
    }


    public ArrayList<Task> getAllTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getAllSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }


    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    //очищаю мапу сабтасок
    //прохожу по мапе эпиков и очищаю в каждом лист с Id сабтасок
    public void deleteAllSubTasks() {
        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.removeSubTasks();
            updateEpicStatus(epic);
        }
    }


    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public SubTask getSubTaskById(int subTaskId) {
        return subTasks.get(subTaskId);
    }


    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int epicId) {
        if (epics.get(epicId) != null) {
            ArrayList<Integer> epicSubTaskIds = epics.get(epicId).getSubTaskIds();
            for (var epicSubTaskId : epicSubTaskIds) {
                subTasks.remove(epicSubTaskId);
            }

            epics.remove(epicId);
        }
    }

    public void deleteSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        if (subTask != null) {
            subTasks.remove(subTaskId);
            Epic epic = epics.get(subTask.getEpicId());
            epic.removeSubTaskById(subTaskId);
            updateEpicStatus(epic);
        }
    }

    private void updateEpicStatus(Epic epic) {
        int cntStatusNEW = 0;
        int cntStatusDONE = 0;

        ArrayList<SubTask> subTasks = getSubTasksOfEpicById(epic.getId());

        for (SubTask subTask : subTasks) {
            if (subTask.getStatus().equals(Status.NEW))
                cntStatusNEW++;
            if (subTask.getStatus().equals(Status.DONE))
                cntStatusDONE++;
        }
        if (subTasks.size() == 0 || subTasks.size() == cntStatusNEW)
            epic.setStatus(Status.NEW);
        else if (subTasks.size() == cntStatusDONE)
            epic.setStatus(Status.DONE);
        else
            epic.setStatus(Status.IN_PROGRESS);
    }

    public ArrayList<SubTask> getSubTasksOfEpicById(int epicId) {
        ArrayList<SubTask> subTasksOfEpic = new ArrayList<>();
        for (Integer subTaskId : epics.get(epicId).getSubTaskIds()) {
            subTasksOfEpic.add(subTasks.get(subTaskId));
        }

        return subTasksOfEpic;
    }
}
