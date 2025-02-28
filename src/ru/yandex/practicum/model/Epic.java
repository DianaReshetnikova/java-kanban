package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTaskIds;

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
        subTaskIds = new ArrayList<>();
    }

    public Epic(String title, String description, int id) {
        super(title, description, Status.NEW, id);
        subTaskIds = new ArrayList<>();
    }

    public void setSubTask(Integer subTaskId) {
        this.subTaskIds.add(subTaskId);
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void removeSubTaskById(Integer id) {
        subTaskIds.remove(id);
    }

    public void removeSubTasks() {
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
