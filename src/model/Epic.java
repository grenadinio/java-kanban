package model;

import service.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        subTasks = new ArrayList<>();
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);
    }

    public void calculateEpicStatus() {
        boolean allNew = true;
        boolean allDone = true;

        for (SubTask subTask : subTasks) {
            switch (subTask.getStatus()) {
                case IN_PROGRESS:
                    allNew = false;
                    allDone = false;
                    break;
                case NEW:
                    allDone = false;
                    break;
                case DONE:
                    allNew = false;
                    break;
            }

            if (!allNew && !allDone) {
                break;
            }
        }

        if (allNew || subTasks.isEmpty()) {
            this.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            this.setStatus(TaskStatus.DONE);
        } else {
            this.setStatus(TaskStatus.IN_PROGRESS);
        }

    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subTasks=" + subTasks +
                "}";
    }
}
