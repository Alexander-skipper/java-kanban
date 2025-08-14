package manager.http;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class EpicsEndpointTest extends CommonTestSetup {
    @Test
    void createEpic_shouldReturn201AndSaveEpic() throws Exception {
        Epic epic = new Epic("Epic", "Description");
        String epicJson = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    void getEpicSubtasks_shouldReturn200AndSubtasksList() throws Exception {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Desc"));
        taskManager.createSubtask(new Subtask("Sub", "Desc", TaskStatus.NEW, epic.getId()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Sub"));
    }

    @Test
    void deleteEpic_shouldReturn200AndRemoveWithSubtasks() throws Exception {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Desc"));
        taskManager.createSubtask(new Subtask("Sub", "Desc", TaskStatus.NEW, epic.getId()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
    }
}
