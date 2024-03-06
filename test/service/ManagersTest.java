package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.history.HistoryManager;
import service.history.InMemoryHistoryManager;
import service.task.FileBackedTaskManager;
import service.task.TaskManager;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Утилита менеджеров")
class ManagersTest {

    @Test
    @DisplayName("должна создать дефолтный менеджер задач")
    void getDefaultTest() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Менеджер задач не инициализировался");
        assertInstanceOf(FileBackedTaskManager.class, taskManager,
                "Менеджер задач не является экземпляром FileBackedTaskManager");
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