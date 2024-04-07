package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.task.TaskManager;

import java.io.IOException;
import java.util.TreeSet;

public class PrioritizedHandler extends AbstractHandler {

    private final TaskManager manager;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    public PrioritizedHandler(TaskManager manager, ErrorHandler errorHandler, Gson gson) {
        this.manager = manager;
        this.errorHandler = errorHandler;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().equals("GET")) {
                TreeSet<Task> prioritizedTasks = manager.getPrioritizedTasks();
                String response = gson.toJson(prioritizedTasks);

                sendText(exchange, 200, response);
            }
        } catch (Exception e) {
            errorHandler.handle(exchange, e);
        }
    }
}
