package test;

import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import service.InMemoryTaskManager;
import utils.ManagerSaveException;

public class EpicTest {

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    Epic epic;
    {
        try {
            epic = inMemoryTaskManager.createNewEpic(new Epic("name", "description"));
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void emptyListOfSubtasks() {
        TaskStatus epicStatus = epic.getStatus();
        Assertions.assertEquals(TaskStatus.NEW, epicStatus);
    }

    @Test
    public void allSubtasksStatusIsNew() throws ManagerSaveException {
        inMemoryTaskManager.createNewSubtask(epic,
                new Subtask("n1", "d1", "02.01.2000 12:00", 10, 5));
        inMemoryTaskManager.createNewSubtask(epic,
                new Subtask("n2", "d2", "03.01.2000 12:00", 10, 5));
        inMemoryTaskManager.updateEpicStatus(epic);
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void allSubtasksStatusIsDone() throws ManagerSaveException {
        epic.setStatus(TaskStatus.NEW);
        Subtask subtask1 = inMemoryTaskManager.createNewSubtask(epic,
                new Subtask("n1", "d1", "02.01.2000 12:00", 10, 5));
        Subtask subtask2 = inMemoryTaskManager.createNewSubtask(epic,
                new Subtask("n2", "d2", "03.01.2000 12:00", 10, 10));
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateEpicStatus(epic);
        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void subtasksStatusIsNewAndDone() throws ManagerSaveException {
        inMemoryTaskManager.createNewSubtask(epic,
                new Subtask("n1", "d1", "02.01.2000 12:00", 10, 5));
        Subtask subtask2 = inMemoryTaskManager.createNewSubtask(epic,
                new Subtask("n2", "d2", "03.01.2000 12:00", 10, 10));
        subtask2.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateEpicStatus(epic);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void allSubtasksStatusIsInProgress() throws ManagerSaveException {
        Subtask subtask1 = inMemoryTaskManager.createNewSubtask(epic,
                new Subtask("n1", "d1", "02.01.2000 12:00", 10, 5));
        Subtask subtask2 = inMemoryTaskManager.createNewSubtask(epic,
                new Subtask("n2", "d2","03.01.2000 12:00", 10, 10));
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        inMemoryTaskManager.updateEpicStatus(epic);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
}
