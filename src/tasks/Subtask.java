package tasks;

import java.time.Duration;
import java.time.LocalDateTime;


public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, TaskStatus taskStatus, int epicId) {
        super(name, description, taskStatus);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, TaskStatus taskStatus, int epicId) {
        super(id, name, description, taskStatus);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatus taskStatus, int epicId,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, taskStatus, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, TaskStatus taskStatus, int epicId,
                   LocalDateTime startTime, Duration duration) {
        super(id, name, description, taskStatus, startTime, duration);
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
