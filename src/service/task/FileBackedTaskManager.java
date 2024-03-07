package service.task;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import util.TaskStatus;
import util.TaskType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
                + task.getDescription() + "," + task.getEpicId();
    }

    private static Task fromString(String value) {
        final String[] columns = value.split(",");

        int id = Integer.parseInt(columns[0]);
        TaskType type = TaskType.valueOf(columns[1]);
        String name = columns[2];
        TaskStatus status = TaskStatus.valueOf(columns[3]);
        String description = columns[4];
        int epicId = -1;
        if (type == TaskType.SUBTASK)
            epicId = Integer.parseInt(columns[5]);

        return switch (type) {
            case TASK -> new Task(id, name, status, description);
            case SUBTASK -> new SubTask(epicId, id, name, status, description);
            case EPIC -> new Epic(id, name, status, description, new ArrayList<>());
        };
    }

    private static String historyToString(List<Task> history) {
        StringBuilder sb = new StringBuilder();
        for (Task task : history) {
            sb.append(task.getId()).append(",");
        }
        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        return sb.toString();
    }

    private static List<Integer> historyFromString(String value) {
        final String[] ids = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String id : ids) {
            history.add(Integer.valueOf(id));
        }
        return history;
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

                if (maxId < id) {
                    maxId = id;
                }
            }
            super.setSeq(maxId);
            if ((line = reader.readLine()) != null) {
                List<Integer> historyIds = historyFromString(line);
                for (Integer id : historyIds) {
                    Task task = super.getTaskById(id);
                    if (task != null) {
                        super.getHistoryManager().add(task);
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка при чтении файла.", e);
        }
    }

    private void save() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append("id,type,name,status,description,epicId");
            for (Task task : super.getTasks()) {
                writer.newLine();
                writer.append(toString(task));
            }
            for (SubTask subTask : super.getSubTasks()) {
                writer.newLine();
                writer.append(toString(subTask));
            }
            for (Epic epic : super.getEpics()) {
                writer.newLine();
                writer.append(toString(epic));
            }
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
