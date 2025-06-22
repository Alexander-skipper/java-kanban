package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    //История сохраняет разные задачи"
    @Test
    void historyPreservesDifferentTasks() {
        Task task1 = taskManager.createTask(new Task("Task1", "Desc", TaskStatus.NEW));
        Task task2 = taskManager.createTask(new Task("Task2", "Desc", TaskStatus.IN_PROGRESS));

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(TaskStatus.NEW, history.get(0).getTaskStatus());
        assertEquals(TaskStatus.IN_PROGRESS, history.get(1).getTaskStatus());
    }

    //Задача не изменяется при добавлении.
    @Test
    void addToHistory() {
        Task task = taskManager.createTask(new Task("Task", "Desc", TaskStatus.NEW));
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не должна быть null");
        assertEquals(1, history.size(), "История должна содержать 1 задачу");
        assertEquals(task, history.get(0), "Задача в истории должна соответствовать добавленной");
    }

    // Повторное добавление задачи заменяет предыдущую запись.
    @Test
    void duplicateTaskInHistory() {
        Task task = taskManager.createTask(new Task("Task", "Desc", TaskStatus.NEW));
        historyManager.add(task);
        Task updatedTask = new Task(task.getId(), "Task", "Updated Desc", TaskStatus.IN_PROGRESS);
        historyManager.add(updatedTask);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать только одну задачу");
        assertEquals(TaskStatus.IN_PROGRESS, history.get(0).getTaskStatus(), "Задача должна быть обновлена");
    }

    // Удаление задачи из истории.
    @Test
    void removeTaskFromHistory() {
        Task task1 = taskManager.createTask(new Task("Task1", "Desc", TaskStatus.NEW));
        Task task2 = taskManager.createTask(new Task("Task2", "Desc", TaskStatus.IN_PROGRESS));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать 1 задачу после удаления");
        assertEquals(task2, history.get(0), "Оставшаяся задача должна быть task2");
    }

    // Пустая история.
    @Test
    void emptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }
}

