package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private final ArrayList<Task> tasksHistory;

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
    public ArrayList<Task> getHistory() {
        return tasksHistory;
    }
}
