package utils;

import service.*;
import java.io.File;
import java.io.IOException;

public final class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
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
