package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Status;

import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subTaskIds;

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
        subTaskIds = new ArrayList<>();
    }

    public void setSubTask(Integer subTaskId) {
        this.subTaskIds.add(subTaskId);
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void removeSubTaskById(Integer id) {
        subTaskIds.remove(id);
    }

    public void removeSubTasks(){
        subTaskIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTaskIds +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
