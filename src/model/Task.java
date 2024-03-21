package model;

import util.TaskStatus;
import util.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class Task {
    private int id;
    private String name;
    private TaskStatus status;
    private String description;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.status = TaskStatus.NEW;
        this.description = description;
        this.startTime = null;
        this.duration = null;
    }

    public Task(String name, String description, LocalDateTime startTime, Long duration) {
        this.name = name;
        this.status = TaskStatus.NEW;
        this.description = description;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(int id, String name, TaskStatus status, String description, LocalDateTime startTime, Long duration) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEpicId() {
        return null;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Long getDuration() {
        return Optional.ofNullable(duration)
                .map(Duration::toMinutes)
                .orElse(0L);
    }

    public void setDuration(Long minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return (startTime != null && duration != null) ? startTime.plus(duration) : null;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status.getName() +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
