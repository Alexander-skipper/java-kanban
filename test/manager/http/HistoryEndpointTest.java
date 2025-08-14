package manager.http;

import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class HistoryEndpointTest extends CommonTestSetup {
    @Test
    void getHistory_shouldReturn200AndHistoryList() throws Exception {
        Task task = taskManager.createTask(new Task("Task", "Desc", TaskStatus.NEW));
        taskManager.getTask(task.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task"));
    }

    @Test
    void getHistory_whenEmpty_shouldReturn200AndEmptyList() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}
