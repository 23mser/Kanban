package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import utils.ManagerSaveException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface TaskManager {

    Task createNewTask(Task task) throws ManagerSaveException;

    Epic createNewEpic(Epic epic) throws ManagerSaveException;

    Subtask createNewSubtask(Epic epic, Subtask subtask) throws ManagerSaveException;

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

    ArrayList<Subtask> getSubtasksOfEpic(Epic epic);

    Epic getEpicOfSubtask(int subtaskId);

    List<Task> getHistory();

    void setStatus(Task task, TaskStatus taskStatus);

    Set<Task> getPrioritizedTasks();
}
