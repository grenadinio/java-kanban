package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import model.Task;
import service.task.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends AbstractHandler {

    private final TaskManager manager;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    public TasksHandler(TaskManager manager, ErrorHandler errorHandler, Gson gson) {
        this.manager = manager;
        this.errorHandler = errorHandler;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                getHandler(exchange);
                break;
            case "POST":
                postHandler(exchange);
                break;
            case "DELETE":
                deleteHandler(exchange);
                break;
            default:
                sendText(exchange, 404, "Такого эндпоинта не существует");
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        try {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            switch (pathParts.length) {
                case 2:
                    List<Task> tasks = manager.getTasks();
                    sendText(exchange, 200, gson.toJson(tasks));
                    break;
                case 3:
                    Task task = manager.getTaskById(Integer.parseInt(pathParts[2]));
                    if (task != null) {
                        sendText(exchange, 200, gson.toJson(task));
                    } else {
                        throw new NotFoundException("Задачи с идентификатором " + pathParts[2] + " не существует.");
                    }
                    break;
                default:
                    throw new NotFoundException("Неверный путь");
            }
        } catch (Exception e) {
            errorHandler.handle(exchange, e);
        }
    }

    private void postHandler(HttpExchange exchange) throws IOException {
        try {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(reader, Task.class);
            switch (pathParts.length) {
                case 2:
                    Task newTask = manager.createTask(task);
                    sendText(exchange, 201, "Создана новая задача под id: " + newTask.getId());
                    break;
                case 3:
                    manager.updateTask(task);
                    sendText(exchange, 200, "Задача под id: " + task.getId() + " успешно обновлена");
                    break;
                default:
                    throw new NotFoundException("Неверный путь");
            }
        } catch (Exception e) {
            errorHandler.handle(exchange, e);
        }
    }

    private void deleteHandler(HttpExchange exchange) throws IOException {
        try {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            switch (pathParts.length) {
                case 2:
                    manager.deleteAllTasks();
                    sendText(exchange, 200, "Все задачи успешно удалены.");
                    break;
                case 3:
                    int id = Integer.parseInt(pathParts[2]);
                    manager.removeTaskById(id);
                    sendText(exchange, 200, "Задача под id: " + id + " успешно удалена");
                    break;
                default:
                    throw new NotFoundException("Неверный путь");
            }
        } catch (Exception e) {
            errorHandler.handle(exchange, e);
        }
    }
}
