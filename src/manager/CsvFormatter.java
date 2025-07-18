package manager;
import tasks.*;

public class CsvFormatter {

    public static final String CSV_HEADER = "id,type,name,status,description,epic";


    // Преобразование задачи в CSV строку
    public static String toString(Task task) {
        if (task == null) return "";

        String epicField = "";
        if (task.getType() == TaskType.SUBTASK) {
            epicField = String.valueOf(((Subtask) task).getEpicId());
        }

        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getTaskStatus().name(),
                task.getDescription(),
                epicField);
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

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setTaskStatus(status);
                return epic;
            case SUBTASK:
                int epicId = fields.length > 5 ? Integer.parseInt(fields[5]) : 0;
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                return null;
        }
    }
}


