package service.task;

import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.history.HistoryManager;
import util.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private final HistoryManager historyManager;

    private int seq = 0;

    private int generateId() {
        return seq++;
    }

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    public void setSeq(int maxId) {
        seq = maxId + 1;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks(Epic epic) {
        List<Integer> subTaskIdsCopy = new ArrayList<>(epic.getSubTaskIds());
        for (Integer id : subTaskIdsCopy) {
            epic.removeSubTaskById(id);
            historyManager.remove(id);
            subTasks.remove(id);
        }
        calculateEpicStatus(epic);
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        if (!epics.containsKey(subTask.getEpicId())) {
            return null;
        }

        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubTaskById(subTask.getId());
        calculateEpicStatus(epic);
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return;
        }
        Epic updatedEpic = epics.get(epic.getId());
        updatedEpic.setName(epic.getName());
        updatedEpic.setDescription(epic.getDescription());
        epics.put(updatedEpic.getId(), updatedEpic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            return;
        }
        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            return;
        }
        subTasks.put(subTask.getId(), subTask);
        calculateEpicStatus(epic);
    }

    @Override
    public void removeTaskById(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        for (int subTaskId : epic.getSubTaskIds()) {
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void removeSubTaskById(int id) {
        Epic epic = epics.get(subTasks.get(id).getEpicId());
        epic.removeSubTaskById(id);
        historyManager.remove(id);
        subTasks.remove(id);
        calculateEpicStatus(epic);
    }

    @Override
    public List<SubTask> getEpicSubTasks(Epic epic) {
        List<SubTask> subTaskList = new ArrayList<>();
        for (int id : epic.getSubTaskIds()) {
            subTaskList.add(subTasks.get(id));
        }
        return subTaskList;
    }

    @Override
    public void calculateEpicStatus(Epic epic) {
        boolean allNew = true;
        boolean allDone = true;

        for (Integer subTaskId : epic.getSubTaskIds()) {
            SubTask subTask = subTasks.get(subTaskId);
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

        if (allNew || epic.getSubTaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }

    }

    protected void restoreTask(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void restoreEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    protected void restoreSubTask(SubTask subTask) {
        if (!epics.containsKey(subTask.getEpicId())) {
            return;
        }

        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubTaskById(subTask.getId());
        calculateEpicStatus(epic);
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
