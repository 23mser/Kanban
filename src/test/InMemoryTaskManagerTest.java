package test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @Test
    public void allListsOfTasksIsEmpty() {
        Assertions.assertEquals(0, taskManager.getAllTasks().size());
        Assertions.assertEquals(0, taskManager.getAllEpics().size());
        Assertions.assertEquals(0, taskManager.getAllSubtasks().size());
    }
}