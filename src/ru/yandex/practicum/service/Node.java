package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Task;

//Класс Node описывает упрощенный узел двусвязного списка LinkedList
public class Node {
    public Task task;
    public Node next;
    public Node prev;

    public Node(Task task, Node next, Node prev) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }
}
