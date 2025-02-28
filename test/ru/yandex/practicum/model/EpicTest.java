package ru.yandex.practicum.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void shouldBeTheSameEpicsWithEqualsId(){
        Epic epic1 = new Epic("Epic 1", "Epic 1 description", 1);
        Epic epic2 = new Epic("Epic 2", "Epic 2 description", 1);
        assertEquals(epic1, epic2, "Эпики не совпадают");
    }
}