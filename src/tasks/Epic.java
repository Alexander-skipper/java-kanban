package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubtaskId(int subtaskId) {
        subtasksId.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtasksId.remove((Integer) subtaskId);
    }

    @Override
    public void setTaskStatus(TaskStatus taskStatus) {
        super.setTaskStatus(taskStatus);
    }
}
