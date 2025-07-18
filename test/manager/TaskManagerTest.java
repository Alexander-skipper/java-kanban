package manager;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;



public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void init() {
       taskManager = createTaskManager();

    }

    @Test
    public void testCreateTask() {
        taskManager.createTask(new Task("name", "desk", TaskStatus.NEW));
        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
    }

    //Задачи с одинаковым id должны быть равны.
    @Test
    void tasksWithSameIdAreEqual() {
        Task task1 = new Task("Task1", "Desc1", TaskStatus.NEW);
        Task task2 = new Task("Task2", "Desc2", TaskStatus.DONE);
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    //Подзадачи с одинаковым id должны быть равны.
    @Test
    void subtasksWithSameIdAreEqual() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Desc"));
        Subtask sub1 = new Subtask("Sub1", "Desc1", TaskStatus.NEW, epic.getId());
        Subtask sub2 = new Subtask("Sub2", "Desc2", TaskStatus.DONE, epic.getId());
        sub1.setId(2);
        sub2.setId(2);

        assertEquals(sub1, sub2, "Подзадачи с одинаковым id должны быть равны");
    }

    // Эпик не может быть своей подзадачей
    @Test
    void epicCannotBeItsOwnSubtask() {
        Epic epic = new Epic("Test Epic", "Test Epic description");
        int epicId = taskManager.createEpic(epic).getId();

        Subtask subtask = new Subtask("Test Subtask", "Test Subtask description", TaskStatus.NEW, epicId);
        subtask.setId(epicId); // Пытаемся сделать подзадачу с тем же ID, что и эпик

        Subtask createdSubtask = taskManager.createSubtask(subtask);

        assertNull(createdSubtask, "Эпик не должен быть своей собственной подзадачей");
        assertTrue(taskManager.getEpicSubtasks(epicId).isEmpty(), "У эпика не должно быть подзадач");
    }

    // Подзадача не может быть своим эпиком
    @Test
    void subtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask("Test Subtask", "Test Subtask description", TaskStatus.NEW, 1);
        subtask.setId(1); // Пытаемся сделать подзадачу с тем же ID, что и её эпик

        // Сначала создаем эпик с ID=1
        Epic epic = new Epic("Test Epic", "Test Epic description");
        epic.setId(1);
        taskManager.createEpic(epic);

        Subtask createdSubtask = taskManager.createSubtask(subtask);

        assertNull(createdSubtask, "Подзадача не должна быть своим собственным эпиком");
        assertTrue(taskManager.getEpicSubtasks(1).isEmpty(), "У эпика не должно быть подзадач");
    }

    //Менеджеры должны инициализироваться.
    @Test
    void managersInitialize() {
        assertNotNull(Managers.getDefault(), "TaskManager должен быть инициализирован");
        assertNotNull(Managers.getDefaultHistory(), "HistoryManager должен быть инициализирован");
    }

    //Добавление и поиск задач разных типов.
    @Test
    void addAndFindTasks() {
        Task task = taskManager.createTask(new Task("Task", "Desc", TaskStatus.NEW));
        Epic epic = taskManager.createEpic(new Epic("Epic", "Desc"));
        Subtask sub = taskManager.createSubtask(
                new Subtask("Sub", "Desc", TaskStatus.NEW, epic.getId()));

        assertNotNull(taskManager.getTask(task.getId()), "Задача должна быть найдена");
        assertNotNull(taskManager.getEpic(epic.getId()), "Эпик должен быть найден");
        assertNotNull(taskManager.getSubtasks(sub.getId()), "Подзадача должна быть найдена");
    }

    //Ручные и автоматические id не конфликтуют/
    @Test
    void manualAndAutoIds() {
        Task manualTask = new Task("Manual", "Desc", TaskStatus.NEW);
        manualTask.setId(100);
        taskManager.createTask(manualTask);

        Task autoTask = taskManager.createTask(new Task("Auto", "Desc", TaskStatus.NEW));

        assertNotEquals(manualTask.getId(), autoTask.getId(), "ID не должны совпадать");
    }

    //Задача не изменяется при добавлении.
    @Test
    void taskRemainsUnchanged() {
        Task original = new Task("Original", "Desc", TaskStatus.NEW);
        Task added = taskManager.createTask(original);

        assertEquals(original.getName(), added.getName(), "Имя не должно изменяться");
        assertEquals(original.getDescription(), added.getDescription(), "Описание не должно изменяться");
        assertEquals(original.getTaskStatus(), added.getTaskStatus(), "Статус не должен изменяться");
    }

    // Удаление задачи удаляет её из истории.
    @Test
    void deleteTaskRemovesFromHistory() {
        Task task = taskManager.createTask(new Task("Task", "Desc", TaskStatus.NEW));
        taskManager.getTask(task.getId());
        taskManager.deleteTask(task.getId());

        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления задачи");
    }

    // Удаление эпика удаляет его подзадачи из истории.
    @Test
    void deleteEpicRemovesSubtasksFromHistory() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Desc"));
        Subtask subtask = taskManager.createSubtask(
                new Subtask("Subtask", "Desc", TaskStatus.NEW, epic.getId()));
        taskManager.getSubtasks(subtask.getId());
        taskManager.getEpic(epic.getId());
        taskManager.deleteEpic(epic.getId());

        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления эпика");
    }

    // Удаляем только таски.
    @Test
    void deleteAllTasksRemovesOnlyTasks() {
        Task task = taskManager.createTask(new Task("Task", "Desc", TaskStatus.NEW));
        Epic epic = taskManager.createEpic(new Epic("Epic", "Desc"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Sub", "Desc", TaskStatus.NEW, epic.getId()));

        taskManager.deleteAllTasks();

        assertTrue(taskManager.getTasks().isEmpty(), "Все задачи должны быть удалены");
        assertFalse(taskManager.getEpics().isEmpty(), "Эпики должны остаться");
        assertFalse(taskManager.getSubtasks().isEmpty(), "Подзадачи должны остаться");
    }

    // Удаляем только сабтаски.
    @Test
    void deleteAllSubtasksRemovesOnlySubtasks() {
        Task task = taskManager.createTask(new Task("Task", "Desc", TaskStatus.NEW));
        Epic epic = taskManager.createEpic(new Epic("Epic", "Desc"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Sub", "Desc", TaskStatus.NEW, epic.getId()));

        taskManager.deleteAllSubtasks();

        assertFalse(taskManager.getTasks().isEmpty(), "Задачи должны остаться");
        assertFalse(taskManager.getEpics().isEmpty(), "Эпики должны остаться");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Все подзадачи должны быть удалены");
    }

    // Удаляем эпики и их сабтаски.
    @Test
    void deleteAllEpicsRemovesEpicsAndSubtasks() {
        Task task = taskManager.createTask(new Task("Task", "Desc", TaskStatus.NEW));
        Epic epic = taskManager.createEpic(new Epic("Epic", "Desc"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Sub", "Desc", TaskStatus.NEW, epic.getId()));

        taskManager.deleteAllEpics();

        assertFalse(taskManager.getTasks().isEmpty(), "Задачи должны остаться");
        assertTrue(taskManager.getEpics().isEmpty(), "Все эпики должны быть удалены");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Все подзадачи должны быть удалены");
    }

    // Удаляем всё.
    @Test
    void deleteAllRemovesEverything() {
        Task task = taskManager.createTask(new Task("Task", "Desc", TaskStatus.NEW));
        Epic epic = taskManager.createEpic(new Epic("Epic", "Desc"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Sub", "Desc", TaskStatus.NEW, epic.getId()));

        taskManager.deleteAll();

        assertTrue(taskManager.getTasks().isEmpty(), "Все задачи должны быть удалены");
        assertTrue(taskManager.getEpics().isEmpty(), "Все эпики должны быть удалены");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Все подзадачи должны быть удалены");
    }
}



