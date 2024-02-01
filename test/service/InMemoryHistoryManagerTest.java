package service;

import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджер истории")
class InMemoryHistoryManagerTest {
    @Test
    @DisplayName("должен добавлять историю так, чтобы она не превышала 10")
    void shouldAddHistoryNotMoreThen10Elements() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        for (int i = 1; i <= 15; i++) {
            historyManager.add(new Task("Task " + i, "Task " + i));
        }

        assertEquals(10, historyManager.getHistory().size(), "Размер истории превышает 10");
    }
}