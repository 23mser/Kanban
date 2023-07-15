package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, "01.01.3000 00:00", 0);
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subtasks.isEmpty()) {
            return DEFAULT_START;
        }
        else if (subtasks.size() == 1) {
            return subtasks.get(0).getStartTime();
        }
        subtasks.sort((s1, s2) -> {
            if (s1.getStartTime().isAfter(s2.getStartTime())) {
                return 1;
            } else if (s1.getStartTime().isBefore(s2.getStartTime())) {
                return -1;
            } else {
                return 0;
            }
        });
        int i = 0;
        while (subtasks.get(i).getStartTime().equals(DEFAULT_START)) {
            i++;
            if (i == subtasks.size()) {
                return subtasks.get(i - 1).getStartTime();
            }
        }
        return subtasks.get(i).getStartTime();
    }

    @Override
    public void setStartTime(String startTime) {
        super.setStartTime(startTime);
    }

    @Override
    public Duration getDuration() {
        Duration duration = Duration.ofMinutes(0);
        for (Subtask subtask : subtasks) {
            duration = duration.plus(subtask.getDuration());
        }
        return duration;
    }

    @Override
    public void setDuration(int duration) {
        super.setDuration(duration);
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subtasks.isEmpty()) {
            return startTime;
        } else if (subtasks.size() == 1) {
            return subtasks.get(0).getEndTime();
        }
        subtasks.sort((s1, s2) -> {
            if (s1.getStartTime().isAfter(s2.getStartTime())) {
                return 1;
            } else if (s1.getStartTime().isBefore(s2.getStartTime())) {
                return -1;
            } else {
                return 0;
            }
        });
        int i = subtasks.size()-1;
        while (subtasks.get(i).getEndTime().equals(startTime)) {
            i--;
            if (i == -1) {
                return subtasks.get(0).getStartTime();
            }
        }
        return subtasks.get(i).getEndTime();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(name, epic.name) &&
                Objects.equals(description, epic.description) &&
                Objects.equals(status, epic.status);
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
}