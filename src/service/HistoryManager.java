package service;

import model.Task;

public interface HistoryManager {

    void add(Task task);

    void getHistory();
}
