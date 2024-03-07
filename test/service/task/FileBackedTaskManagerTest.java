package service.task;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджер задач (файл)")
class FileBackedTaskManagerTest {
    private static FileBackedTaskManager taskManager;
    private File file;

    @BeforeEach
    void init() throws IOException {
        file = File.createTempFile("task", "csv");
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    @DisplayName("должен загрузить пустой файл")
    public void shouldLoadEmptyFile() {
        try {
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
            assertEquals(0, loadedManager.getTasks().size(), "Количество задач несоответствует");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Не удалось загрузить файл. Ошибка: " + e);
        }
    }

    @Test
    @DisplayName("должен сохранять задачу в файл")
    public void shouldSaveTaskToFile() throws IOException {
        taskManager.createTask(new Task("Новая задача", "Описание первой задачи."));
        BufferedReader br = new BufferedReader(new FileReader(file));
        br.readLine();
        assertEquals("0,TASK,Новая задача,NEW,Описание первой задачи.,null", br.readLine(),
                "Строка не соответствует ожидаемой");
    }

    @Test
    @DisplayName("должен сохранять все задачи в файл")
    public void shouldSaveAllTasksToFile() throws IOException {
        taskManager.createTask(new Task("Новая задача 1", "Описание 1 задачи."));
        taskManager.createTask(new Task("Новая задача 2", "Описание 2 задачи."));
        taskManager.createTask(new Task("Новая задача 3", "Описание 3 задачи."));
        taskManager.createTask(new Task("Новая задача 4", "Описание 4 задачи."));

        BufferedReader br = new BufferedReader(new FileReader(file));
        assertEquals("id,type,name,status,description,epicId", br.readLine(),
                "Строка 0 не соответствует ожидаемой");
        assertEquals("0,TASK,Новая задача 1,NEW,Описание 1 задачи.,null", br.readLine(),
                "Строка 1 не соответствует ожидаемой");
        assertEquals("1,TASK,Новая задача 2,NEW,Описание 2 задачи.,null", br.readLine(),
                "Строка 2 не соответствует ожидаемой");
        assertEquals("2,TASK,Новая задача 3,NEW,Описание 3 задачи.,null", br.readLine(),
                "Строка 3 не соответствует ожидаемой");
        assertEquals("3,TASK,Новая задача 4,NEW,Описание 4 задачи.,null", br.readLine(),
                "Строка 4 не соответствует ожидаемой");
        assertNull(br.readLine(), "Строка 5 не соответствует ожидаемой");
    }

    @Test
    @DisplayName("должен сохранять историю в файл")
    public void shouldSaveHistoryToFile() throws IOException {
        taskManager.createTask(new Task("Новая задача 1", "Описание 1 задачи."));
        taskManager.createTask(new Task("Новая задача 2", "Описание 2 задачи."));
        taskManager.createTask(new Task("Новая задача 3", "Описание 3 задачи."));
        taskManager.createTask(new Task("Новая задача 4", "Описание 4 задачи."));

        taskManager.getTaskById(1);
        taskManager.getTaskById(0);
        taskManager.getTaskById(2);

        BufferedReader br = new BufferedReader(new FileReader(file));

        br.readLine();
        br.readLine();
        br.readLine();
        br.readLine();
        br.readLine();
        assertEquals("", br.readLine(), "Строка 5 не соответствует ожидаемой");
        assertEquals("1,0,2", br.readLine(),
                "Строка 6 не соответствует ожидаемой");
    }

    @Test
    @DisplayName("должен читать задачи из файла")
    public void shouldReadTasksFromFile() {
        taskManager.createTask(new Task("Новая задача 1", "Описание 1 задачи."));
        taskManager.createTask(new Task("Новая задача 2", "Описание 2 задачи."));
        taskManager.createTask(new Task("Новая задача 3", "Описание 3 задачи."));
        taskManager.createTask(new Task("Новая задача 4", "Описание 4 задачи."));

        try {
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
            assertEquals(4, loadedManager.getTasks().size(), "Количество задач несоответствует");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Не удалось загрузить файл. Ошибка: " + e);
        }
    }

    @Test
    @DisplayName("должен загружать историю из файла")
    public void shouldLoadHistoryFromFile() {
        taskManager.createTask(new Task("Новая задача 1", "Описание 1 задачи."));
        taskManager.createTask(new Task("Новая задача 2", "Описание 2 задачи."));
        taskManager.createTask(new Task("Новая задача 3", "Описание 3 задачи."));
        taskManager.createTask(new Task("Новая задача 4", "Описание 4 задачи."));

        taskManager.getTaskById(1);
        taskManager.getTaskById(0);
        taskManager.getTaskById(2);

        try {
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
            assertEquals(3, loadedManager.getHistory().size(), "Длина истории несоответствует");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Не удалось загрузить файл. Ошибка: " + e);
        }
    }
}
