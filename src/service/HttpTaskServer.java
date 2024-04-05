package service;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import handler.*;
import model.Task;
import service.task.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;

    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) {
        Gson gson = getGson();
        ErrorHandler errorHandler = new ErrorHandler(gson);
        try {
            httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        httpServer.createContext("/tasks", new TasksHandler(manager, errorHandler, gson));
        httpServer.createContext("/subtasks", new SubTasksHandler(manager, errorHandler, gson));
        httpServer.createContext("/epics", new EpicsHandler(manager, errorHandler, gson));
        httpServer.createContext("/history", new HistoryHandler(manager, errorHandler, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager, errorHandler, gson));
    }

    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(tm);
        tm.createTask(new Task("Task1", "Description1"));
        tm.createTask(new Task("Task2", "Description2"));
        tm.getTaskById(0);

        httpTaskServer.start();
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}
