package test;

import http.HttpTaskManager;
import http.HttpTaskServer;
import http.KVServer;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ManagerSaveException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest {
    KVServer kvServer;
    HttpTaskServer server;
    HttpTaskManager fileBacked;

    @BeforeEach
    protected void start() throws IOException, InterruptedException, ManagerSaveException {
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer(new HttpTaskManager());
        server.start();
        fileBacked = server.getFileBacked();
        fileBacked.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));
        Epic epic = fileBacked.createNewEpic(new Epic("en", "ed"));
        fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
    }

    @AfterEach
    public void serverStop() {
        kvServer.stop();
        server.stop();
    }

    @Test
    public void shouldLoadManagerAfterSave() throws ManagerSaveException {

        assertEquals(1, fileBacked.getAllTasks().size());
        assertEquals(1, fileBacked.getAllEpics().size());
        assertEquals(1, fileBacked.getAllSubtasks().size());
        fileBacked.save();

        HttpTaskManager newManager = server.getFileBacked();
        newManager.createNewTask(new Task("tn", "td"));
        assertEquals(2, newManager.getAllTasks().size());
        assertEquals(1, newManager.getAllEpics().size());
        assertEquals(1, newManager.getAllSubtasks().size());
    }

    @Test
    public void shouldLoadManagerAfterSaveAndDelete() {
        assertEquals(1, fileBacked.getAllTasks().size());
        assertEquals(1, fileBacked.getAllEpics().size());
        assertEquals(1, fileBacked.getAllSubtasks().size());

        fileBacked.deleteAllTasks();

        HttpTaskManager newManager = server.getFileBacked();
        assertEquals(0, newManager.getAllTasks().size());
        assertEquals(1, newManager.getAllEpics().size());
        assertEquals(1, newManager.getAllSubtasks().size());
    }

    @Test
    public void shouldLoadManagerWithHistory() throws ManagerSaveException {
        assertEquals(0, fileBacked.getHistory().size());

        fileBacked.getTask(1);
        fileBacked.getEpic(2);
        fileBacked.getSubtask(3);

        HttpTaskManager newManager = server.getFileBacked();
        assertEquals(3, newManager.getHistory().size());
    }

    @Test
    public void shouldLoadNewManagerEqualsManager() {
        HttpTaskManager newManager = server.getFileBacked();
        assertEquals(fileBacked, newManager);
    }
}