package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    HistoryManager historyManager = Managers.getDefaultHistory();

    private int counterId;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        counterId = 0;
    }

    @Override
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

    @Override
    public Epic createEpic(Epic newEpic) {
        if (newEpic != null) {
            if (epics.containsKey(newEpic.getId()))
                return newEpic;

            newEpic.setId(++counterId);
            epics.put(newEpic.getId(), newEpic);
        }

        return newEpic;
    }

    @Override
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


    @Override
    public Task updateTask(Task newTask) {
        if (newTask != null) {
            if (!tasks.containsKey(newTask.getId()))
                return newTask;

            tasks.put(newTask.getId(), newTask);
        }

        return newTask;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        if (newEpic != null) {
            if (!epics.containsKey(newEpic.getId()))
                return newEpic;

            updateEpicStatus(newEpic);
            epics.put(newEpic.getId(), newEpic);
        }

        return newEpic;
    }

    @Override
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


    @Override
    public ArrayList<Task> getAllTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }


    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }


    //очищаю мапу сабтасок
    //прохожу по мапе эпиков и очищаю в каждом лист с Id сабтасок
    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.removeSubTasks();
            updateEpicStatus(epic);
        }
    }


    @Override
    public Task getTaskById(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        historyManager.add(subTasks.get(subTaskId));
        return subTasks.get(subTaskId);
    }


    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int epicId) {
        if (epics.get(epicId) != null) {
            ArrayList<Integer> epicSubTaskIds = epics.get(epicId).getSubTaskIds();
            for (var epicSubTaskId : epicSubTaskIds) {
                subTasks.remove(epicSubTaskId);
            }

            epics.remove(epicId);
        }
    }

    @Override
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
        if (subTasks.isEmpty() || subTasks.size() == cntStatusNEW)
            epic.setStatus(Status.NEW);
        else if (subTasks.size() == cntStatusDONE)
            epic.setStatus(Status.DONE);
        else
            epic.setStatus(Status.IN_PROGRESS);
    }

    @Override
    public ArrayList<SubTask> getSubTasksOfEpicById(int epicId) {
        ArrayList<SubTask> subTasksOfEpic = new ArrayList<>();
        for (Integer subTaskId : epics.get(epicId).getSubTaskIds()) {
            subTasksOfEpic.add(subTasks.get(subTaskId));
        }

        return subTasksOfEpic;
    }

    @Override
    public ArrayList<Task> getHistory(){
        return historyManager.getHistory();
    }
}
