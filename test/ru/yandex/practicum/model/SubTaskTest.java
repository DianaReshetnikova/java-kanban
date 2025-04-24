package ru.yandex.practicum.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.service.Status;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    @Test
    void shouldBeTheSameEpicsWithEqualsId() {
        SubTask subTask1 = new SubTask("SubTask 1", "SubTask 1 description", Status.NEW, 1, 1);
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask 2 description", Status.NEW, 1, 1);
        assertEquals(subTask1, subTask2, "Эпики не совпадают");
    }
}