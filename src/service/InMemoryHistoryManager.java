package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> taskHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (taskHistory.size() < 10) {
            taskHistory.add(task);
        } else {
            taskHistory.remove(0);
            taskHistory.add(task);
        }
    }

    @Override
    public void getHistory() {
        System.out.println("Просмотренные задачи:");
        for (Task task : taskHistory) {
            System.out.println(task.getName());
        }
    }
}
