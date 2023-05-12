package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, ArrayList<Subtask>> subtasksInEpic = new HashMap<>();
    private final ArrayList<Subtask> subtaskArrayList = new ArrayList<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public int id = 1;

    public int getId() {
        return id++;
    }

    @Override
    public void createNewTask(Task task) {
        task.setStatus(TaskStatus.NEW);
        task.setId(getId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createNewEpic(Epic epic) {
        epic.setStatus(TaskStatus.NEW);
        epic.setId(getId());
        epics.put(epic.getId(), epic);
        subtasksInEpic.put(epic.getId(), new ArrayList<>());
    }

    @Override
    public void createNewSubtask(Epic epic, Subtask subtask) {
        int epicId = epic.getId();
        subtask.setStatus(TaskStatus.NEW);
        subtask.setId(getId());
        subtasks.put(subtask.getId(), subtask);
        subtaskArrayList.add(subtask);
        subtasksInEpic.put(epicId, subtaskArrayList);
        updateEpicStatus(epic);
        epic.addSubtaskId(subtask);
    }

    @Override
    public void getList() {
        if (!tasks.isEmpty() || !epics.isEmpty()) {
            System.out.println(getAllTasks());
            System.out.println(getAllEpics());
            System.out.println(getAllSubtasks());
        } else {
            System.out.println("Задачи отсутствуют.");
        }
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        for (Integer taskId : tasks.keySet()) {
            taskList.add(tasks.get(taskId));
        }
        return taskList;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> epicList = new ArrayList<>();
        for (Integer epicId : epics.keySet()) {
            epicList.add(epics.get(epicId));
        }
        return epicList;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> subtaskList = new ArrayList<>();
        for (Integer epicId : epics.keySet()) {
            for (Subtask currentSubtaskInEpic : subtaskArrayList) {
                if (currentSubtaskInEpic.epicId == epicId) {
                    subtaskList.add(currentSubtaskInEpic);
                }
            }
        }
        return subtaskList;
    }

    @Override
    public void deleteAll() {
        for (Integer epicId : subtasksInEpic.keySet()) {
            subtasksInEpic.remove(epicId);
        }
        tasks.clear();
        subtasks.clear();
        epics.clear();
        System.out.println("Все удалено.");
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены.");
    }

    @Override
    public void deleteAllEpics() {
        subtasksInEpic.clear();
        subtasks.clear();
        epics.clear();
        System.out.println("Все эпики и их подзадачи удалены.");
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        System.out.println("Все подзадачи удалены.");
    }

    @Override
    public Task getTask(int number) {
        Task foundTask = null;
        if (tasks.containsKey(number)) {
            for (Integer id : tasks.keySet()) {
                if (number == id) {
                    foundTask = tasks.get(id);
                    System.out.println("Идентификатор " + number + " - задача: " + foundTask.getName() + ".");
                    break;
                }
            }
        } else {
            System.out.println("Задачи с таким идентификатором не найдено.");
        }
        return foundTask;
    }

    @Override
    public Epic getEpic(int number) {
        Epic foundEpic = null;
        if (epics.containsKey(number)) {
            for (Integer id : epics.keySet()) {
                if (number == id) {
                    foundEpic = epics.get(id);
                    System.out.println("Идентификатор " + number + " - эпик: " + foundEpic.getName() + ".");
                    break;
                }
            }
        } else {
            System.out.println("Эпика с таким идентификатором не найдено.");
        }
        return foundEpic;
    }

    @Override
    public Subtask getSubtask(int number) {
        Subtask foundSubtask = null;
        if (subtasks.containsKey(number)) {
            for (Integer id : subtasks.keySet()) {
                if (number == id) {
                    foundSubtask = subtasks.get(id);
                    System.out.println("Идентификатор " + number + " - подзадача: " + foundSubtask.getName() + ".");
                    break;
                }
            }
        } else {
            System.out.println("Подзадачи с таким идентификатором не найдено.");
        }
        return foundSubtask;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println("Задача " + task.getName() + " обновлена.");
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        System.out.println("Эпик " + epic.getName() + " обновлен.");
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        System.out.println("Подзадача " + subtask.getName() + " обновлена.");
        Epic currentEpic = getEpicOfSubtask(subtask);
        updateEpicStatus(currentEpic);
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        TaskStatus epicStatus = TaskStatus.NEW;
        for (Integer subtaskId : subtasks.keySet()) {
            Subtask currentSubtask = subtasks.get(subtaskId);
            if (currentSubtask.getStatus().equals(TaskStatus.DONE)) {
                epicStatus = currentSubtask.getStatus();
            } else {
                epicStatus = TaskStatus.IN_PROGRESS;
                break;
            }
        }
        if (subtasks.isEmpty() || epicStatus == TaskStatus.NEW) {
            epic.setStatus(TaskStatus.NEW);
            System.out.println("Статус эпика " + epic.getName() + " - " + epic.getStatus());
        } else if (epicStatus.equals(TaskStatus.DONE)) {
            epic.setStatus(TaskStatus.DONE);
            System.out.println("Статус эпика " + epic.getName() + " - " + epic.getStatus());
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
            System.out.println("Статус эпика " + epic.getName() + " - " + epic.getStatus());
        }
    }

    @Override
    public void deleteById(int idInput) {
        if (tasks.containsKey(idInput) || epics.containsKey(idInput) || subtasks.containsKey(idInput)) {
            for (Integer id : tasks.keySet()) {
                if (idInput == id) {
                    Task foundTask = tasks.get(id);
                    System.out.println("Задача " + foundTask.getName() + " удалена.");
                    tasks.remove(id);
                    break;
                }
            }
            for (Integer id : epics.keySet()) {
                if (idInput == id) {
                    Epic foundEpic = epics.get(id);
                    System.out.println("Задача " + foundEpic.getName() + " удалена.");
                    epics.remove(id);
                    subtasksInEpic.remove(id);
                    break;
                }
            }
            for (Integer id : subtasks.keySet()) {
                if (idInput == id) {
                    Subtask foundSubtask = subtasks.get(id);
                    System.out.println("Задача " + foundSubtask.getName() + " удалена.");
                    subtasks.remove(id);
                    Epic currentEpic = getEpicOfSubtask(foundSubtask);
                    updateEpicStatus(currentEpic);
                    subtaskArrayList.removeIf(subtask -> subtask.getId() == idInput);
                    break;
                }
            }
        } else {
            System.out.println("Такого идентификатора не существует.");
        }
    }

    @Override
    public void getSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> currentSubtasksOfEpic = subtasksInEpic.get(epic.getId());
        if (!(currentSubtasksOfEpic == null)) {
            System.out.println("Эпик " + epic.getName() + " включает следующие подзадачи: ");
            for (Subtask currentSubtaskInEpic : subtaskArrayList) {
                if (currentSubtaskInEpic.epicId == epic.id) {
                    System.out.println(currentSubtaskInEpic.getName());
                }
            }
        } else {
            System.out.println("У данного эпика нет подзадач.");
        }
    }

    @Override
    public Epic getEpicOfSubtask(Subtask subtask) {
        Epic foundEpic = null;
        for (Integer epicId : subtasksInEpic.keySet()) {
            Epic currentEpic = epics.get(epicId);
            for (Subtask currentSubtask : subtaskArrayList) {
                if (currentSubtask.equals(subtask)) {
                    foundEpic = currentEpic;
                    break;
                }
            }
        }
        return foundEpic;
    }

}
