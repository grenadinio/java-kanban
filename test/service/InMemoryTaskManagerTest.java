package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджер задач")
class InMemoryTaskManagerTest {
    static TaskManager taskManager;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
    }

    @Test
    @DisplayName("должен возвращать ту же задачу, что была создана")
    void shouldReturnSameTaskAsWasCreated() {
        Task task = taskManager.createTask(new Task("Новая задача", "Описание первой задачи."));
        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("должен возвращать список задач с той же задачей, которая была создана")
    void shouldReturnSameTaskInTaskListAsWasCreated() {
        Task task = taskManager.createTask(new Task("Новая задача", "Описание первой задачи."));
        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    @DisplayName("должен возвращать тот же эпик, что был создан")
    void shouldReturnSameEpicAsWasCreated() {
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Эпика не найдено");
        assertEquals(epic, savedEpic, "Эпики не совпадают");
    }

    @Test
    @DisplayName("должен возвращать список эпиков с тем же эпиком, который был создан")
    void shouldReturnSameEpicInEpicsListAsWasCreated() {
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    @DisplayName("должен возвращать ту же подзадачу, что была создана")
    void shouldReturnSameSubTaskAsWasCreated() {
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        SubTask subTask = taskManager.createSubTask(new SubTask(epic.getId(), "Подзадача", "Описание"));
        SubTask savedSubTask = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(savedSubTask, "Подзадачи не найдено");
        assertEquals(subTask, savedSubTask, "Подзадачи не совпадают");
        assertEquals(subTask.getEpicId(), savedSubTask.getEpicId(), "Айди эпиков подзадач не совпадают");
    }

    @Test
    @DisplayName("должен возвращать список подзадач с той же подзадачей, которая была создана")
    void shouldReturnSameSubTaskInSubTaskListAsWasCreated() {
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        SubTask subTask = taskManager.createSubTask(new SubTask(epic.getId(), "Подзадача", "Описание"));
        final List<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    @DisplayName("должен возвращать список подзадач эпика с той же подзадачей, которая была создана")
    void shouldReturnSameSubTaskInSubTaskListFromApicAsWasCreated() {
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        SubTask subTask = taskManager.createSubTask(new SubTask(epic.getId(), "Подзадача", "Описание"));
        final List<SubTask> subTaskList = taskManager.getEpicSubTasks(epic);

        assertNotNull(subTaskList, "Подзадачи не возвращаются.");
        assertEquals(1, subTaskList.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTaskList.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    @DisplayName("не должен создать подзадачу с подзадачей в виде эпика")
    void shouldNotCreateSubtaskWithSubtaskAsEpic() {
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        SubTask subTask = taskManager.createSubTask(new SubTask(epic.getId(), "Подзадача", "Описание"));

        SubTask subTask1 = taskManager.createSubTask(new SubTask(subTask.getId(), "Подзадача", "Описание"));

        assertNull(subTask1, "Создалась подзадача у которой эпик - это подзадача");
    }

    @Test
    @DisplayName("при обновлении задачи должен менять всё кроме id")
    void shouldChangeAllExceptIDonTaskUpdate() {
        Task task = taskManager.createTask(new Task("Новая задача", "Описание первой задачи."));
        Task savedTask = taskManager.getTaskById(task.getId());

        Task editTask = new Task(savedTask.getId(), savedTask.getName(), savedTask.getStatus(), savedTask.getDescription());
        editTask.setName("New name");
        editTask.setStatus(TaskStatus.DONE);
        editTask.setDescription("New description");

        taskManager.updateTask(editTask);

        Task editedTask = taskManager.getTaskById(editTask.getId());

        assertEquals(TaskStatus.DONE, editedTask.getStatus(), "Статус задачи не совпадает");
        assertEquals("New name", editedTask.getName(), "Имя задачи не совпадает");
        assertEquals("New description", editedTask.getDescription(), "Описание задачи не совпадает");
    }

    @Test
    @DisplayName("при обновлении эпика должен менять только Имя и Описание")
    void shouldEditOnlyNameAndDescriptionOnEpicUpdate() {
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        Epic savedEpic = taskManager.getEpicById(epic.getId());

        Epic editEpic = new Epic(savedEpic.getId(), savedEpic.getName(), savedEpic.getStatus(), savedEpic.getDescription(), new ArrayList<>(savedEpic.getSubTaskIds()));
        editEpic.setStatus(TaskStatus.IN_PROGRESS);
        editEpic.setName("New name");
        editEpic.setDescription("Новое описание");

        Epic fakeEpic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        SubTask subTask = taskManager.createSubTask(new SubTask(fakeEpic.getId(), "Подзадача", "Описание"));

        editEpic.addSubTaskById(subTask.getId());
        taskManager.updateEpic(editEpic);

        Epic editedEpic = taskManager.getEpicById(epic.getId());

        assertEquals(TaskStatus.NEW, editedEpic.getStatus(), "Статус эпика не совпадает");
        assertEquals("New name", editedEpic.getName(), "Имя эпика не совпадает");
        assertEquals("Новое описание", editedEpic.getDescription(), "Описание эпика не совпадает");
        assertEquals(0, editedEpic.getSubTaskIds().size(), "Количество подзадач не совпадает");
    }

    @Test
    @DisplayName("при обновлении подзадачи должен менять всё кроме id")
    void shouldChangeAllExceptIDonSubTaskUpdate() {
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        SubTask subTask = taskManager.createSubTask(new SubTask(epic.getId(), "Подзадача", "Описание"));

        SubTask editSubTask = new SubTask(subTask.getEpicId(), subTask.getId(), subTask.getName(), subTask.getStatus(), subTask.getDescription());
        editSubTask.setStatus(TaskStatus.IN_PROGRESS);
        editSubTask.setName("New name");
        editSubTask.setDescription("Новое описание");
        Epic epicNew = taskManager.createEpic(new Epic("Test epic", "Test description"));
        editSubTask.setEpicId(epicNew.getId());

        taskManager.updateSubTask(editSubTask);

        SubTask editedSubTask = taskManager.getSubTaskById(editSubTask.getId());

        assertEquals(TaskStatus.IN_PROGRESS, editedSubTask.getStatus(), "Статус подзадачи не совпадает");
        assertEquals("New name", editedSubTask.getName(), "Имя подзадачи не совпадает");
        assertEquals("Новое описание", editedSubTask.getDescription(), "Описание подзадачи не совпадает");
        assertEquals(2, editedSubTask.getEpicId(), "ID эпика не совпадает");
    }

    @Test
    @DisplayName("должен создать задачу с сгенерированным ID, а не с указанным")
    void shouldCreateTaskWithGeneratedID() {
        Task task = taskManager.createTask(new Task(999, "Задача4", TaskStatus.DONE, "Описание4"));

        assertEquals(0, task.getId(), "ID не совпадает");
    }

    @Test
    @DisplayName("при удалении эпика, должна удаляться история просмотра его подзадач и его самого")
    void shouldDeleteEpicAndSubTasksFromHistoryOnEpicDeletion() {
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        Epic epic2 = taskManager.createEpic(new Epic("Test epic", "Test description"));
        SubTask subTask = taskManager.createSubTask(new SubTask(epic.getId(), "Подзадача", "Описание"));

        taskManager.getSubTaskById(subTask.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getEpicById(epic2.getId());

        taskManager.removeEpicById(epic.getId());

        assertEquals(1, taskManager.getHistory().size(), "Неверная длинна истории после удаления");

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getHistory().size(), "Неверная длинна истории после полного удаления");
    }

    @Test
    @DisplayName("при удалении задачи, должна удаляться история её просмотра")
    void shouldDeleteTaskFromHistoryOnTaskDeletion() {
        for (int i = 0; i <= 14; i++) {
            taskManager.createTask(new Task("Task " + i, "Description " + i));
            taskManager.getTaskById(i);
        }
        taskManager.removeTaskById(0);
        taskManager.removeTaskById(4);

        assertEquals(13, taskManager.getHistory().size(), "Неверная длинна истории после удаления");
        assertEquals(1, taskManager.getHistory().getFirst().getId(), "Неверный ID первой задачи");

        taskManager.deleteAllTasks();

        assertEquals(0, taskManager.getHistory().size(), "Неверная длинна истории после удаления");
    }

    @Test
    @DisplayName("при удалении подзадачи, должна удаляться история её просмотра")
    void shouldDeleteSubTaskFromHistoryOnSubTaskDeletion() {
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        SubTask subTask = taskManager.createSubTask(new SubTask(epic.getId(), "Подзадача1", "Описание1"));
        SubTask subTask2 = taskManager.createSubTask(new SubTask(epic.getId(), "Подзадача2", "Описание2"));
        SubTask subTask3 = taskManager.createSubTask(new SubTask(epic.getId(), "Подзадача3", "Описание3"));

        taskManager.getSubTaskById(subTask3.getId());
        taskManager.getSubTaskById(subTask2.getId());
        taskManager.getSubTaskById(subTask.getId());

        taskManager.removeSubTaskById(subTask.getId());

        assertEquals(2, taskManager.getHistory().size(), "Неверная длинна истории после удаления");
        assertEquals(subTask2.getId(), taskManager.getHistory().getLast().getId(), "Неверный ID последней задачи");

        taskManager.deleteAllSubTasks(epic);

        assertEquals(0, taskManager.getHistory().size(), "Неверная длинна истории после полного удаления");
    }
}