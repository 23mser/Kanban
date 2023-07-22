package utils;

import http.HttpTaskManager;
import service.*;
import java.io.File;
import java.io.IOException;

public final class Managers {

    public static HttpTaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager();
    }

    public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFile(File file) throws IOException, ManagerSaveException {
        if (file == null || file.length() == 0) {
            return new FileBackedTasksManager(file);
        }
        return FileBackedTasksManager.loadFromFile(file);
    }
}
