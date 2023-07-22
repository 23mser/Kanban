package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    public int id;
    public String name;
    public String description;
    public TaskStatus status;
    public LocalDateTime startTime;
    public Duration duration;

    public TaskType taskType;
    public static final LocalDateTime DEFAULT_START = LocalDate.of(3000, 1, 1).atStartOfDay();
    public static final Duration DEFAULT_DURATION = Duration.ofMinutes(0);

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.startTime = DEFAULT_START;
        this.duration = DEFAULT_DURATION;
    }

    public Task(String name, String description, String startTime, int duration) {
        this.name = name;
        this.description = description;
        this.startTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(String name, String description, TaskStatus status, String startTime, int duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        this.duration = Duration.ofMinutes(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (name != null) {
            hash = hash + name.hashCode();
        }
        hash = hash * 31;

        if (description != null) {
            hash = hash + description.hashCode();
        }

        if (status != null) {
            hash = hash + status.hashCode();
        }
        return hash;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = Duration.ofMinutes(duration);
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

}

