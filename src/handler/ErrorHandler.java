package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ManagerLoadException;
import exception.ManagerSaveException;
import exception.NotFoundException;
import exception.ValidationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ErrorHandler {
    private final Gson gson;

    public ErrorHandler(Gson gson) {
        this.gson = gson;
    }

    public void handle(HttpExchange h, NumberFormatException e) throws IOException {
        e.printStackTrace();
        sendText(h, 400, gson.toJson(e));
    }

    public void handle(HttpExchange h, ManagerSaveException e) throws IOException {
        e.printStackTrace();
        sendText(h, 400, gson.toJson(e));
    }

    public void handle(HttpExchange h, ManagerLoadException e) throws IOException {
        e.printStackTrace();
        sendText(h, 400, gson.toJson(e));
    }

    public void handle(HttpExchange h, NullPointerException e) throws IOException {
        e.printStackTrace();
        sendText(h, 400, gson.toJson(e));
    }

    public void handle(HttpExchange h, NotFoundException e) throws IOException {
        e.printStackTrace();
        sendText(h, 404, gson.toJson(e));
    }

    public void handle(HttpExchange h, ValidationException e) throws IOException {
        e.printStackTrace();
        sendText(h, 406, gson.toJson(e));
    }

    public void handle(HttpExchange h, Exception e) throws IOException {
        e.printStackTrace();
        sendText(h, 500, gson.toJson(e));
    }

    private void sendText(HttpExchange h, int code, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(code, response.length);
        h.getResponseBody().write(response);
    }
}
