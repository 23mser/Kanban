package test;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;
import utils.ManagerSaveException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    HistoryManager historyManager;
    TaskManager taskManager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void start() throws ManagerSaveException {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager();
        task = taskManager.createNewTask(new Task("tn", "td", "12.01.2000 12:00", 10));
        epic = taskManager.createNewEpic(new Epic("en", "ed"));
        subtask = taskManager.
                createNewSubtask(epic, new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
    }

    @Test
    void add() {
        assertEquals(0, historyManager.getHistory().size());
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));
        historyManager.add(epic);
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(epic, historyManager.getHistory().get(1));
        historyManager.add(subtask);
        assertEquals(3, historyManager.getHistory().size());
        assertEquals(subtask, historyManager.getHistory().get(2));
        historyManager.add(task);
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    void remove() {
        assertEquals(0, historyManager.getHistory().size());
        historyManager.remove(1);
        assertEquals(0, historyManager.getHistory().size());
        historyManager.add(epic);
        assertEquals(1, historyManager.getHistory().size());
        historyManager.remove(1);
        assertEquals(1, historyManager.getHistory().size());
        historyManager.remove(2);
        assertEquals(0, historyManager.getHistory().size());
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        assertEquals(3, historyManager.getHistory().size());
        historyManager.remove(3);
        assertEquals(2, historyManager.getHistory().size());
        historyManager.remove(1);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void getHistory() {
        assertEquals(0, historyManager.getHistory().size());
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(task, history.get(0));
        historyManager.add(epic);
        history = historyManager.getHistory();
        assertEquals(epic, history.get(1));
        historyManager.add(subtask);
        history = historyManager.getHistory();
        assertEquals(subtask, history.get(2));
        assertEquals(3, historyManager.getHistory().size());
        historyManager.add(task);
        assertEquals(3, historyManager.getHistory().size());
    }
}