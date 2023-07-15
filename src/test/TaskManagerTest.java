package test;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import service.TaskManager;
import utils.ManagerSaveException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;

    @Test
    void createNewTask() throws ManagerSaveException {
        Task task = new Task("tn", "td", "01.01.2000 12:00", 10);
        final int taskId = taskManager.createNewTask(task).getId();

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final HashMap<Integer, Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(taskId), "Задачи не совпадают.");
    }

    @Test
    void createNewEpic() throws ManagerSaveException {
        Epic epic = new Epic("en", "ed");
        final int epicId = taskManager.createNewEpic(epic).getId();

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Подзадача не найдена.");
        assertEquals(epic, savedEpic, "Подзадачи не совпадают.");

        final HashMap<Integer, Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(epicId), "Эпики не совпадают.");
    }

    @Test
    void createNewSubtask() throws ManagerSaveException {
        Epic epic = new Epic("en", "ed");
        final int epicId = taskManager.createNewEpic(epic).getId();

        Subtask subtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));

        final Subtask savedSubtask = taskManager.getSubtask(subtask.getId());
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertNotNull(savedEpic, "Эпик не найден.");

        final HashMap<Integer, Epic> epics = taskManager.getAllEpics();
        final HashMap<Integer, Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(subtask.getId()), "Подадачи не совпадают.");
        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(epicId), "Эпики не совпадают.");
        assertTrue(epic.getSubtasks().contains(subtask), "Эпик не содержит подзадачи.");
    }

    @Test
    void getAllTasks() throws ManagerSaveException {
        Task task = taskManager.getAllTasks().get(0);

        assertNull(task);

        Task secondTask = taskManager.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));

        assertEquals(1, taskManager.getAllTasks().size(), "Задач больше или меньше одной.");
        assertTrue(taskManager.getAllTasks().containsValue(secondTask), "Задача отсутствует.");
    }

    @Test
    void getAllEpics() throws ManagerSaveException {
        Epic epic = taskManager.getAllEpics().get(0);

        assertNull(epic);

        Epic secondEpic = taskManager.createNewEpic(new Epic("en", "ed"));

        assertEquals(1, taskManager.getAllEpics().size(), "Эпиков больше или меньше одного.");
        assertTrue(taskManager.getAllEpics().containsValue(secondEpic), "Эпик отсутствует.");
    }

    @Test
    void getAllSubtasks() throws ManagerSaveException {
        Epic epic = new Epic("en", "ed");
        Subtask subtask = taskManager.getAllSubtasks().get(0);

        assertNull(subtask);

        final int epicId = taskManager.createNewEpic(epic).getId();
        Subtask secondSubtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epicId));

        assertEquals(1, taskManager.getAllSubtasks().size(), "Подзадач больше или меньше одной.");
        assertTrue(taskManager.getAllSubtasks().containsValue(secondSubtask), "Подзадача отсутствует.");
    }

    @Test
    void deleteAll() throws ManagerSaveException {
        assertEquals(0, taskManager.getAllTasks().size());
        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());

        Task task = taskManager.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));
        Epic epic = taskManager.createNewEpic(new Epic("en", "ed"));
        Subtask subtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));

        assertEquals(task, taskManager.getTask(task.getId()));
        assertEquals(epic, taskManager.getEpic(epic.getId()));
        assertEquals(subtask, taskManager.getSubtask(subtask.getId()));

        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(1, taskManager.getAllEpics().size());
        assertEquals(1, taskManager.getAllSubtasks().size());

        taskManager.deleteAll();

        assertEquals(0, taskManager.getAllTasks().size());
        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void deleteAllTasks() throws ManagerSaveException {
        assertEquals(0, taskManager.getAllTasks().size());

        Task task = taskManager.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));
        Task secondTask = taskManager.createNewTask(new Task("tn", "td", "02.01.2000 12:00", 10));

        assertEquals(2, taskManager.getAllTasks().size(), "Список должен содержать 2 задачи.");
        assertEquals(task, taskManager.getTask(task.getId()), "Задача не совпадает.");
        assertEquals(secondTask, taskManager.getTask(secondTask.getId()), "Задача не совпадает.");

        taskManager.deleteAllTasks();

        assertNull(taskManager.getTask(task.getId()), "Задача не удалена.");
        assertNull(taskManager.getTask(secondTask.getId()), "Задача не удалена.");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список задач должен быть пустым.");
    }

    @Test
    void deleteAllEpics() throws ManagerSaveException {
        assertEquals(0, taskManager.getAllEpics().size());

        Epic epic = taskManager.createNewEpic(new Epic("en", "ed"));
        Epic secondEpic = taskManager.createNewEpic(new Epic("en", "ed"));
        Subtask subtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
        Subtask secondSubtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 13:00", 10, epic.getId()));

        assertEquals(2, taskManager.getAllEpics().size(), "Список должен содержать 2 эпика.");
        assertEquals(2, taskManager.getAllSubtasks().size(), "Список должен содержать 2 подзадачи.");
        assertEquals(epic, taskManager.getEpic(epic.getId()), "Эпик не совпадает.");
        assertEquals(secondEpic, taskManager.getEpic(secondEpic.getId()), "Эпик не совпадает.");

        taskManager.deleteAllEpics();

        assertNull(taskManager.getEpic(epic.getId()), "Эпик не удален.");
        assertNull(taskManager.getEpic(secondEpic.getId()), "Эпик не удален.");
        assertNull(taskManager.getSubtask(subtask.getId()), "Подзадача эпика не удалена.");
        assertNull(taskManager.getSubtask(secondSubtask.getId()), "Подзадача эпика не удалена.");
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков должен быть пустым.");
    }

    @Test
    void deleteAllSubtasks() throws ManagerSaveException {
        assertEquals(0, taskManager.getAllSubtasks().size());

        Epic epic = taskManager.createNewEpic(new Epic("en", "ed"));
        Subtask subtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
        Subtask secondSubtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 13:00", 10, epic.getId()));

        assertEquals(2, taskManager.getAllSubtasks().size(), "Список должен содержать 2 подзадачи.");
        assertEquals(1, taskManager.getAllEpics().size(), "Список должен содержать 1 эпик.");
        assertEquals(subtask, taskManager.getSubtask(subtask.getId()), "Подзадача не совпадает.");
        assertEquals(secondSubtask, taskManager.getSubtask(secondSubtask.getId()), "Подзадача не совпадает.");

        taskManager.deleteAllSubtasks();

        assertNull(taskManager.getSubtask(1), "Подзадача не удалена.");
        assertNull(taskManager.getSubtask(2), "Подзадача не удалена.");
        assertEquals(1, taskManager.getAllEpics().size(), "Эпик не должен быть удален.");
    }

    @Test
    void getTask() throws ManagerSaveException {
        assertEquals(0, taskManager.getAllTasks().size());

        Task task = taskManager.getAllTasks().get(0);

        assertNull(task);

        Task secondTask = taskManager.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));

        assertEquals(secondTask, taskManager.getTask(secondTask.getId()), "Задачи не совпадают.");
        assertEquals(1, taskManager.getAllTasks().size(), "Неверное количество задач.");
    }

    @Test
    void getEpic() throws ManagerSaveException {
        assertEquals(0, taskManager.getAllEpics().size());

        Epic epic = taskManager.getAllEpics().get(0);

        assertNull(epic);

        Task secondEpic = taskManager.createNewEpic(new Epic("en", "ed"));

        assertEquals(secondEpic, taskManager.getEpic(secondEpic.getId()), "Эпики не совпадают.");
        assertEquals(1, taskManager.getAllEpics().size(), "Неверное количество эпиков.");
    }

    @Test
    void getSubtask() throws ManagerSaveException {
        assertEquals(0, taskManager.getAllSubtasks().size());

        Subtask subtask = taskManager.getAllSubtasks().get(0);

        assertNull(subtask);

        Epic epic = taskManager.createNewEpic(new Epic("en", "ed"));
        Subtask secondSubtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));

        assertEquals(secondSubtask, taskManager.getSubtask(secondSubtask.getId()), "Подзадачи не совпадают.");
        assertEquals(1, taskManager.getAllSubtasks().size(), "Неверное количество эпиков.");
    }

    @Test
    void updateTask() throws ManagerSaveException {
        assertEquals(0, taskManager.getAllTasks().size());

        taskManager.updateTask(new Task("tn", "td", "01.01.2000 12:00", 10));

        assertEquals(0, taskManager.getAllTasks().size());

        Task task = taskManager.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));
        Task secondTask = taskManager.createNewTask(new Task("tn", "td", "01.01.2000 13:00", 10));

        taskManager.updateTask(task);

        assertNotEquals(task.getId(), secondTask.getId(), "Идентификаторы задач не должны совпадать.");
        assertEquals(task.getName(), secondTask.getName(), "Названия задач не совпадают.");
        assertEquals(task.getDescription(), secondTask.getDescription(), "Описания задач не совпадают.");
        assertEquals(task.getStatus(), secondTask.getStatus(), "Статусы задач не совпадают.");
    }

    @Test
    void updateEpic() throws ManagerSaveException {
        assertEquals(0, taskManager.getAllEpics().size());

        taskManager.updateEpic(new Epic("en", "dn"));

        assertEquals(0, taskManager.getAllEpics().size());

        Epic epic = taskManager.createNewEpic(new Epic("en", "dn"));
        Epic secondEpic = taskManager.createNewEpic(new Epic("en", "dn"));
        taskManager.updateEpic(epic);

        assertNotEquals(epic.getId(), secondEpic.getId(), "Идентификаторы эпиков не должны совпадать.");
        assertEquals(epic.getName(), secondEpic.getName(), "Названия эпиков не совпадают.");
        assertEquals(epic.getDescription(), secondEpic.getDescription(), "Описания эпиков не совпадают.");
        assertEquals(epic.getStatus(), secondEpic.getStatus(), "Статусы эпиков не совпадают");
    }

    @Test
    void updateSubtask() throws ManagerSaveException {
        assertEquals(0, taskManager.getAllSubtasks().size());

        Epic epic = taskManager.createNewEpic(new Epic("en", "ed"));
        taskManager.updateSubtask(new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));

        assertEquals(0, taskManager.getAllSubtasks().size());

        Subtask subtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
        Subtask secondSubtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 13:00", 10, epic.getId()));

        taskManager.updateSubtask(subtask);

        assertEquals(epic, taskManager.getEpicOfSubtask(subtask.getId()));
        assertNotEquals(subtask.getId(), secondSubtask.getId(), "Идентификаторы подзадач не должны совпадать.");
        assertEquals(subtask.getName(), secondSubtask.getName(), "Названия подзадач не совпадают.");
        assertEquals(subtask.getDescription(), secondSubtask.getDescription(), "Описания подзадач не совпадают.");
        assertEquals(subtask.getStatus(), secondSubtask.getStatus(), "Статусы подзадач не совпадают.");

    }

    @Test
    void updateEpicStatus() throws ManagerSaveException {
        Epic epic = taskManager.createNewEpic(new Epic("en", "ed"));
        Subtask subtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
        Subtask secondSubtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 13:00", 10, epic.getId()));

        taskManager.setStatus(subtask, TaskStatus.NEW);
        taskManager.setStatus(secondSubtask, TaskStatus.NEW);

        assertEquals(TaskStatus.NEW, subtask.getStatus(), "Статус подзадачи должен быть NEW.");
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика должен быть NEW.");

        taskManager.setStatus(subtask, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, subtask.getStatus(), "Статус подзадачи должен быть IN_PROGRESS.");
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS.");

        taskManager.setStatus(subtask, TaskStatus.DONE);
        taskManager.setStatus(secondSubtask, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.DONE, subtask.getStatus(), "Статус подзадачи должен быть DONE.");
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS.");

        taskManager.setStatus(secondSubtask, TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика должен быть DONE.");
    }

    @Test
    void deleteById() throws ManagerSaveException {
        Task task = taskManager.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));
        Epic epic = taskManager.createNewEpic(new Epic("en", "ed"));
        Subtask subtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));

        assertTrue(taskManager.getAllTasks().containsValue(task));
        assertTrue(taskManager.getAllEpics().containsValue(epic));
        assertTrue(taskManager.getAllSubtasks().containsValue(subtask));

        taskManager.deleteById(task.getId());
        assertFalse(taskManager.getAllTasks().containsValue(task), "Задача не удалена.");

        taskManager.deleteById(subtask.getId());
        assertFalse(taskManager.getAllSubtasks().containsValue(subtask), "Подзадача не удалена.");

        taskManager.deleteById(epic.getId());
        assertFalse(taskManager.getAllEpics().containsValue(epic), "Эпик не удален.");
    }

    @Test
    void getSubtasksOfEpic() throws ManagerSaveException {
        Epic epic = taskManager.createNewEpic(new Epic("en", "ed"));
        Subtask subtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
        Subtask secondSubtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 13:00", 10, epic.getId()));

        ArrayList<Subtask> subtasksOfEpic = taskManager.getSubtasksOfEpic(epic);

        assertTrue(subtasksOfEpic.contains(subtask));
        assertTrue(subtasksOfEpic.contains(secondSubtask));
    }

    @Test
    void getEpicOfSubtask() throws ManagerSaveException {
        Epic epic = taskManager.createNewEpic(new Epic("en", "ed"));
        Subtask subtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));

        Epic secondEpic = taskManager.getEpicOfSubtask(subtask.getId());

        assertEquals(epic, secondEpic);
    }

    @Test
    void getHistory() throws ManagerSaveException {
        Task task = taskManager.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));
        Epic epic = taskManager.createNewEpic(new Epic("en", "ed"));
        Subtask subtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));

        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());

        List<Task> history = taskManager.getHistory();

        assertTrue(history.contains(task), "Задача не добавлена в историю.");
        assertTrue(history.contains(epic), "Эпик не добавлен в историю.");
        assertTrue(history.contains(subtask), "Подзадача не добавлена в историю.");
    }

    @Test
    void findEpic() throws ManagerSaveException {
        assertEquals(0, taskManager.getAllEpics().size());

        Epic epic = taskManager.getAllEpics().get(0);

        assertNull(epic);

        Task secondEpic = taskManager.createNewEpic(new Epic("en", "ed"));

        assertEquals(secondEpic, taskManager.getEpic(secondEpic.getId()), "Эпики не совпадают.");
        assertEquals(1, taskManager.getAllEpics().size(), "Неверное количество эпиков.");
    }

    @Test
    void setStatus() throws ManagerSaveException {
        Task task = taskManager.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));
        Epic epic = taskManager.createNewEpic(new Epic("en", "ed"));
        Subtask subtask = taskManager.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));

        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(TaskStatus.NEW, subtask.getStatus());

        taskManager.setStatus(task, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());

        taskManager.setStatus(subtask, TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, subtask.getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        taskManager.setStatus(subtask, TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, subtask.getStatus());
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }
}