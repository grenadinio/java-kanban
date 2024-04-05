package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.task.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends AbstractHandler {
    private final TaskManager manager;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, ErrorHandler errorHandler, Gson gson) {
        this.manager = manager;
        this.errorHandler = errorHandler;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().equals("GET")) {
                List<Task> history = manager.getHistory();
                String response = gson.toJson(history);

                sendText(exchange, 200, response);
            }
        } catch (Exception e) {
            errorHandler.handle(exchange, e);
        }
    }
}