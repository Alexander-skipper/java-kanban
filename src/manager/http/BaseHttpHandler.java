package manager.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new TimeAdapters.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new TimeAdapters.DurationAdapter())
            .create();

    protected String readRequest(HttpExchange exchange) throws IOException {
        try (InputStream input = exchange.getRequestBody()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    protected void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        byte[] resp = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        sendResponse(exchange, text, 200);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "Задача не найдена", 404);
    }

    protected void sendHasOverlaps(HttpExchange exchange, String message) throws IOException {
        sendResponse(exchange, message, 406);
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        sendResponse(exchange, message, 400);
    }
}
