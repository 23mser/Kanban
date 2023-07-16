package test;

import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import utils.ManagerSaveException;

public class EpicTest {

    private static InMemoryTaskManager taskManager;
    private static Epic epic;

    @BeforeEach
    void beforeEach() throws ManagerSaveException {
        taskManager = new InMemoryTaskManager();
        epic = taskManager.createNewEpic(new Epic("en", "ed"));
    }

    @AfterEach
    void afterEach() throws ManagerSaveException {
        taskManager.deleteAll();
        taskManager.getHistory().clear();
    }

    @Test
    public void emptyListOfSubtasks() {
        TaskStatus epicStatus = epic.getStatus();
        Assertions.assertEquals(TaskStatus.NEW, epicStatus);
    }

    @Test
    public void allSubtasksStatusIsNew() throws ManagerSaveException {
        taskManager.createNewSubtask(epic,
                new Subtask("n1", "d1", "02.01.2000 12:00", 10, 5));
        taskManager.createNewSubtask(epic,
                new Subtask("n2", "d2", "03.01.2000 12:00", 10, 5));
        taskManager.updateEpicStatus(epic);
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void allSubtasksStatusIsDone() throws ManagerSaveException {
        epic.setStatus(TaskStatus.NEW);
        Subtask subtask1 = taskManager.createNewSubtask(epic,
                new Subtask("n1", "d1", "02.01.2000 12:00", 10, 5));
        Subtask subtask2 = taskManager.createNewSubtask(epic,
                new Subtask("n2", "d2", "03.01.2000 12:00", 10, 10));
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateEpicStatus(epic);
        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void subtasksStatusIsNewAndDone() throws ManagerSaveException {
        taskManager.createNewSubtask(epic,
                new Subtask("n1", "d1", "02.01.2000 12:00", 10, 5));
        Subtask subtask2 = taskManager.createNewSubtask(epic,
                new Subtask("n2", "d2", "03.01.2000 12:00", 10, 10));
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateEpicStatus(epic);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void allSubtasksStatusIsInProgress() throws ManagerSaveException {
        Subtask subtask1 = taskManager.createNewSubtask(epic,
                new Subtask("n1", "d1", "02.01.2000 12:00", 10, 5));
        Subtask subtask2 = taskManager.createNewSubtask(epic,
                new Subtask("n2", "d2","03.01.2000 12:00", 10, 10));
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpicStatus(epic);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
} 