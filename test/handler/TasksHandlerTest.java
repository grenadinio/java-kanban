package handler;

import com.google.gson.Gson;
import model.Task;
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

@DisplayName("Обработчик пути /tasks")
class TasksHandlerTest {
    InMemoryTaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Task task1;
    Task task2;

    @BeforeEach
    void init() {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();

        task1 = taskManager.createTask(new Task("Task1", "Description1"));
        task2 = taskManager.createTask(new Task("Task2", "Description2"));
    }

    @AfterEach
    void end() {
        httpTaskServer.stop();
    }

    @DisplayName("должен возвращать корректный JSON при GET-запросе без параметров")
    @Test
    void shouldReturnCorrectJsonOnGetRequestWithoutParameters() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedJson = "[{\"id\":0,\"name\":\"Task1\",\"status\":\"NEW\",\"description\":\"Description1\"}," +
                    "{\"id\":1,\"name\":\"Task2\",\"status\":\"NEW\",\"description\":\"Description2\"}]";
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
            URI url = URI.create("http://localhost:8080/tasks/0");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedJson = "{\"id\":0,\"name\":\"Task1\",\"status\":\"NEW\",\"description\":\"Description1\"}";
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
            String postData = gson.toJson(task1);

            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(postData, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedAnswer = "Создана новая задача под id: " + (task2.getId() + 1);
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
            task1.setDescription("1234");
            String postData = gson.toJson(task1);

            URI url = URI.create("http://localhost:8080/tasks/0");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(postData, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedAnswer = "Задача под id: " + task1.getId() + " успешно обновлена";
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
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedAnswer = "Все задачи успешно удалены.";
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
            URI url = URI.create("http://localhost:8080/tasks/0");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedAnswer = "Задача под id: " + task1.getId() + " успешно удалена";
            assertEquals(200, response.statusCode(), "Неверный код ответа");
            assertEquals(expectedAnswer, response.body(), "Сервер ответил неверно");
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса возникла ошибка.");
        }
    }
}