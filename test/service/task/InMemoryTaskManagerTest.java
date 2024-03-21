package service.task;

import exception.NotFoundException;
import exception.ValidationException;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджер задач (память)")
class InMemoryTaskManagerTest {
    static TaskManager taskManager;

    @BeforeEach
    void init() {
        taskManager = new InMemoryTaskManager();
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
        SubTask subTask = taskManager
                .createSubTask(new SubTask(epic.getId(), "Подзадача", "Описание"));
        final List<SubTask> subTaskList = taskManager.getEpicSubTasks(epic);

        assertNotNull(subTaskList, "Подзадачи не возвращаются.");
        assertEquals(1, subTaskList.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTaskList.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    @DisplayName("не должен создать подзадачу с подзадачей в виде эпика")
    void shouldNotCreateSubtaskWithSubtaskAsEpic() {
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        SubTask subTask = taskManager
                .createSubTask(new SubTask(epic.getId(), "Подзадача", "Описание"));
        assertThrows(NotFoundException.class, () -> {
            SubTask subTask1 = taskManager
                    .createSubTask(new SubTask(subTask.getId(), "Подзадача", "Описание"));

            assertNull(subTask1, "Создалась подзадача у которой эпик - это подзадача");
        }, "Создание подзадачи с неверно указанным EpicId должно приводить к исключению");
    }

    @Test
    @DisplayName("при обновлении задачи должен менять всё кроме id")
    void shouldChangeAllExceptIDonTaskUpdate() {
        Task task = taskManager.createTask(new Task("Новая задача", "Описание первой задачи."));
        Task savedTask = taskManager.getTaskById(task.getId());

        Task editTask = new Task(savedTask.getId(), savedTask.getName(), savedTask.getStatus(),
                savedTask.getDescription(), savedTask.getStartTime(), savedTask.getDuration());
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

        Epic editEpic = new Epic(savedEpic.getId(), savedEpic.getName(), savedEpic.getStatus(),
                savedEpic.getDescription(), new ArrayList<>(savedEpic.getSubTaskIds()), savedEpic.getStartTime(),
                savedEpic.getDuration());
        editEpic.setStatus(TaskStatus.IN_PROGRESS);
        editEpic.setName("New name");
        editEpic.setDescription("Новое описание");

        Epic fakeEpic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        SubTask subTask = taskManager
                .createSubTask(new SubTask(fakeEpic.getId(), "Подзадача", "Описание"));

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

        SubTask editSubTask = new SubTask(subTask.getEpicId(), subTask.getId(), subTask.getName(), subTask.getStatus(),
                subTask.getDescription(), subTask.getStartTime(), subTask.getDuration());
        editSubTask.setStatus(TaskStatus.IN_PROGRESS);
        editSubTask.setName("New name");
        editSubTask.setDescription("Новое описание");
        Epic epicNew = taskManager.createEpic(new Epic("Test epic", "Test description"));
        editSubTask.setEpicId(epicNew.getId());

        taskManager.updateSubTask(editSubTask);

        SubTask editedSubTask = taskManager.getSubTaskById(editSubTask.getId());

        assertEquals(TaskStatus.IN_PROGRESS, editedSubTask.getStatus(), "Статус подзадачи не совпадает");
        assertEquals("New name", editedSubTask.getName(), "Имя подзадачи не совпадает");
        assertEquals("Новое описание", editedSubTask.getDescription(),
                "Описание подзадачи не совпадает");
        assertEquals(2, editedSubTask.getEpicId(), "ID эпика не совпадает");
    }

    @Test
    @DisplayName("должен создать задачу с сгенерированным ID, а не с указанным")
    void shouldCreateTaskWithGeneratedID() {
        Task task = taskManager.createTask(new Task(999, "Задача4", TaskStatus.DONE, "Описание4",
                LocalDateTime.now(), 30L));

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

        assertEquals(1, taskManager.getHistory().size(),
                "Неверная длинна истории после удаления");

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getHistory().size(),
                "Неверная длинна истории после полного удаления");
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
        SubTask subTask = taskManager
                .createSubTask(new SubTask(epic.getId(), "Подзадача1", "Описание1"));
        SubTask subTask2 = taskManager
                .createSubTask(new SubTask(epic.getId(), "Подзадача2", "Описание2"));
        SubTask subTask3 = taskManager
                .createSubTask(new SubTask(epic.getId(), "Подзадача3", "Описание3"));

        taskManager.getSubTaskById(subTask3.getId());
        taskManager.getSubTaskById(subTask2.getId());
        taskManager.getSubTaskById(subTask.getId());

        taskManager.removeSubTaskById(subTask.getId());

        assertEquals(2, taskManager.getHistory().size(), "Неверная длинна истории после удаления");
        assertEquals(subTask2.getId(), taskManager.getHistory().getLast().getId(),
                "Неверный ID последней задачи");

        taskManager.deleteAllSubTasks(epic);

        assertEquals(0, taskManager.getHistory().size(),
                "Неверная длинна истории после полного удаления");
    }

    @Test
    @DisplayName("при обновлении задачи должен добавить время и продолжительность и верно вычислить окончание задачи")
    void shouldAddTimeAndDurationOnTaskUpdate() {
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        SubTask subTask = taskManager
                .createSubTask(new SubTask(epic.getId(), "Подзадача1", "Описание1"));

        assertNull(subTask.getStartTime(), "Неверное время начала изначальной задачи");
        assertEquals(0, subTask.getDuration(), "Неверная продолжительность изначальной задачи");

        SubTask editSubTask = new SubTask(subTask.getEpicId(), subTask.getId(), subTask.getName(), subTask.getStatus(),
                subTask.getDescription(), subTask.getStartTime(), subTask.getDuration());

        LocalDateTime ldt = LocalDateTime.now();
        editSubTask.setStartTime(ldt);
        editSubTask.setDuration(100L);

        taskManager.updateSubTask(editSubTask);

        SubTask editedSubTask = taskManager.getSubTaskById(editSubTask.getId());

        assertEquals(ldt, editedSubTask.getStartTime(), "Неверное время начала задачи");
        assertEquals(100, editedSubTask.getDuration(), "Неверная продолжительность");
        assertEquals(ldt.plus(Duration.ofMinutes(100)),
                editedSubTask.getEndTime(), "Неверное время окончания задачи");
    }

    @Test
    @DisplayName("должен высчитывать время начала и окончания эпика на основе подзадач")
    void shouldCalculateEpicStartAndEndTimeBySubTasks() {
        LocalDateTime basicLDT = LocalDateTime.of(2024, 2, 7, 10, 15);
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask(epic.getId(), "Подзадача1",
                "Описание1", basicLDT, 100L));
        SubTask subTask2 = taskManager.createSubTask(new SubTask(epic.getId(), "Подзадача2",
                "Описание2", basicLDT.plusDays(4), 60L));
        SubTask subTask3 = taskManager.createSubTask(new SubTask(epic.getId(), "Подзадача3",
                "Описание3", basicLDT.plusDays(10), 60L));
        SubTask subTask4 = taskManager.createSubTask(new SubTask(epic.getId(), "Подзадача4",
                "Описание4", basicLDT.minusDays(4), 60L));

        long sum = subTask1.getDuration() + subTask2.getDuration() + subTask3.getDuration() + subTask4.getDuration();

        assertEquals(subTask4.getStartTime(), epic.getStartTime(), "Неверное время начала эпика");
        assertEquals(sum, epic.getDuration(), "Неверная длительность эпика");
        assertEquals(subTask3.getEndTime(), epic.getEndTime(), "Неверное время окончания эпика");
    }

