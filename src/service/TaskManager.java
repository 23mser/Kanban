package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    void createNewTask(Task task);

    void createNewEpic(Epic epic);

    void createNewSubtask(Epic epic, Subtask subtask);

    void getList();

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void deleteAll();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTask(int number);

    Epic getEpic(int number);

    Subtask getSubtask(int number);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void updateEpicStatus(Epic epic);

    void deleteById(int idInput);

    void getSubtasksOfEpic(Epic epic);

    Epic getEpicOfSubtask(Subtask subtask);

    List<Task> getHistory();

}
