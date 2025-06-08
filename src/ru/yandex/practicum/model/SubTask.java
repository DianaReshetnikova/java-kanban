package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.TaskType;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public SubTask(String title, String description, Status status, int epicId, int id) {
        super(title, description, status, id);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }
}
