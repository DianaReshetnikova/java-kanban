package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/*Класс TaskManager отвечает за хранение списков задач разных типов и работу с ними*/
public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    private int counterId;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
        counterId = 0;
    }


    public Task createTask(Task newTask) {
        if (Objects.nonNull(newTask.getId()))
            return newTask;

        newTask.setId(++counterId);
        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    public Epic createEpic(Epic newEpic){
        if (Objects.nonNull(newEpic.getId()))
            return newEpic;

        newEpic.setId(++counterId);
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    public SubTask createSubTask(SubTask newSubTask){
        if (Objects.nonNull(newSubTask.getId()))//поле id в классе Task должно быть null
            return newSubTask;

        if(Objects.isNull(newSubTask.getEpicId()))//поле epicId должно быть не null
            return newSubTask;

        if(!epics.containsKey(newSubTask.getEpicId()))
            return newSubTask;

        newSubTask.setId(++counterId);
        subTasks.put(newSubTask.getId(), newSubTask);

        Epic epic = epics.get(newSubTask.getEpicId());
        epic.setSubTask(newSubTask);
        epic.updateEpicStatus();

        return newSubTask;
    }


    public Task updateTask(Task newTask) {
        if (Objects.isNull(newTask.getId()))
            return newTask;

        if (!tasks.containsKey(newTask.getId()))
            return newTask;

        tasks.put(newTask.getId(), newTask);
        return newTask;
    }

    public Epic updateEpic(Epic newEpic){
        if (Objects.isNull(newEpic.getId()))
            return newEpic;

        if (!epics.containsKey(newEpic.getId()))
            return newEpic;

        newEpic.updateEpicStatus();
        epics.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    public SubTask updateSubTask(SubTask newSubTask){
        if (Objects.isNull(newSubTask.getId()))//id подзадачи не должен быть null
            return newSubTask;

        if(Objects.isNull(newSubTask.getEpicId()))//поле epicId не должен быть null
            return newSubTask;

        if(!epics.containsKey(newSubTask.getEpicId()))//epics должен содержать epicId подзадачи
            return newSubTask;

        if (!subTasks.containsKey(newSubTask.getId()))//subTasks должен содержать эту подзадачу
            return newSubTask;

        subTasks.put(newSubTask.getId(), newSubTask);

        Epic epic = epics.get(newSubTask.getEpicId());
        epic.removeSubTaskById(newSubTask.getId());
        epic.setSubTask(newSubTask);
        epic.updateEpicStatus();

        return newSubTask;
    }


    public ArrayList<Task> getAllTasksList() {
        ArrayList<Task> tasksList = new ArrayList<>();
        tasksList.addAll(tasks.values());
        return tasksList;
    }

    public ArrayList<Epic> getAllEpicsList() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        epicsList.addAll(epics.values());
        return epicsList;
    }

    public ArrayList<SubTask> getAllSubTasksList() {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        subTasksList.addAll(subTasks.values());
        return subTasksList;
    }



    public void deleteAllTasks(){
        tasks.clear();
    }

    public void deleteAllEpics(){
        epics.clear();
        subTasks.clear();
    }

    public void deleteAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.removeSubTasks();
            epic.updateEpicStatus();
        }

        subTasks.clear();
    }


    public Task getTaskById(int taskId){
        return tasks.get(taskId);
    }

    public Epic getEpicById(int epicId){
        return epics.get(epicId);
    }

    public SubTask getSubTaskById(int subTaskId){
        return subTasks.get(subTaskId);
    }


    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int epicId) {
        ArrayList<SubTask> epicSubTasks = epics.get(epicId).getSubTasks();
        for (var epicSubTask : epicSubTasks){
            subTasks.remove(epicSubTask.getId());
        }

        epics.remove(epicId);
    }

    public void deleteSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        Epic epic = epics.get(subTask.getEpicId());
        epic.removeSubTaskById(subTaskId);
        epic.updateEpicStatus();

        subTasks.remove(subTaskId);
    }


    public ArrayList<SubTask> getSubTasksOfEpicById(int epicId){
        return epics.get(epicId).getSubTasks();
    }
}
