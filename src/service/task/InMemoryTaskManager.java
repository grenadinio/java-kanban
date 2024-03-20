package service.task;

import exception.NotFoundException;
import exception.ValidationException;
import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.history.HistoryManager;
import util.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private final HistoryManager historyManager;
    private final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime);
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(comparator);
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
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.remove(subTasks.get(id));
        }
        epics.keySet().forEach(historyManager::remove);
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks(Epic epic) {
        List<Integer> subTaskIdsCopy = new ArrayList<>(epic.getSubTaskIds());
        for (Integer id : subTaskIdsCopy) {
            epic.removeSubTaskById(id);
            historyManager.remove(id);
            prioritizedTasks.remove(subTasks.get(id));
            subTasks.remove(id);
        }
        calculateEpicStatus(epic);
        calculateEpicTimes(epic);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Не найдено задачи с id: " + id);
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Не найдено эпика с id: " + id);
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask == null) {
            throw new NotFoundException("Не найдено подзадачи с id: " + id);
        }
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Task createTask(Task task) {
        if (checkTasksIntersection(task)) {
            throw new ValidationException("Время выполнения задачи пересекается с существующей.");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
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
        if (checkTasksIntersection(subTask)) {
            throw new ValidationException("Время выполнения подзадачи пересекается с существующей.");
        }
        if (!epics.containsKey(subTask.getEpicId())) {
            throw new NotFoundException("Не найдено эпика с id: " + subTask.getEpicId());
        }

        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }
        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubTaskById(subTask.getId());
        calculateEpicStatus(epic);
        calculateEpicTimes(epic);
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new NotFoundException("Не найдено задачи с id: " + task.getId());
        }
        if (checkTasksIntersection(task)) {
            throw new ValidationException("Время выполнения задачи пересекается с существующей.");
        }

        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(tasks.get(task.getId()));
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            throw new NotFoundException("Не найдено эпика с id: " + epic.getId());
        }
        Epic updatedEpic = epics.get(epic.getId());
        updatedEpic.setName(epic.getName());
        updatedEpic.setDescription(epic.getDescription());
        epics.put(updatedEpic.getId(), updatedEpic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            throw new NotFoundException("Не найдено подзадачи с id: " + subTask.getId());
        }
        if (checkTasksIntersection(subTask)) {
            throw new ValidationException("Время выполнения подзадачи пересекается с существующей.");
        }
        Epic epic = epics.get(subTask.getEpicId());
        if (epic == null) {
            throw new NotFoundException("Не найдено эпика с id: " + subTask.getEpicId());
        }
        if (subTask.getStartTime() != null) {
            prioritizedTasks.remove(subTasks.get(subTask.getId()));
            prioritizedTasks.add(subTask);
        }
        subTasks.put(subTask.getId(), subTask);
        calculateEpicStatus(epic);
        calculateEpicTimes(epic);
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.get(id) == null) {
            throw new NotFoundException("Не найдено задачи с id: " + id);
        }
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Не найдено эпика с id: " + id);
        }
        for (Integer subTaskId : epic.getSubTaskIds()) {
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void removeSubTaskById(int id) {
        if (subTasks.get(id) == null) {
            throw new NotFoundException("Не найдено подзадачи с id: " + id);
        }
        Epic epic = epics.get(subTasks.get(id).getEpicId());
        if (epic == null) {
            throw new NotFoundException("Не найдено эпика с id: " + id);
        }
        epic.removeSubTaskById(id);
        historyManager.remove(id);
        prioritizedTasks.remove(subTasks.get(id));
        subTasks.remove(id);
        calculateEpicStatus(epic);
        calculateEpicTimes(epic);
    }

    @Override
    public List<SubTask> getEpicSubTasks(Epic epic) {
        return epic.getSubTaskIds().stream()
                .map(subTasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public void calculateEpicStatus(Epic epic) {
        if (epic == null) {
            throw new NotFoundException("Не найдено эпика");
        }
        List<SubTask> subTasksList = epic.getSubTaskIds().stream()
                .map(subTasks::get)
                .toList();

        if (subTasksList.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (SubTask subTask : subTasksList) {
            TaskStatus status = subTask.getStatus();
            allNew &= status == TaskStatus.NEW;
            allDone &= status == TaskStatus.DONE;

            if (!allNew && !allDone) {
                break;
            }
        }

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void calculateEpicTimes(Epic epic) {
        if (epic == null) {
            throw new NotFoundException("Не найдено эпика");
        }
        List<Integer> subTaskIds = epic.getSubTaskIds();

        if (subTaskIds.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(0L);
            return;
        }

        LocalDateTime minStartTime = null;
        LocalDateTime maxEndTime = null;
        long totalDuration = 0;

        for (Integer id : subTaskIds) {
            SubTask subTask = subTasks.get(id);
            if (subTask == null) {
                continue;
            }

            LocalDateTime startTime = subTask.getStartTime();
            LocalDateTime endTime = subTask.getEndTime();
            long duration = subTask.getDuration();

            if (startTime != null && (minStartTime == null || startTime.isBefore(minStartTime))) {
                minStartTime = startTime;
            }

            if (endTime != null && (maxEndTime == null || endTime.isAfter(maxEndTime))) {
                maxEndTime = endTime;
            }

            totalDuration += duration;
        }

        epic.setStartTime(minStartTime);
        epic.setEndTime(maxEndTime);
        epic.setDuration(totalDuration);
    }

    protected void restoreTask(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void restoreEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    protected void restoreSubTask(SubTask subTask) {
        if (!epics.containsKey(subTask.getEpicId())) {
            throw new NotFoundException("Не найдено эпика с id: " + subTask.getEpicId());
        }

        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubTaskById(subTask.getId());
        calculateEpicStatus(epic);
        calculateEpicTimes(epic);
    }

    private boolean checkTasksIntersection(Task task) {
        //false - нет пересечений | true - есть пересечения
        LocalDateTime taskStart = task.getStartTime();
        LocalDateTime taskEnd = task.getEndTime();

        return prioritizedTasks.stream().anyMatch(prioritizedTask -> {
            LocalDateTime priorTaskStart = prioritizedTask.getStartTime();
            LocalDateTime priorTaskEnd = prioritizedTask.getEndTime();

            return (taskStart.isEqual(priorTaskStart) || taskEnd.isEqual(priorTaskEnd)) ||
                    (taskStart.isBefore(priorTaskStart) && taskEnd.isAfter(priorTaskStart)) ||
                    (taskStart.isAfter(priorTaskStart) && taskStart.isBefore(priorTaskEnd));
        });
    }
}
