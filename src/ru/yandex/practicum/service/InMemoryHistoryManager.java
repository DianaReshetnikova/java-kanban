package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Task;

import java.util.ArrayList;
import java.util.List;

/* InMemoryHistoryManager - класс-менеджер, хранящий информацию о последних 10 просмотренных задачах.
Просмотром считается вызов тех методов, которые получают задачу по идентификатору, —
getTaskById(int id), getSubtaskById(int id) и getEpicById(int id). */

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> tasksHistory;

    public InMemoryHistoryManager() {
        tasksHistory = new ArrayList<>(10);
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (tasksHistory.size() == 10) {
                tasksHistory.removeFirst();
            }

            tasksHistory.addLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }
}
