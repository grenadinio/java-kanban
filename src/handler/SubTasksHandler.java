package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import model.Epic;
import model.SubTask;
import service.task.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubTasksHandler extends AbstractHandler {

    private final TaskManager manager;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    public SubTasksHandler(TaskManager manager, ErrorHandler errorHandler, Gson gson) {
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
                sendText(exchange, 404, "Такого эндпоинта не существует.");
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        try {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            switch (pathParts.length) {
                case 2:
                    List<SubTask> subTasks = manager.getSubTasks();
                    sendText(exchange, 200, gson.toJson(subTasks));
                    break;
                case 3:
                    SubTask subTask = manager.getSubTaskById(Integer.parseInt(pathParts[2]));
                    if (subTask != null) {
                        sendText(exchange, 200, gson.toJson(subTask));
                    } else {
                        throw new NotFoundException("Подзадачи с идентификатором " + pathParts[2] + " не существует.");
                    }
                    break;
                default:
                    throw new NotFoundException("Неверный путь.");
            }
        } catch (Exception e) {
            errorHandler.handle(exchange, e);
        }
    }

    private void postHandler(HttpExchange exchange) throws IOException {
        try {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            SubTask subTask = gson.fromJson(reader, SubTask.class);
            switch (pathParts.length) {
                case 2:
                    SubTask newSubTask = manager.createSubTask(subTask);
                    sendText(exchange, 201, "Создана новая подзадача под id: " + newSubTask.getId() + ".");
                    break;
                case 3:
                    manager.updateSubTask(subTask);
                    sendText(exchange, 200, "Подзадача под id: " + subTask.getId() + " успешно обновлена.");
                    break;
                default:
                    throw new NotFoundException("Неверный путь.");
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
                    for (Epic epic : manager.getEpics()) {
                        manager.deleteAllSubTasks(epic);
                    }
                    sendText(exchange, 200, "Все подзадачи успешно удалены.");
                    break;
                case 3:
                    int id = Integer.parseInt(pathParts[2]);
                    manager.removeSubTaskById(id);
                    sendText(exchange, 200, "Подзадача под id: " + id + " успешно удалена.");
                    break;
                default:
                    throw new NotFoundException("Неверный путь.");
            }
        } catch (Exception e) {
            errorHandler.handle(exchange, e);
        }
    }
}
