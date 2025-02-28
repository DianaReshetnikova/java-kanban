package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);
    ArrayList<Task> getHistory();
}
