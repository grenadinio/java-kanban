package service;

import model.*;

import java.util.ArrayList;
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
        for (int id : epic.getSubTaskIds()) {
            subTasks.remove(id);
        }
        calculateEpicStatus(epic);
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
        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubTaskById(subTask.getId());
        calculateEpicStatus(epic);
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
        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            return;
        }
        subTasks.put(subTask.getId(), subTask);
        calculateEpicStatus(epic);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (int subTaskId : epic.getSubTaskIds()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    public void removeSubTaskById(int id) {
        Epic epic = epics.get(subTasks.get(id).getEpicId());
        epic.removeSubTaskById(id);
        subTasks.remove(id);
        calculateEpicStatus(epic);
    }

    public List<SubTask> getEpicSubTasks(Epic epic) {
        List<SubTask> subTaskList = new ArrayList<>();
        for (int id : epic.getSubTaskIds()){
            subTaskList.add(subTasks.get(id));
        }
        return subTaskList;
    }

    private void calculateEpicStatus(Epic epic) {
        boolean allNew = true;
        boolean allDone = true;

        for (Integer subTaskId : epic.getSubTaskIds()) {
            SubTask subTask = getSubTaskById(subTaskId);
            switch (subTask.getStatus()) {
                case IN_PROGRESS:
                    allNew = false;
                    allDone = false;
                    break;
                case NEW:
                    allDone = false;
                    break;
                case DONE:
                    allNew = false;
                    break;
            }

            if (!allNew && !allDone) {
                break;
            }
        }

        if (allNew || subTasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }

    }

}
