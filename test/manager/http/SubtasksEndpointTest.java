package manager.http;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksEndpointTest extends CommonTestSetup {
    @Test
    void createSubtask_withValidEpic_shouldReturn201() throws Exception {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Desc"));
        Subtask subtask = new Subtask("Subtask", "Desc", TaskStatus.NEW, epic.getId());
        String subtaskJson = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getSubtasks().size());
    }

    @Test
    void createSubtask_withoutEpic_shouldNotCreateSubtask() throws Exception {
        assertNull(taskManager.getEpic(999), "Эпик 999 не должен существовать");
        List<Subtask> initialSubtasks = new ArrayList<>(taskManager.getSubtasks());
        Subtask subtask = new Subtask("Subtask", "Desc", TaskStatus.NEW, 999);
        String subtaskJson = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(initialSubtasks, taskManager.getSubtasks(),
                "Список подзадач не должен измениться");

        if (response.statusCode() == 201) {
            assertFalse(response.body().contains("\"id\":"),
                    "Ответ не должен содержать ID подзадачи");
        }
    }

    @Test
    void updateSubtaskStatus_shouldUpdateEpicStatus() throws Exception {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Desc"));
        Subtask subtask = taskManager.createSubtask(
                new Subtask("Sub", "Desc", TaskStatus.NEW, epic.getId()));


        subtask.setTaskStatus(TaskStatus.DONE);
        String subtaskJson = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(TaskStatus.DONE, epic.getTaskStatus());
    }
}