package manager;

import manager.exceptions.ManagerSaveException;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    // Тесты для проверки пересечения задач по времени.
    @Test
    void tasksWithTimeIntersectionShouldThrowException() {
        LocalDateTime startTime1 = LocalDateTime.now();
        Duration duration1 = Duration.ofHours(1);
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW, startTime1, duration1);
        taskManager.createTask(task1);

        LocalDateTime startTime2 = startTime1.plusMinutes(30);
        Duration duration2 = Duration.ofHours(1);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW, startTime2, duration2);

        assertThrows(ManagerSaveException.class, () -> taskManager.createTask(task2),
                "Должно быть выброшено исключение при пересечении задач по времени");
    }

    @Test
    void subtasksWithTimeIntersectionShouldThrowException() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));

        LocalDateTime startTime1 = LocalDateTime.now();
        Duration duration1 = Duration.ofHours(1);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", TaskStatus.NEW,
                epic.getId(), startTime1, duration1);
        taskManager.createSubtask(subtask1);

        LocalDateTime startTime2 = startTime1.plusMinutes(30);
        Duration duration2 = Duration.ofHours(1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", TaskStatus.NEW,
                epic.getId(), startTime2, duration2);

        assertThrows(ManagerSaveException.class, () -> taskManager.createSubtask(subtask2),
                "Должно быть выброшено исключение при пересечении подзадач по времени");
    }

    @Test
    void tasksWithoutTimeShouldNotConflict() {
        // Проверка задач без времени
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW);

        assertDoesNotThrow(() -> taskManager.createTask(task2),
                "Задачи без времени выполнения не должны конфликтовать");
    }

    @Test
    void tasksWithDifferentTimeShouldNotConflict() {
        LocalDateTime startTime1 = LocalDateTime.now();
        Duration duration1 = Duration.ofHours(1);
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW, startTime1, duration1);
        taskManager.createTask(task1);

        LocalDateTime startTime2 = startTime1.plusHours(2);
        Duration duration2 = Duration.ofHours(1);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW, startTime2, duration2);

        assertDoesNotThrow(() -> taskManager.createTask(task2),
                "Задачи с разным временем выполнения не должны конфликтовать");
    }

    @Test
    void updateTaskWithTimeIntersectionShouldThrowException() {
        LocalDateTime startTime1 = LocalDateTime.now();
        Duration duration1 = Duration.ofHours(1);
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW, startTime1, duration1);
        taskManager.createTask(task1);

        LocalDateTime startTime2 = startTime1.plusHours(2);
        Duration duration2 = Duration.ofHours(1);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW, startTime2, duration2);
        taskManager.createTask(task2);

        task2.setStartTime(startTime1.plusMinutes(30));

        assertThrows(ManagerSaveException.class, () -> taskManager.updateTask(task2),
                "Должно быть выброшено исключение при обновлении задачи с пересечением времени");
    }
}
