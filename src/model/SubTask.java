package model;

import util.TaskStatus;
import util.TaskType;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(Integer epicId, String name, String description) {
        super(name, description);
        this.epicId = epicId;
    }

    public SubTask(Integer epicId, int id, String name, TaskStatus status, String description) {
        super(id, name, status, description);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status='" + getStatus().getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                "}";
    }
}
