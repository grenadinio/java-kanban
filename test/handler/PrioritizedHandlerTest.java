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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Обработчик пути /prioritized")
class PrioritizedHandlerTest {
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
        LocalDateTime basicLDT = LocalDateTime.of(2024, 1, 1, 11, 11);
        try (HttpClient client = HttpClient.newHttpClient()) {
            taskManager.createTask(new Task("Задача1", "Описание", basicLDT.plusMinutes(100), 100L));
            taskManager.createTask(new Task("Задача2", "Описание", basicLDT.minusMinutes(100), 100L));

            URI url = URI.create("http://localhost:8080/prioritized");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String expectedJson = "[{\"id\":1,\"name\":\"Задача2\",\"status\":\"NEW\",\"description\":\"Описание\"," +
                    "\"duration\":\"PT1H40M\",\"startTime\":\"2024-01-01T09:31:00\"},{\"id\":0,\"name\":\"Задача1\"," +
                    "\"status\":\"NEW\",\"description\":\"Описание\",\"duration\":\"PT1H40M\",\"startTime\":" +
                    "\"2024-01-01T12:51:00\"}]";
            assertEquals(expectedJson, response.body(), "Сервер ответил неверным JSON");
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса возникла ошибка.");
        }

    }
}