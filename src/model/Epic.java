package model;

import util.TaskStatus;
import util.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTaskIds;

    public Epic(String name, String description) {
        super(name, description);
        subTaskIds = new ArrayList<>();
    }

    public Epic(int id, String name, TaskStatus status, String description, List<Integer> subTaskIds) {
        super(id, name, status, description);
        this.subTaskIds = subTaskIds;
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubTaskById(int id) {
        subTaskIds.add(id);
    }

    public void removeSubTaskById(Integer id) {
        subTaskIds.remove(id);
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status='" + getStatus().getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subTaskIds=" + subTaskIds +
                "}";
    }
}
