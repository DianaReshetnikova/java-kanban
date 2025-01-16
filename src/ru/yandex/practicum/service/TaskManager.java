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


    //Тут не совсем поняла замечание. В классе Task поле Id у меня типа Integer(может принимать null)
    //значит когда создаем задачу оно должно быть null (т.е. задача только создана и ей еще не присвоен Id)
    // А при вызове метода  обновления задачи наоборот должно быть не null (т.е. Id уже был присвоен)
    public Task createTask(Task newTask) {
        if (Objects.nonNull(newTask.getId()))
            return newTask;

        //Задаем уникальный Id новой задаче
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

        //Если в сабтаске указан эпик которого нет в мапе эпиков, возврат сабтаски
        if(!epics.containsKey(newSubTask.getEpicId()))
            return newSubTask;

        newSubTask.setId(++counterId);
        subTasks.put(newSubTask.getId(), newSubTask);

        Epic epic = epics.get(newSubTask.getEpicId());
        epic.setSubTask(newSubTask.getId());
        epic.updateEpicStatus(getSubTasksOfEpicById(epic.getId()));

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

        newEpic.updateEpicStatus(getSubTasksOfEpicById(newEpic.getId()));
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
        epic.setSubTask(newSubTask.getId());
        epic.updateEpicStatus(getSubTasksOfEpicById(epic.getId()));

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


    public void deleteAllTasks(){
        tasks.clear();
    }

    public void deleteAllEpics(){
        epics.clear();
        subTasks.clear();
    }

    //очищаю мапу сабтасок
    //прохожу по мапе эпиков и очищаю в каждом лист с Id сабтасок
    public void deleteAllSubTasks() {
        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.removeSubTasks();
            epic.updateEpicStatus(getSubTasksOfEpicById(epic.getId()));
        }
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
        if(epics.get(epicId) != null){
            ArrayList<Integer> epicSubTaskIds = epics.get(epicId).getSubTaskIds();
            for (var epicSubTaskId : epicSubTaskIds){
                subTasks.remove(epicSubTaskId);
            }

            epics.remove(epicId);
        }
    }

    public void deleteSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.get(subTaskId);
        if(subTask != null)
        {
            subTasks.remove(subTaskId);
            Epic epic = epics.get(subTask.getEpicId());
            epic.removeSubTaskById(subTaskId);
            epic.updateEpicStatus(getSubTasksOfEpicById(epic.getId()));
        }
    }


    //возвращаю лист сабтасок, полученных из мапы subTasks для запрошенного epicId
    //полученный лист передаю в метод обновления статуса эпика
    public ArrayList<SubTask> getSubTasksOfEpicById(int epicId){
        ArrayList<SubTask> subTasksOfEpic = new ArrayList<>();
        for (SubTask subTask : subTasks.values()){
            if(subTask.getEpicId() == epicId)
                subTasksOfEpic.add(subTask);
        }

        return subTasksOfEpic;
    }
}
