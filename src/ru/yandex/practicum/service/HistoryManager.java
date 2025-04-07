package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Task;

import java.util.List;

/* HistoryManager - интерфейс для управления историей просмотров задач.
Два метода: add(Task task) - помечает задачи как просмотренные, а getHistory — возвращать их список */

public interface HistoryManager {
    void add(Task task);

    void remove(int id);//метод для удаления задачи из истории если она просмотрена > 1 раза

    List<Task> getHistory();
}
