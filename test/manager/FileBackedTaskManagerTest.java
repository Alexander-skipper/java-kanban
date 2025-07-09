package manager;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;



public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;


    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("data", ".csv");
            return new FileBackedTaskManager(tempFile, Managers.getDefaultHistory());
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл", e);
        }
    }

    @AfterEach
    void remove() {
        tempFile.delete();
    }

    @Test
    void testSaveAndLoadEmptyManager() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
        assertTrue(loadedManager.getSubtasks().isEmpty());
    }

    @Test
    void testSaveAndLoadTasks() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        taskManager.createTask(task);
        int taskId = task.getId();

        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask("Subtask 1", "Subtask desc", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        int subtaskId = subtask.getId();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Task loadedTask = loadedManager.getTask(taskId);
        assertNotNull(loadedTask);
        assertEquals("Task 1", loadedTask.getName());

        Epic loadedEpic = loadedManager.getEpic(epicId);
        assertNotNull(loadedEpic);
        assertEquals("Epic 1", loadedEpic.getName());

        Subtask loadedSubtask = loadedManager.getSubtasks(subtaskId);
        assertNotNull(loadedSubtask);
        assertEquals("Subtask 1", loadedSubtask.getName());

    }

    @Test
    void testTaskUpdateAndSave() {
        Task task = new Task("Original task", "Desc", TaskStatus.NEW);
        taskManager.createTask(task);
        int taskId = task.getId();

        task.setName("Updated task");
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Task loadedTask = loadedManager.getTask(taskId);
        assertEquals("Updated task", loadedTask.getName());
        assertEquals(TaskStatus.IN_PROGRESS, loadedTask.getTaskStatus());
    }

    @Test
    void testEpicStatusAfterLoading() {
        Epic epic = new Epic("Epic", "Epic desc");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask("Subtask", "Desc", TaskStatus.NEW, epicId);
        taskManager.createSubtask(subtask);
        int subtaskId = subtask.getId();

        subtask.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(TaskStatus.DONE, loadedManager.getEpic(epicId).getTaskStatus());
    }

    @Test
    void testDeleteTaskAndSave() {
        Task task = new Task("Task to delete", "Desc", TaskStatus.NEW);
        taskManager.createTask(task);
        int taskId = task.getId();

        taskManager.deleteTask(taskId);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertNull(loadedManager.getTask(taskId));
        assertTrue(loadedManager.getTasks().isEmpty());
    }

    @Test
    void testLoadFromNonExistentFile() {
        File nonExistentFile = new File("non_existent_file.csv");
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(nonExistentFile);

        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
        assertTrue(loadedManager.getSubtasks().isEmpty());
    }

    @Test
    void testSaveAfterDeletingAllTasks() {
        Task task1 = new Task("Task 1", "Desc", TaskStatus.NEW);
        taskManager.createTask(task1);
        int task1Id = task1.getId();

        Task task2 = new Task("Task 2", "Desc", TaskStatus.NEW);
        taskManager.createTask(task2);
        int task2Id = task2.getId();

        taskManager.deleteAllTasks();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertNull(loadedManager.getTask(task1Id));
        assertNull(loadedManager.getTask(task2Id));
        assertTrue(loadedManager.getTasks().isEmpty());
    }
}