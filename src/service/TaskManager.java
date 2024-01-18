package service;

import model.*;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;

    int seq = 0;

    private int generateId() {
        return seq++;
    }

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        subTasks.clear();
        epics.clear();
    }

    public void deleteAllSubTasks(Epic epic) {
        for (SubTask subTask : epic.getSubTasks()) {
            subTasks.remove(subTask.getId());
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        subTask.getEpic().addSubTask(subTask);
        return subTask;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        Epic updatedEpic = epics.get(epic.getId());
        updatedEpic.setName(epic.getName());
        updatedEpic.setDescription(epic.getDescription());
    }

    public void updateSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpic().getId());
        if (epic == null) {
            return;
        }
        subTasks.put(subTask.getId(), subTask);
        epic.calculateEpicStatus();
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        for (SubTask subTask : epics.get(id).getSubTasks()) {
            subTasks.remove(subTask.getId());
        }
        epics.remove(id);
    }

    public void removeSubTaskById(int id) {
        Epic epic = subTasks.get(id).getEpic();
        epic.removeSubTask(subTasks.get(id));
        subTasks.remove(id);
        epic.calculateEpicStatus();
    }

    public List<SubTask> getEpicSubTasks(Epic epic) {
        return epic.getSubTasks();
    }

}
