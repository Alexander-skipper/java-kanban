package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // Методы для Task
    ArrayList<Task> getTasks();

    Task getTask(int id);

    Task createTask(Task task);

    Task updateTask(Task task);

    Task deleteTask(int id);

    // Методы для Subtask
    ArrayList<Subtask> getSubtasks();

    Subtask getSubtasks(int id);

    Subtask createSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    Subtask deleteSubtask(int id);

    // Методы для Epic
    ArrayList<Epic> getEpics();

    Epic getEpic(int id);

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    Epic deleteEpic(int id);

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    void deleteAllTasks();

    List<Task> getHistory();
}
