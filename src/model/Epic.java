package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTaskIds;

    public Epic(String name, String description) {
        super(name, description);
        subTaskIds = new ArrayList<>();
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubTaskById(int id) {
        subTaskIds.add(id);
    }

    public void removeSubTaskById(int id) {
        subTaskIds.remove(id);
    }


    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subTaskIds=" + subTaskIds +
                "}";
    }
}
