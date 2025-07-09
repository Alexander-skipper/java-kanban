package manager;

import tasks.*;
import manager.exceptions.ManagerSaveException;
import manager.exceptions.ManagerLoadException;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;


    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        this.file = file;
        this.historyManager = historyManager;
    }

    // Загрузка данных из файла.
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file, Managers.getDefaultHistory());

        if (file.exists() && file.length() > 0) {
            manager.load();
        }
        return manager;
    }



    // Переопределенные методы Task
    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Task deleteTask(int id) {
        Task deletedTask = super.deleteTask(id);
        save();
        return deletedTask;
    }

    // Переопределенные методы Subtask
    @Override
    public Subtask getSubtasks(int id) {
        Subtask subtask = super.getSubtasks(id);
        save();
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask deletedSubtask = super.deleteSubtask(id);
        save();
        return deletedSubtask;
    }

    // Переопределенные методы Epic
    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public Epic deleteEpic(int id) {
        Epic deletedEpic = super.deleteEpic(id);
        save();
        return deletedEpic;
    }

    // Переопределенные методы удаления всех задач
    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        save();
    }


    // Загрузка данных
        private void load() {
            try {
                String content = Files.readString(file.toPath());
                String[] lines = content.split("\n");

                if (lines.length < 2) return;

                int maxId = 0;

                for (int i = 1; i < lines.length; i++) {
                    Task task = CsvFormatter.fromString(lines[i]);
                    if (task == null) continue;

                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }

                    switch (task.getType()) {
                        case TASK:
                            tasks.put(task.getId(), task);
                            break;
                        case EPIC:
                            epics.put(task.getId(), (Epic) task);
                            break;
                        case SUBTASK:
                            Subtask subtask = (Subtask) task;
                            subtasks.put(task.getId(), subtask);
                            Epic epic = epics.get(subtask.getEpicId());
                            if (epic != null) {
                                epic.addSubtaskId(subtask.getId());
                            }
                            break;
                    }
                }
                generatorId = maxId + 1;
            } catch (IOException e) {
                throw new ManagerLoadException("Не удалось загрузить задачи из файла: " + file.getPath(), e);
            }
        }

    // Сохранение данных в файл
    private void save() throws ManagerSaveException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(CsvFormatter.CSV_HEADER + "\n"); // Запись заголовка
            for (Task task : getAllTasks()) {
                writer.write(CsvFormatter.toString(task) + "\n"); // Запись задач
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла", e);
        }
    }

    // Получение всех задач.
    private List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(getTasks());
        tasks.addAll(getEpics());
        tasks.addAll(getSubtasks());
        return tasks;
    }
}


