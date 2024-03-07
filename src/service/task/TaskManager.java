package service.task;

import model.Epic;
import model.SubTask;
import model.Task;
import service.history.HistoryManager;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Epic> getEpics();

    List<SubTask> getSubTasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks(Epic epic);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubTaskById(int id);

    List<SubTask> getEpicSubTasks(Epic epic);

    void calculateEpicStatus(Epic epic);

    HistoryManager getHistoryManager();

    List<Task> getHistory();
}
