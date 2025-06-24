package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.List;

public interface TaskManager {
    // Методы для Task
    List<Task> getTasks();

    Task getTask(int id);

    Task createTask(Task task);

    Task updateTask(Task task);

    Task deleteTask(int id);

    // Методы для Subtask
    List<Subtask> getSubtasks();

    Subtask getSubtasks(int id);

    Subtask createSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    Subtask deleteSubtask(int id);

    // Методы для Epic
    List<Epic> getEpics();

    Epic getEpic(int id);

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    Epic deleteEpic(int id);

    List<Subtask> getEpicSubtasks(int epicId);

    // Метод для получения истории
    List<Task> getHistory();

    // Методы для удаления
    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    void deleteAll();
}
