package manager.http;

import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TasksEndpointTest extends CommonTestSetup {
    @Test
    void createTask_shouldReturn201AndSaveTask() throws Exception {
        Task task = new Task("Test", "Description", TaskStatus.NEW);
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    void getTaskById_withInvalidId_shouldReturn404() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void updateTask_shouldReturn200AndUpdateTask() throws Exception {
        Task task = taskManager.createTask(new Task("Original", "Desc", TaskStatus.NEW));
        task.setName("Updated");
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Updated", taskManager.getTask(task.getId()).getName());
    }

    @Test
    void deleteTask_shouldReturn200AndRemoveTask() throws Exception {
        Task task = taskManager.createTask(new Task("ToDelete", "Desc", TaskStatus.NEW));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void createTaskWithTime_shouldReturn201AndSetTime() throws Exception {
        Task task = new Task("Timed", "Desc", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasks = taskManager.getTasks();

        Task createdTask = null;
        for (Task t : tasks) {
            if ("Timed".equals(t.getName())) {
                createdTask = t;
                break;
            }
        }
        assertNotNull(createdTask, "Задача не была создана на сервере");
        assertNotNull(createdTask.getStartTime(), "Время начала не установлено");
        assertNotNull(createdTask.getId(), "ID не был сгенерирован");
    }
}
