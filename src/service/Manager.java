package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Manager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, ArrayList<Subtask>> subtasksInEpic = new HashMap<>();
    private final ArrayList<Subtask> subtaskArrayList = new ArrayList<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public int id = 1;

    public int getId() {
        return id++;
    }

    public void createNewTask(Task task) {
        task.setStatus("NEW");
        task.setId(getId());
        tasks.put(task.getId(), task);
    }

    public void createNewEpic(Epic epic) {
        epic.setStatus("NEW");
        epic.setId(getId());
        epics.put(epic.getId(), epic);
        subtasksInEpic.put(epic.getId(), new ArrayList<>());
    }

    public void createNewSubtask(Epic epic, Subtask subtask) {
        int epicId = epic.getId();
        subtask.setStatus("NEW");
        subtask.setId(getId());
        subtasks.put(subtask.getId(), subtask);
        subtaskArrayList.add(subtask);
        subtasksInEpic.put(epicId, subtaskArrayList);
        updateEpicStatus(epic);
        epic.addSubtaskId(subtask);
    }

    public void getList() {
        if (!tasks.isEmpty() || !epics.isEmpty()) {
            System.out.println(getAllTasks());
            System.out.println(getAllEpics());
            System.out.println(getAllSubtasks());
        } else {
            System.out.println("Задачи отсутствуют.");
        }
    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        for (Integer taskId : tasks.keySet()) {
            taskList.add(tasks.get(taskId));
        }
        return taskList;
    }

    public List<Epic> getAllEpics() {
        List<Epic> epicList = new ArrayList<>();
        for (Integer epicId : epics.keySet()) {
            epicList.add(epics.get(epicId));
        }
        return epicList;
    }

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

    public void deleteAll() {
        for (Integer epicId : subtasksInEpic.keySet()) {
            subtasksInEpic.remove(epicId);
        }
        tasks.clear();
        subtasks.clear();
        epics.clear();
        System.out.println("Все удалено.");
    }

    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены.");
    }

    public void deleteAllEpics() {
        subtasksInEpic.clear();
        subtasks.clear();
        epics.clear();
        System.out.println("Все эпики и их подзадачи удалены.");
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        System.out.println("Все подзадачи удалены.");
    }

    public void getTaskById(int number) {
        if (tasks.containsKey(number)) {
            for (Integer id : tasks.keySet()) {
                if (number == id) {
                    Task foundTask = tasks.get(id);
                    System.out.println("Идентификатор " + number + " - задача: " + foundTask.getName() + ".");
                    break;
                }
            }
        } else {
            System.out.println("Задачи с таким идентификатором не найдено.");
        }
    }

    public void getEpicById(int number) {
        if (epics.containsKey(number)) {
            for (Integer id : epics.keySet()) {
                if (number == id) {
                    Epic foundEpic = epics.get(id);
                    System.out.println("Идентификатор " + number + " - эпик: " + foundEpic.getName() + ".");
                    break;
                }
            }
        } else {
            System.out.println("Эпика с таким идентификатором не найдено.");
        }
    }

    public void getSubtaskById(int number) {
        if (subtasks.containsKey(number)) {
            for (Integer id : subtasks.keySet()) {
                if (number == id) {
                    Subtask foundSubtask = subtasks.get(id);
                    System.out.println("Идентификатор " + number + " - подзадача: " + foundSubtask.getName() + ".");
                    break;
                }
            }
        } else {
            System.out.println("Подзадачи с таким идентификатором не найдено.");
        }
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println("Задача " + task.getName() + " обновлена.");
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        System.out.println("Эпик " + epic.getName() + " обновлен.");
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        System.out.println("Подзадача " + subtask.getName() + " обновлена.");
        Epic currentEpic = getEpicOfSubtask(subtask);
        updateEpicStatus(currentEpic);
    }

    public void updateEpicStatus(Epic epic) {
        String epicStatus = "NEW";
        for (Integer subtaskId : subtasks.keySet()) {
            Subtask currentSubtask = subtasks.get(subtaskId);
            if (currentSubtask.getStatus().equals("DONE")) {
                epicStatus = currentSubtask.getStatus();
            } else {
                epicStatus = "IN_PROGRESS";
                break;
            }
        }
        if (subtasks.isEmpty() || epicStatus.equals("NEW")) {
            epic.setStatus("NEW");
            System.out.println("Статус эпика " + epic.getName() + " - " + epic.getStatus());
        } else if (epicStatus.equals("DONE")) {
            epic.setStatus("DONE");
            System.out.println("Статус эпика " + epic.getName() + " - " + epic.getStatus());
        } else {
            epic.setStatus("IN_PROGRESS");
            System.out.println("Статус эпика " + epic.getName() + " - " + epic.getStatus());
        }
    }

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