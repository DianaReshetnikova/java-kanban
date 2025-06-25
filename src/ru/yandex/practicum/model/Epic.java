package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.TaskType;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Epic extends Task {
    private List<Integer> subTaskIds;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
        subTaskIds = new ArrayList<>();
    }

    public Epic(String title, String description, LocalDateTime startTime, Duration duration) {
        super(title, description, Status.NEW, startTime, duration);
        subTaskIds = new ArrayList<>();
    }

    public Epic(String title, String description, int id) {
        super(title, description, Status.NEW, id);
        subTaskIds = new ArrayList<>();
    }

    public Epic(String title, String description, int id, LocalDateTime startTime, Duration duration) {
        super(title, description, Status.NEW, id, startTime, duration);
        subTaskIds = new ArrayList<>();
    }

    public Epic(String title, String description, int id, Status status) {
        super(title, description, status, id);
        subTaskIds = new ArrayList<>();
    }

    public Epic(String title, String description, int id, Status status, LocalDateTime startTime, Duration duration) {
        super(title, description, status, id, startTime, duration);
        subTaskIds = new ArrayList<>();
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                "subTaskIds=" + subTaskIds +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status;

        if (Optional.ofNullable(startTime).isEmpty())
            result += ", startTime=" + "-";
        else
            result += ", startTime=" + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"));


        if (Optional.ofNullable(duration).isEmpty())
            result += ", duration=" + "-" +
                    '}';
        else
            result += ", duration=" + duration.toHoursPart() + ":" + duration.toMinutesPart() +
                    '}';

        return result;
    }

    public void setSubTask(Integer subTaskId) {
        this.subTaskIds.add(subTaskId);
    }

    public void setSubTasksList(List<Integer> list) {
        this.subTaskIds = new ArrayList<>(List.copyOf(list));
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

    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public void setEndTime(LocalDateTime dateTime) {
        this.endTime = dateTime;
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
//        if (subTaskIds.isEmpty()) {
//            Optional<LocalDateTime> endTimeCalculated = super.getEndTime();
//            endTimeCalculated.ifPresent(localDateTime -> endTime = localDateTime);
//
//            return endTimeCalculated;
//        } else return Optional.ofNullable(endTime);

        return Optional.ofNullable(endTime);
    }
}
