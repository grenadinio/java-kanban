package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Утилита менеджеров")
class ManagersTest {

    @Test
    @DisplayName("должна создать дефолтный менеджер задач")
    void getDefaultTest() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Менеджер задач не инициализировался");
        assertInstanceOf(InMemoryTaskManager.class, taskManager,
                "Менеджер задач не является экземпляром InMemoryTaskManager");
    }

    @Test
    @DisplayName("должна создать дефолтный менеджер истории")
    void getDefaultHistoryTest() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Менеджер истории не инициализировался");
        assertInstanceOf(InMemoryHistoryManager.class, historyManager,
                "Менеджер истории не является экземпляром InMemoryHistoryManager");
    }
}