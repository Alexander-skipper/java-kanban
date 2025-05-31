package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int generatorId = 1;
    private HistoryManager historyManager = Managers.getDefaultHistory();


    // Методы для Task
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }


    @Override
    public Task createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return null;
        }
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task deleteTask(int id) {
        return tasks.remove(id);
    }

    // Методы для Subtask
    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtasks(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null || !epics.containsKey(subtask.getEpicId())) {
            return null;
        }

        // Проверка для случая, когда ID подзадачи был установлен вручную
        if (subtask.getId() != null) {
            if (subtask.getId() == subtask.getEpicId()) {
                return null;
            }
            if (epics.containsKey(subtask.getId())) {
                return null;
            }
        }

        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic);

        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return null;
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
        return subtask;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
            }
        }
        return subtask;
    }

    // Методы для Epic
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return null;
        }
        Epic existingEpic = epics.get(epic.getId());
        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
        return existingEpic;
    }

    @Override
    public Epic deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasksId()) {
                subtasks.remove(subtaskId);
            }

        }
        return epic;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        ArrayList<Subtask> result = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                result.add(subtask);
            }
        }
        return result;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        generatorId = 1;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void addHistory(Task task) {
        historyManager.add(task);
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtasksId();
        if (subtaskIds.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }
        boolean allDone = true;
        boolean allNew = true;

        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;

            if (!Objects.equals(subtask.getTaskStatus(), TaskStatus.DONE)) {
                allDone = false;
            }
            if (!Objects.equals(subtask.getTaskStatus(), TaskStatus.NEW)) {
                allNew = false;
            }
        }
        if (allDone) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private int getNextId() {
        return generatorId++;
    }

}

