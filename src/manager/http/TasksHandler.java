package manager.http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.exceptions.ManagerSaveException;
import tasks.Task;
import java.io.IOException;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (path.equals("/tasks")) {
                        handleGetAllTasks(exchange);
                    } else if (path.startsWith("/tasks/")) {
                        handleTaskWithId(exchange, path, "GET");
                    } else {
                        sendBadRequest(exchange, "Некорректный путь");
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateTask(exchange);
                    break;
                case "DELETE":
                    if (path.startsWith("/tasks/")) {
                        handleTaskWithId(exchange, path, "DELETE");
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

    private void handleTaskWithId(HttpExchange exchange, String path, String method) throws IOException {
        String idStr = path.substring("/tasks/".length());
        try {
            int taskId = Integer.parseInt(idStr);
            if (method.equals("GET")) {
                Task task = taskManager.getTask(taskId);
                if (task != null) {
                    sendText(exchange, gson.toJson(task));
                } else {
                    sendNotFound(exchange);
                }
            } else if (method.equals("DELETE")) {
                Task task = taskManager.deleteTask(taskId);
                if (task != null) {
                    sendText(exchange, "Задача удалена");
                } else {
                    sendNotFound(exchange);
                }
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange, "Некорректный ID задачи");
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getTasks());
        sendText(exchange, response);
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        String body = readRequest(exchange);
        try {
            Task task = gson.fromJson(body, Task.class);
            if (task.getId() == null) {
                taskManager.createTask(task);
                sendResponse(exchange, "Задача создана", 201);
            } else {
                taskManager.updateTask(task);
                sendText(exchange, "Задача обновлена");
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange, "Неверный формат JSON");
        } catch (ManagerSaveException e) {
            sendHasOverlaps(exchange, e.getMessage());
        }
    }
}
