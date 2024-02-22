package service;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Менеджер истории")
class InMemoryHistoryManagerTest {
    static HistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    @DisplayName("должен добавлять историю так, чтобы айди просмотренных задач были уникальны")
    void shouldAddUniqueIDsInHistory() {
        for (int i = 0; i <= 14; i++) {
            historyManager.add(new Task(i, "Task " + i, TaskStatus.NEW, "Task " + i));
        }
        historyManager.add(new Task(0, "Task " + 0, TaskStatus.NEW, "Task " + 0));

        assertEquals(15, historyManager.getHistory().size(), "Неверный размер истории");
        assertEquals(0, historyManager.getHistory().getLast().getId(), "Неверный ID последнего элемента истории");
        assertEquals(1, historyManager.getHistory().getFirst().getId(), "Неверный ID первого элемента истории");
    }

    @Test
    @DisplayName("должен удалять")
    void should() {
        for (int i = 0; i <= 14; i++) {
            historyManager.add(new Task(i, "Task " + i, TaskStatus.NEW, "Task " + i));
        }
        historyManager.remove(0);
        historyManager.remove(5);
        historyManager.remove(14);

        assertEquals(12, historyManager.getHistory().size(), "Неверный размер истории");
        assertEquals(13, historyManager.getHistory().getLast().getId(), "Неверный ID последнего элемента истории");
        assertEquals(1, historyManager.getHistory().getFirst().getId(), "Неверный ID первого элемента истории");
    }
}