package manager;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class CsvFormatter {

    public static final String CSV_HEADER = "id,type,name,status,description,epic, startTime, duration";


    // Преобразование задачи в CSV строку
    public static String toString(Task task) {
        if (task == null) return "";

        String epicField = "";
        if (task.getType() == TaskType.SUBTASK) {
            epicField = String.valueOf(((Subtask) task).getEpicId());
        }

        String startTime = task.getStartTime() != null ? task.getStartTime().toString() : "";
        String duration = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";

        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getTaskStatus().name(),
                task.getDescription(),
                epicField,
                startTime,
                duration);
    }

    // Создание задачи из CSV строки
    public static Task fromString(String value) {
        if (value == null || value.isEmpty()) return null;

        String[] fields = value.split(",");
        if (fields.length < 5) return null;

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        String epicIdStr = fields.length > 5 ? fields[5] : "";
        String startTimeStr = fields.length > 6 ? fields[6] : "";
        String durationStr = fields.length > 7 ? fields[7] : "";

        LocalDateTime startTime = !startTimeStr.isEmpty() ? LocalDateTime.parse(startTimeStr) : null;
        Duration duration = !durationStr.isEmpty() ? Duration.ofMinutes(Long.parseLong(durationStr)) : null;

        switch (type) {
            case TASK:
                Task task = new Task(id, name, description, status);
                task.setStartTime(startTime);
                task.setDuration(duration);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setTaskStatus(status);
                return epic;
            case SUBTASK:
                int epicId = !epicIdStr.isEmpty() ? Integer.parseInt(epicIdStr) : 0;
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                subtask.setStartTime(startTime);
                subtask.setDuration(duration);
                return subtask;
            default:
                return null;
        }
    }
}


