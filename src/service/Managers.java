package service;

import service.history.HistoryManager;
import service.history.InMemoryHistoryManager;
import service.task.FileBackedTaskManager;
import service.task.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
