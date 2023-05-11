package utils;

import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

public final class Managers {

    TaskManager taskManager = new InMemoryTaskManager();

    HistoryManager historyManager = new InMemoryHistoryManager();


    public TaskManager getDefault() {
        return taskManager;
    }

    public HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
