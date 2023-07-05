package service;

import model.Epic;
import model.Subtask;
import model.Task;
import utils.ManagerSaveException;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    void createNewTask(Task task) throws ManagerSaveException;

    Epic createNewEpic(Epic epic) throws ManagerSaveException;

    void createNewSubtask(Epic epic, Subtask subtask) throws ManagerSaveException;

    void getList();

    HashMap<Integer, Task> getAllTasks();

    HashMap<Integer, Epic> getAllEpics();

    HashMap<Integer, Subtask> getAllSubtasks();

    void deleteAll() throws ManagerSaveException;

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTask(int number) throws ManagerSaveException;

    Epic getEpic(int number) throws ManagerSaveException;

    Subtask getSubtask(int number) throws ManagerSaveException;

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void updateEpicStatus(Epic epic);

    void deleteById(int idInput);

    void getSubtasksOfEpic(Epic epic);

    Epic getEpicOfSubtask(int subtaskId);

    List<Task> getHistory();

}
