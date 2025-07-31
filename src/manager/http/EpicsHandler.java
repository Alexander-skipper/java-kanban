package manager.http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Epic;
import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (path.equals("/epics")) {
                        handleGetAllEpics(exchange);
                    } else if (path.startsWith("/epics/")) {
                        if (path.endsWith("/subtasks")) {
                            handleGetEpicSubtasks(exchange, path);
                        } else {
                            handleEpicWithId(exchange, path, "GET");
                        }
                    } else {
                        sendBadRequest(exchange, "Некорректный путь");
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateEpic(exchange);
                    break;
                case "DELETE":
                    if (path.startsWith("/epics/")) {
                        handleEpicWithId(exchange, path, "DELETE");
                    } else {
                        sendBadRequest(exchange, "Некорректный путь");
                    }
                    break;
                default:
                    sendBadRequest(exchange, "Неподдерживаемый метод");
            }
        } catch (Exception e) {
            sendResponse(exchange, "Ошибка сервера", 500);
        }
    }

    private void handleEpicWithId(HttpExchange exchange, String path, String method) throws IOException {
        String idStr = path.substring("/epics/".length());
        try {
            int epicId = Integer.parseInt(idStr);
            if (method.equals("GET")) {
                Epic epic = taskManager.getEpic(epicId);
                if (epic != null) {
                    sendText(exchange, gson.toJson(epic));
                } else {
                    sendNotFound(exchange);
                }
            } else if (method.equals("DELETE")) {
                Epic epic = taskManager.deleteEpic(epicId);
                if (epic != null) {
                    sendText(exchange, "Эпик удален");
                } else {
                    sendNotFound(exchange);
                }
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Некорректный ID эпика");
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, String path) throws IOException {
        String idStr = path.substring("/epics/".length(), path.length() - "/subtasks".length());
        try {
            int epicId = Integer.parseInt(idStr);
            Epic epic = taskManager.getEpic(epicId);
            if (epic != null) {
                String response = gson.toJson(taskManager.getEpicSubtasks(epicId));
                sendText(exchange, response);
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Некорректный ID эпика");
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getEpics());
        sendText(exchange, response);
    }

    private void handleCreateOrUpdateEpic(HttpExchange exchange) throws IOException {
        String body = readRequest(exchange);
        try {
            Epic epic = gson.fromJson(body, Epic.class);
            if (epic.getId() == null) {
                taskManager.createEpic(epic);
                sendResponse(exchange, "Эпик создан", 201);
            } else {
                taskManager.updateEpic(epic);
                sendText(exchange, "Эпик обновлен");
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Неверный формат JSON");
        }
    }
}
