package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import service.InMemoryTaskManager;
import utils.ManagerSaveException;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @AfterEach
    void afterEach() throws ManagerSaveException {
        taskManager.deleteAll();
        taskManager.getHistory().clear();
    }
}