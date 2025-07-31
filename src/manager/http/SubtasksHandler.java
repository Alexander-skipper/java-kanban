package manager.http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.exceptions.ManagerSaveException;
import tasks.Subtask;
import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (path.equals("/subtasks")) {
                        handleGetAllSubtasks(exchange);
                    } else if (path.startsWith("/subtasks/")) {
                        handleSubtaskWithId(exchange, path, "GET");
                    } else {
                        sendBadRequest(exchange, "Некорректный путь");
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateSubtask(exchange);
                    break;
                case "DELETE":
                    if (path.startsWith("/subtasks/")) {
                        handleSubtaskWithId(exchange, path, "DELETE");
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

    private void handleSubtaskWithId(HttpExchange exchange, String path, String method) throws IOException {
        String idStr = path.substring("/subtasks/".length());
        try {
            int subtaskId = Integer.parseInt(idStr);
            if (method.equals("GET")) {
                Subtask subtask = taskManager.getSubtasks(subtaskId);
                if (subtask != null) {
                    sendText(exchange, gson.toJson(subtask));
                } else {
                    sendNotFound(exchange);
                }
            } else if (method.equals("DELETE")) {
                Subtask subtask = taskManager.deleteSubtask(subtaskId);
                if (subtask != null) {
                    sendText(exchange, "Подзадача удалена");
                } else {
                    sendNotFound(exchange);
                }
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Некорректный ID подзадачи");
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getSubtasks());
        sendText(exchange, response);
    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        String body = readRequest(exchange);
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (subtask.getId() == null) {
                taskManager.createSubtask(subtask);
                sendResponse(exchange, "Подзадача создана", 201);
            } else {
                taskManager.updateSubtask(subtask);
                sendText(exchange, "Подзадача обновлена");
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Неверный формат JSON");
        } catch (ManagerSaveException e) {
            sendHasOverlaps(exchange, e.getMessage());
        }
    }
}
