package manager;

import manager.exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Objects;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected int generatorId = 1;
    protected HistoryManager historyManager;
    private final Set<Task> prioritizedTasks = new TreeSet<>((t1, t2) -> {
        if (t1.getStartTime() == null && t2.getStartTime() == null) return t1.getId() - t2.getId();
        if (t1.getStartTime() == null) return 1;
        if (t2.getStartTime() == null) return -1;
        int timeCompare = t1.getStartTime().compareTo(t2.getStartTime());
        return timeCompare != 0 ? timeCompare : t1.getId() - t2.getId();
    });

    protected void addToPrioritizedTasks(Task task) {
        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    protected Set<Task> getPrioritizedTasksSet() {
        return new HashSet<>(prioritizedTasks);
    }

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }


    // Методы для Task
    @Override
    public List<Task> getTasks() {

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
        if (hasTaskIntersections(task)) throw new  ManagerSaveException("Задача пересекается по времени");
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) return null;
        if (hasTaskIntersections(task)) throw new ManagerSaveException("Задача пересекается по времени");
        Task oldTask = tasks.get(task.getId());
        prioritizedTasks.remove(oldTask);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Task deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
        return task;
    }

    // Методы для Subtask
    @Override
    public List<Subtask> getSubtasks() {
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
        if (hasTaskIntersections(subtask)) throw new ManagerSaveException("Подзадача пересекается по времени");
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
        updateEpicTime(epic);
        if (subtask.getStartTime() != null) prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return null;
        }
        if (hasTaskIntersections(subtask)) throw new ManagerSaveException("Подзадача пересекается по времени");
        Subtask oldSubtask = subtasks.get(subtask.getId());
        prioritizedTasks.remove(oldSubtask);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
        if (subtask.getStartTime() != null) prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
            historyManager.remove(id);
        }
        return subtask;
    }

    // Методы для Epic
    @Override
    public List<Epic> getEpics() {
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
            epic.getSubtasksId().forEach(subtaskId -> {
                Subtask subtask = subtasks.remove(subtaskId);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                }
                historyManager.remove(subtaskId);
            });
            historyManager.remove(id);
        }
        return epic;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        List<Subtask> result = new ArrayList<>();
        epic.getSubtasksId().forEach(subtaskId -> {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) result.add(subtask);
        });
        return result;
    }

    @Override
    public Duration getEpicDuration(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null || epic.getSubtasksId().isEmpty()) return Duration.ZERO;

        long totalMinutes = 0;
        for (Integer subtaskId : epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null && subtask.getDuration() != null) {
                totalMinutes += subtask.getDuration().toMinutes();
            }
        }
        return Duration.ofMinutes(totalMinutes);
    }

    @Override
    public LocalDateTime getEpicStartTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null || epic.getSubtasksId().isEmpty()) return null;

        LocalDateTime earliest = null;
        for (Integer subtaskId : epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null && subtask.getStartTime() != null) {
                if (earliest == null || subtask.getStartTime().isBefore(earliest)) {
                    earliest = subtask.getStartTime();
                }
            }
        }
        return earliest;
    }

    @Override
    public LocalDateTime getEpicEndTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return null;
        return epic.getEndTime();
    }



    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(taskId -> historyManager.remove(taskId));
        tasks.values().forEach(task -> prioritizedTasks.remove(task));
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(subtaskId -> historyManager.remove(subtaskId));
        subtasks.values().forEach(subtask -> prioritizedTasks.remove(subtask));
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtasksId().clear();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        });
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        epics.keySet().forEach(epicId -> historyManager.remove(epicId));
        epics.clear();
    }

    @Override
    public void deleteAll() {
        deleteAllTasks();
        deleteAllSubtasks();
        deleteAllEpics();
        generatorId = 1;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean isTasksIntersect(Task task1, Task task2) {
        if (task1 == task2 || task1.getStartTime() == null || task2.getStartTime() == null
                || task1.getEndTime() == null || task2.getEndTime() == null) {
            return false;
        }
        return !task1.getStartTime().isAfter(task2.getEndTime()) &&
                !task2.getStartTime().isAfter(task1.getEndTime());
    }

    @Override
    public boolean hasTaskIntersections(Task newTask) {
        if (newTask.getStartTime() == null) return false;
        return getPrioritizedTasks().stream().anyMatch(existingTask ->
                existingTask.getId() != newTask.getId() &&
                        isTasksIntersect(existingTask, newTask)
        );
    }

    protected void updateEpicTime(Epic epic) {
        Optional<LocalDateTime> startTime = epic.getSubtasksId().stream()
                .map(id -> subtasks.get(id))
                .filter(subtask -> subtask != null)
                .map(subtask -> subtask.getStartTime())
                .filter(time -> time != null)
                .min((t1, t2) -> t1.compareTo(t2));

        Optional<LocalDateTime> endTime = epic.getSubtasksId().stream()
                .map(id -> subtasks.get(id))
                .filter(subtask -> subtask != null)
                .map(subtask -> subtask.getEndTime())
                .filter(time -> time != null)
                .max((t1, t2) -> t1.compareTo(t2));

        epic.setStartTime(startTime.orElse(null));
        epic.setEndTime(endTime.orElse(null));

    }


    protected void updateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtasksId();
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


