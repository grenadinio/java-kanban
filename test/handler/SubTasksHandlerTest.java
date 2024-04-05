package handler;

import com.google.gson.Gson;
import model.Epic;
import model.SubTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.HttpTaskServer;
import service.task.InMemoryTaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Обработчик пути /subtasks")
class SubTasksHandlerTest {
    InMemoryTaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Epic epic;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;

    @BeforeEach
    void init() {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();

        epic = taskManager.createEpic(new Epic("Test epic", "Test description"));
        subTask1 = taskManager
                .createSubTask(new SubTask(epic.getId(), "Подзадача1", "Описание1"));
        subTask2 = taskManager
                .createSubTask(new SubTask(epic.getId(), "Подзадача2", "Описание2"));
        subTask3 = taskManager
                .createSubTask(new SubTask(epic.getId(), "Подзадача3", "Описание3"));
    }

    @AfterEach
    void end() {
        httpTaskServer.stop();
    }

    @DisplayName("должен возвращать корректный JSON при GET-запросе без параметров")
    @Test
    void shouldReturnCorrectJsonOnGetRequestWithoutParameters() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedJson = "[{\"epicId\":0,\"id\":1,\"name\":\"Подзадача1\",\"status\":\"NEW\"," +
                    "\"description\":\"Описание1\"},{\"epicId\":0,\"id\":2,\"name\":\"Подзадача2\"," +
                    "\"status\":\"NEW\",\"description\":\"Описание2\"},{\"epicId\":0,\"id\":3," +
                    "\"name\":\"Подзадача3\",\"status\":\"NEW\",\"description\":\"Описание3\"}]";
            assertEquals(200, response.statusCode(), "Неверный код ответа");
            assertEquals(expectedJson, response.body(), "Сервер ответил неверным JSON");
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса возникла ошибка.");
        }
    }

    @DisplayName("должен возвращать корректный JSON при GET-запросе с параметром")
    @Test
    void shouldReturnCorrectJsonOnGetRequestWithParameter() {
        try (HttpClient client = HttpClient.newHttpClient()) {

            URI url = URI.create("http://localhost:8080/subtasks/1");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedJson = "{\"epicId\":0,\"id\":1,\"name\":\"Подзадача1\",\"status\":\"NEW\",\"description\":\"Описание1\"}";
            assertEquals(200, response.statusCode(), "Неверный код ответа");
            assertEquals(expectedJson, response.body(), "Сервер ответил неверным JSON");
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса возникла ошибка.");
        }
    }

    @DisplayName("должен возвращать корректный JSON при POST-запросе без параметров")
    @Test
    void shouldReturnCorrectJsonOnPostRequestWithoutParameters() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            Gson gson = HttpTaskServer.getGson();
            String postData = gson.toJson(subTask1);

            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(postData, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedAnswer = "Создана новая подзадача под id: " + (subTask3.getId() + 1) + ".";
            assertEquals(201, response.statusCode(), "Неверный код ответа");
            assertEquals(expectedAnswer, response.body(), "Сервер ответил неверно");
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса возникла ошибка.");
        }
    }

    @DisplayName("должен возвращать корректный JSON при POST-запросе с параметром")
    @Test
    void shouldReturnCorrectJsonOnPostRequestWithParameter() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            Gson gson = HttpTaskServer.getGson();
            subTask1.setDescription("12222");
            String postData = gson.toJson(subTask1);

            URI url = URI.create("http://localhost:8080/subtasks/0");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(postData, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedAnswer = "Подзадача под id: " + subTask1.getId() + " успешно обновлена.";
            assertEquals(200, response.statusCode(), "Неверный код ответа");
            assertEquals(expectedAnswer, response.body(), "Сервер ответил неверно");
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса возникла ошибка.");
        }
    }

    @DisplayName("должен возвращать корректный JSON при DELETE-запросе без параметров")
    @Test
    void shouldReturnCorrectJsonOnDeleteRequestWithoutParameters() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedAnswer = "Все подзадачи успешно удалены.";
            assertEquals(200, response.statusCode(), "Неверный код ответа");
            assertEquals(expectedAnswer, response.body(), "Сервер ответил неверно");
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса возникла ошибка.");
        }
    }

    @DisplayName("должен возвращать корректный JSON при DELETE-запросе с параметром")
    @Test
    void shouldReturnCorrectJsonOnDeleteRequestWithParameter() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/1");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedAnswer = "Подзадача под id: " + subTask1.getId() + " успешно удалена.";
            assertEquals(200, response.statusCode(), "Неверный код ответа");
            assertEquals(expectedAnswer, response.body(), "Сервер ответил неверно");
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса возникла ошибка.");
        }
    }
}