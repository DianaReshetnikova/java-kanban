package ru.yandex.practicum.model;

import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class Task implements Comparable<Task> {

    protected Integer id;
    protected String title;
    protected String description;
    protected Status status;
    protected LocalDateTime startTime;
    protected Duration duration;


    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        String result = "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status;

        if (Optional.ofNullable(startTime).isEmpty())
            result += ", startTime=" + "-";
        else
            result += ", startTime=" + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss"));


        if (Optional.ofNullable(duration).isEmpty())
            result += ", duration=" + "-" +
                    '}';
        else
            result += ", duration=" + duration.toHoursPart() + ":" + duration.toMinutesPart() +
                    '}';

        return result;
    }

    public Task(String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description, Status status, int id) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(String title, String description, Status status, int id, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    //Возврат Optional, т.к. отсутствие назначенной даты / длительности задачи - тоже валидный результат работы
    //(Чтобы не возвращать null)
    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    //Возвращаю Optional, потому что отсутствие заданных сроков у задачи - это валидное поведение программы
    public Optional<LocalDateTime> getEndTime() {
        Optional<LocalDateTime> startTimeOptional = getStartTime();
        Optional<Duration> durationOptional = getDuration();

        if (startTimeOptional.isPresent() && durationOptional.isPresent())
            return Optional.of(startTime.plus(duration));

        return Optional.empty();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Task task) {
        return startTime.compareTo(task.startTime);
    }

    public LocalDateTime getStartTimeValue() {
        return startTime;
    }
}
