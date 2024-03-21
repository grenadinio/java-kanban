package service.task;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import util.TaskStatus;
import util.TaskType;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String TASK_CSV = "task.csv";
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public FileBackedTaskManager() {
        this.file = new File(TASK_CSV);
    }

    public void init() {
        loadFromFile();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.init();
        return manager;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks(Epic epic) {
        super.deleteAllSubTasks(epic);
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask newSubTask = super.createSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task getTask = super.getTaskById(id);
        save();
        return getTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic getEpic = super.getEpicById(id);
        save();
        return getEpic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask getSubTask = super.getSubTaskById(id);
        save();
        return getSubTask;
    }

    private String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getEpicId() + "," + task.getStartTime() + "," + task.getDuration();
    }

    private static Task fromString(String value) {
        final String[] columns = value.split(",");

        int id = Integer.parseInt(columns[0]);
        TaskType type = TaskType.valueOf(columns[1]);
        String name = columns[2];
        TaskStatus status = TaskStatus.valueOf(columns[3]);
        String description = columns[4];
        int epicId = -1;
        if (type == TaskType.SUBTASK) {
            epicId = Integer.parseInt(columns[5]);
        }

        LocalDateTime startTime = Objects.equals(columns[6], "null") ? null : LocalDateTime.parse(columns[6]);
        Long duration = Objects.equals(columns[7], "null") ? null : Long.parseLong(columns[7]);

        return switch (type) {
            case TASK -> new Task(id, name, status, description, startTime, duration);
            case SUBTASK -> new SubTask(epicId, id, name, status, description, startTime, duration);
            case EPIC -> new Epic(id, name, status, description, new ArrayList<>(), startTime, duration);
        };
    }

    private static String historyToString(List<Task> history) {
        return history.stream()
                .map(Task::getId)
                .map(Objects::toString)
                .collect(Collectors.joining(","));
    }

    private static List<Integer> historyFromString(String value) {
        return Arrays.stream(value.split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    private void loadFromFile() {
        int maxId = 0;

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("Создали файл");
                } else {
                    System.out.println("Файл уже существует");
                }
            } catch (IOException e) {
                throw new RuntimeException("Произошла ошибка при создании файла.", e);
            }
        }
        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                final Task task = fromString(line);
                final int id = task.getId();
                switch (task.getType()) {
                    case TASK -> super.restoreTask(task);
                    case EPIC -> super.restoreEpic((Epic) task);
                    case SUBTASK -> super.restoreSubTask((SubTask) task);
                }

                maxId = Math.max(maxId, id);
            }
            super.setSeq(maxId);
            if ((line = reader.readLine()) != null) {
                historyFromString(line).stream()
                        .map(super::getTaskById)
                        .filter(Objects::nonNull)
                        .forEach(super.getHistoryManager()::add);
            }

        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка при чтении файла.", e);
        }
    }

    private void save() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append("id,type,name,status,description,epicId");
            Stream.of(super.getTasks(), super.getEpics(), super.getSubTasks())
                    .flatMap(Collection::stream)
                    .forEach(task -> {
                        try {
                            writer.newLine();
                            writer.append(toString(task));
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка про попытке записи списка задач.", e);
                        }
                    });
            String historyIds = historyToString(getHistory());
            if (!historyIds.isEmpty()) {
                writer.newLine();
                writer.newLine();
                writer.append(historyIds);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при записи файла.", e);
        }

    }
}
