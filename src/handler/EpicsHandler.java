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

public class EpicsHandler extends AbstractHandler {

    private final TaskManager manager;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    public EpicsHandler(TaskManager manager, ErrorHandler errorHandler, Gson gson) {
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
            Epic epic;
            switch (pathParts.length) {
                case 2:
                    List<Epic> epics = manager.getEpics();
                    sendText(exchange, 200, gson.toJson(epics));
                    break;
                case 3:
                    epic = manager.getEpicById(Integer.parseInt(pathParts[2]));
                    if (epic != null) {
                        sendText(exchange, 200, gson.toJson(epic));
                    } else {
                        throw new NotFoundException("Эпика с идентификатором " + pathParts[2] + " не существует.");
                    }
                    break;
                case 4:
                    if (!pathParts[3].equals("subtasks")) {
                        throw new NotFoundException("Неверный путь.");
                    }
                    epic = manager.getEpicById(Integer.parseInt(pathParts[2]));
                    if (epic != null) {
                        List<SubTask> subTasks = manager.getEpicSubTasks(epic);
                        sendText(exchange, 200, gson.toJson(subTasks));
                    } else {
                        throw new NotFoundException("Эпика с идентификатором " + pathParts[2] + " не существует.");
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
            Epic epic = gson.fromJson(reader, Epic.class);
            switch (pathParts.length) {
                case 2:
                    Epic newEpic = manager.createEpic(epic);
                    sendText(exchange, 201, "Создан новый эпик под id: " + newEpic.getId() + ".");
                    break;
                case 3:
                    manager.updateEpic(epic);
                    sendText(exchange, 200, "Эпик под id: " + epic.getId() + " успешно обновлён.");
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
                    manager.deleteAllEpics();
                    sendText(exchange, 200, "Все эпики успешно удалены.");
                    break;
                case 3:
                    int id = Integer.parseInt(pathParts[2]);
                    manager.removeEpicById(id);
                    sendText(exchange, 200, "Эпик под id: " + id + " успешно удалён.");
                    break;
                default:
                    throw new NotFoundException("Неверный путь.");
            }
        } catch (Exception e) {
            errorHandler.handle(exchange, e);
        }
    }
}
