package utils;

import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

public final class Managers {

    HistoryManager historyManager = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
