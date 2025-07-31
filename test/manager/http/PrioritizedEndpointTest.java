package manager.http;

import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedEndpointTest extends CommonTestSetup {
    @Test
    void getPrioritized_shouldReturn200AndOrderedTasks() throws Exception {
        Task task1 = taskManager.createTask(new Task("First", "Desc", TaskStatus.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(30)));
        Task task2 = taskManager.createTask(new Task("Second", "Desc", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().indexOf("Second") < response.body().indexOf("First"));
    }

    @Test
    void getPrioritized_withoutTime_shouldReturn200AndTasksWithoutTimeLast() throws Exception {

        Task timedTask = taskManager.createTask(new Task("Timed", "Desc", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30)));
        Task untimedTask = taskManager.createTask(new Task("Untimed", "Desc", TaskStatus.NEW));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String jsonResponse = response.body();
        System.out.println("Response JSON: " + jsonResponse);
        assertTrue(jsonResponse.contains("\"name\":\"Timed\""), "Задача с временем должна быть в ответе");
        assertFalse(jsonResponse.contains("\"name\":\"Untimed\""),
                "Задача без времени не должна быть в приоритетном списке");
        int taskCount = jsonResponse.split("\\{\"id\"").length - 1;
        assertEquals(1, taskCount, "В приоритетном списке должна быть только одна задача");
    }
}
