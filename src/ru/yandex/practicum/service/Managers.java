package ru.yandex.practicum.service;

/* Managers - утилитарный класс.
На нём лежит вся ответственность за создание менеджера задач и менеджера последних просмотренных задач.
То есть Managers должен сам подбирать нужную реализацию TaskManager и возвращать объект правильного типа.*/

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
