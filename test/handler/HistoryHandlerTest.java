package handler;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Обработчик пути /history")
class HistoryHandlerTest {
    InMemoryTaskManager taskManager;
    HttpTaskServer httpTaskServer;

    @BeforeEach
    void init() {
        taskManager = new InMemoryTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    @AfterEach
    void end() {
        httpTaskServer.stop();
    }

    @DisplayName("должен возвращать корректный JSON при GET-запросе")
    @Test
    void shouldReturnCorrectJsonOnGetRequest() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            taskManager.createTask(new Task("Task1", "Description1"));
            taskManager.createTask(new Task("Task2", "Description2"));
            taskManager.getTaskById(0);

            URI url = URI.create("http://localhost:8080/history");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedJson = "[{\"id\":0,\"name\":\"Task1\",\"status\":\"NEW\",\"description\":\"Description1\"}]";
            assertEquals(expectedJson, response.body(), "Сервер ответил неверным JSON");
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса возникла ошибка.");
        }

    }

}