package service;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> taskHistory = new LinkedList<>();

    private static final int MAX_HISTORY_SIZE = 10;


    @Override
    public void add(Task task) {
        if (taskHistory.size() < MAX_HISTORY_SIZE) {
            taskHistory.add(task);
        } else {
            taskHistory.remove(0);
            taskHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}
