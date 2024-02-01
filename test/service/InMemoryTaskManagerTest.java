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
    @DisplayName("должен удалять первый элемент списка и добавлять новый в конец после достижения 10 элементов в листе и добавления 11")
    void shouldDeleteFirstElementAndAddNewToLastPositionAfterReachingLengthOf10() {
        Epic epicOne = taskManager.createEpic(new Epic("Test epic1", "Test description1"));
        Epic epicTwo = taskManager.createEpic(new Epic("Test epic2", "Test description2"));
        Epic epicThree = taskManager.createEpic(new Epic("Test epic3", "Test description3"));
        Epic epicFour = taskManager.createEpic(new Epic("Test epic4", "Test description4"));
        SubTask subTaskOne = taskManager.createSubTask(new SubTask(epicOne.getId(), "Подзадача1", "Описание1"));
        SubTask subTaskTwo = taskManager.createSubTask(new SubTask(epicTwo.getId(), "Подзадача2", "Описание2"));
        SubTask subTaskThree = taskManager.createSubTask(new SubTask(epicThree.getId(), "Подзадача3", "Описание3"));
        SubTask subTaskFour = taskManager.createSubTask(new SubTask(epicFour.getId(), "Подзадача4", "Описание4"));
        Task taskOne = taskManager.createTask(new Task("Задача1", "Описание1"));
        Task taskTwo = taskManager.createTask(new Task("Задача2", "Описание2"));
        Task taskThree = taskManager.createTask(new Task("Задача3", "Описание3"));

        taskManager.getEpicById(epicOne.getId());
        taskManager.getEpicById(epicTwo.getId());
        taskManager.getEpicById(epicThree.getId());
        taskManager.getEpicById(epicFour.getId());

        taskManager.getSubTaskById(subTaskOne.getId());
        taskManager.getSubTaskById(subTaskTwo.getId());
        taskManager.getSubTaskById(subTaskThree.getId());
        taskManager.getSubTaskById(subTaskFour.getId());

        taskManager.getTaskById(taskOne.getId());
        taskManager.getTaskById(taskTwo.getId());
        taskManager.getTaskById(taskThree.getId());

        assertEquals(10, taskManager.getHistory().size(), "Неверная длинна истории");
        assertEquals(1, taskManager.getHistory().getFirst().getId(), "Неверный ID первого элемента в истории");
        assertEquals(10, taskManager.getHistory().getLast().getId(), "Неверный ID последнего элемента в истории");
    }

    @Test
    @DisplayName("должен при добавлении новой задачи в историю, сохранять предыдущую версию задачи в себе, если он до этого была")
    void shouldSavePreviousVersionOfTaskAfterEdit() {
        Task taskFour = taskManager.createTask(new Task("Задача4", "Описание4"));
        taskManager.getTaskById(taskFour.getId());

        Task editTask = new Task(taskFour.getId(), taskFour.getName(), taskFour.getStatus(), taskFour.getDescription());

        editTask.setName("Изменённое имя");
        taskManager.updateTask(editTask);

        taskManager.getTaskById(editTask.getId());

        assertEquals("Задача4", taskManager.getHistory().get(0).getName(), "Имя задания не совпадает");
        assertEquals("Изменённое имя", taskManager.getHistory().get(1).getName(), "Имя задания не совпадает");
    }

    @Test
    @DisplayName("должен создать задачу с сгенерированным ID, а не с указанным")
    void shouldCreateTaskWithGeneratedID() {
        Task task = taskManager.createTask(new Task(999, "Задача4", TaskStatus.DONE, "Описание4"));

        assertEquals(0, task.getId(), "ID не совпадает");
    }
}