    @Test
    @DisplayName("не должен создавать задачи если их время работы пересекается с существующими")
    void shouldNotCreateTaskIfItHasIntersections() {
        LocalDateTime basicLDT = LocalDateTime.of(2024, 1, 1, 11, 11);
        assertThrows(ValidationException.class, () -> {
            Task task1 = taskManager.createTask(new Task("Задача1", "Описание", basicLDT, 100L));
            taskManager.createTask(new Task("Задача2", "Описание", basicLDT, 100L));
            taskManager.createTask(new Task("Задача3", "Описание", basicLDT.plusMinutes(100), 100L));
            Task task4 = taskManager.createTask(new Task("Задача4", "Описание", basicLDT.minusMinutes(100), 100L));
            taskManager.createTask(new Task("Задача5", "Описание", basicLDT.minusMinutes(99), 100L));

            assertEquals(3, taskManager.getTasks().size(), "Неверное количество созданных задач");
            assertEquals(task1, taskManager.getTasks().getFirst(), "Неверное название первой задачи в списке");
            assertEquals(task4, taskManager.getTasks().getLast(), "Неверное название последней задачи в списке");
        }, "Создание задачи с пересечением должно приводить к исключению");
    }

    @Test
    @DisplayName("должен верно рассчитывать статус эпика")
    void shouldCalculateEpicStatus() {
        Epic epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        SubTask subTaskNew1 = taskManager
                .createSubTask(new SubTask(epic.getId(), "Подзадача", "Описание"));
        SubTask subTaskNew2 = taskManager
                .createSubTask(new SubTask(epic.getId(), "Подзадача", "Описание"));

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Неверный статус когда все подзадачи NEW");

        subTaskNew1.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTaskNew1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Неверный статус когда подзадачи со статусами NEW и DONE.");

        subTaskNew2.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTaskNew2);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Неверный статус когда все подзадачи DONE");

        subTaskNew1.setStatus(TaskStatus.IN_PROGRESS);
        subTaskNew2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTaskNew1);
        taskManager.updateSubTask(subTaskNew2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(),
                "Неверный статус когда все подзадачи IN_PROGRESS");

    }

    @Test
    @DisplayName("должен вернуть список задач отсортированный по началу исполнения")
    void shouldReturnTasksByStartTime() {
        LocalDateTime basicLDT = LocalDateTime.of(2024, 1, 1, 11, 11);
        assertThrows(ValidationException.class, () -> {
            taskManager.createTask(new Task("Задача1", "Описание", basicLDT, 100L));
            taskManager.createTask(new Task("Задача2", "Описание", basicLDT, 100L));
            Task task3 = taskManager.createTask(new Task("Задача3", "Описание", basicLDT.plusMinutes(100), 100L));
            Task task4 = taskManager.createTask(new Task("Задача4", "Описание", basicLDT.minusMinutes(100), 100L));
            taskManager.createTask(new Task("Задача5", "Описание", basicLDT.minusMinutes(99), 100L));

            TreeSet<Task> tasks = taskManager.getPrioritizedTasks();

            assertEquals(3, tasks.size(), "Неверная длина списка");
            assertEquals(task4, tasks.getFirst(), "Неверный первый элемент списка");
            assertEquals(task3, tasks.getLast(), "Неверный последний элемент списка");
        }, "Создание задачи с пересечением должно приводить к исключению");
    }
}