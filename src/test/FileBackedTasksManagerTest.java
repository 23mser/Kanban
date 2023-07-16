package test;

import model.Epic;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;
import utils.ManagerSaveException;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    File fileToSave;

    @BeforeEach
    void beforeEach() {
        fileToSave = new File("FileBackedTasksManager.csv");
        taskManager = new FileBackedTasksManager(fileToSave);
    }

    @AfterEach
    void afterEach() throws ManagerSaveException {
        taskManager.deleteAll();
        taskManager.getHistory().clear();
    }

    @Test
    public void saveWhenListIsEmpty() throws ManagerSaveException, IOException {
        taskManager = FileBackedTasksManager.loadFromFile(fileToSave);

        assertEquals(0, taskManager.getAllTasks().size());
        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());
        assertEquals(0, taskManager.getHistory().size());

        taskManager.save();
        if (fileToSave != null) {
            assertTrue(fileToSave.isFile());
        }
    }

    @Test
    public void saveWhenEpicWithoutSubtasks() throws ManagerSaveException, IOException {
        Epic epic = taskManager.createNewEpic(new Epic("en", "ed"));

        assertTrue(taskManager.getAllEpics().containsValue(epic));

        taskManager.getEpic(epic.getId());
        taskManager.save();
        taskManager.deleteAllEpics();
        FileBackedTasksManager taskManager1 = FileBackedTasksManager.loadFromFile(fileToSave);

        assertEquals(1, taskManager1.getAllEpics().size());
        assertEquals(1, taskManager1.getHistory().size());
    }

    @Test
    public void saveWhenHistoryIsEmpty() throws ManagerSaveException, IOException {
        taskManager.createNewTask(new Task("tn", "td", "12.01.2000 12:00", 10));
        taskManager.save();

        assertEquals(0, taskManager.getHistory().size());

        FileBackedTasksManager taskManager1 = FileBackedTasksManager.loadFromFile(fileToSave);

        assertEquals(0, taskManager1.getHistory().size());
    }

    @Test
    public void loadWhenListIsEmpty() throws ManagerSaveException, IOException {
        assertEquals(0, taskManager.getAllTasks().size());
        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());

        FileBackedTasksManager taskManager1 = FileBackedTasksManager.loadFromFile(fileToSave);

        assertEquals(0, taskManager1.getAllTasks().size());
        assertEquals(0, taskManager1.getAllEpics().size());
        assertEquals(0, taskManager1.getAllSubtasks().size());
    }

    @Test
    public void loadWhenEpicWithoutSubtasks() throws ManagerSaveException, IOException {
        Epic epic = taskManager.createNewEpic(new Epic("en", "ed"));

        assertTrue(taskManager.getAllEpics().containsValue(epic));

        taskManager.getEpic(epic.getId());
        taskManager.save();
        taskManager.deleteAllEpics();
        FileBackedTasksManager taskManager1 = FileBackedTasksManager.loadFromFile(fileToSave);

        assertEquals(1, taskManager1.getAllEpics().size());
        assertEquals(1, taskManager1.getHistory().size());
    }

    @Test
    public void loadWhenHistoryIsEmpty() throws ManagerSaveException, IOException {
        assertEquals(0, taskManager.getHistory().size());

        FileBackedTasksManager taskManager1 = FileBackedTasksManager.loadFromFile(fileToSave);

        assertEquals(0, taskManager1.getHistory().size());
    }

}