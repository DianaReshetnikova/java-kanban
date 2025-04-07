package ru.yandex.practicum.service;

import ru.yandex.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* InMemoryHistoryManager - класс-менеджер, хранящий информацию о последних просмотренных задачах.
Просмотром считается вызов тех методов, которые получают задачу по идентификатору, —
getTaskById(int id), getSubtaskById(int id) и getEpicById(int id). */

public class InMemoryHistoryManager implements HistoryManager {

    /* Ключ - id задачи, просмотр которой требуется удалить, значение — место просмотра этой задачи в списке,
    то есть узел связного списка.
    С помощью номера задачи можно получить соответствующий ему узел связного списка и удалить его за O(1).*/
    private Map<Integer, Node> tasksHistoryMap = new HashMap<>();
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        tasksHistoryMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (tasksHistoryMap.containsKey(task.getId())) {
                remove(task.getId());
            }
            linkLast(task);
        }
    }

    // Удаляет задачи, которые уже были просмотрены, из истории просмотра
    // (убирает дубли, оставляет последний просмотр)
    @Override
    public void remove(int id) {
        removeNode(tasksHistoryMap.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    //Метод принимает объект Node — узел связного списка — и удаляет его из списка
    private void removeNode(Node node) {
        if (node == null) return;

        if (node.next != null && node.prev != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        } else if (node.next != null) {
            node.next.prev = null;
            head = node.next;
        } else if (node.prev != null) {
            node.prev.next = null;
            tail = node.prev;
        } else {
            head = null;
            tail = null;
        }

        tasksHistoryMap.remove(node.task.getId());
    }

    // linkLast будет добавлять задачу в конец списка HashMap<Integer, Node>
    public void linkLast(Task task) {
        if (task == null) return;

        final Node oldTail = tail;
        tail = new Node(task, null, tail);
        if (oldTail == null) {
            head = tail;
        } else {
            oldTail.next = tail;
        }
        tasksHistoryMap.put(task.getId(), tail);
    }

    // getTasks собирает все задачи из узлов в HashMap<Integer, Node> и формирует их в обычный ArrayList
    public List<Task> getTasks() {
        List<Task> resultTasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            resultTasks.add(node.task);
            node = node.next;
        }

        return resultTasks;
    }
}
