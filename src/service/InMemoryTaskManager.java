package service;

import model.*;
import utils.ManagerSaveException;
import utils.Managers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, ArrayList<Subtask>> subtasksInEpic = new HashMap<>();
    private final ArrayList<Subtask> subtaskArrayList = new ArrayList<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final Set<Task> prioritizedTasks = new TreeSet<>((t1, t2) -> {
        if (t1.getId() == t2.getId()) {
            return 0;
        } else if (t1.getStartTime().isAfter(t2.getStartTime()) || t1.getStartTime().isEqual(t2.getStartTime())) {
            return 1;
        } else {
            return -1;
        }
    });

    public int id = 1;
    Managers managers = new Managers();
    HistoryManager historyManager = managers.getDefaultHistory();

    public int getId() {
        return id++;
    }

    @Override
    public Task createNewTask(Task task) throws ManagerSaveException {
        task.setTaskType(TaskType.TASK);
        task.setStatus(TaskStatus.NEW);
        task.setId(getId());
        tasks.put(task.getId(), task);
        LocalDateTime taskStart = task.setStartTime(task.getStartTime());
        for (Task prioritizedTask : getPrioritizedTasks()) {
            if (prioritizedTask.getStartTime().isEqual(taskStart)) {
                System.out.println("Выявлено пересечение. Ранее добавленная задач удалена.");
                prioritizedTasks.remove(prioritizedTask);
                break;
            }
        }
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic createNewEpic(Epic epic) throws ManagerSaveException {
        epic.setTaskType(TaskType.EPIC);
        epic.setStatus(TaskStatus.NEW);
        epic.setId(getId());
        epics.put(epic.getId(), epic);
        subtasksInEpic.put(epic.getId(), new ArrayList<>());
        epic.getStartTime();
        epic.getDuration();
        prioritizedTasks.add(epic);
        return epic;
    }

    @Override
    public Subtask createNewSubtask(Epic epic, Subtask subtask) throws ManagerSaveException {
        subtask.setTaskType(TaskType.SUBTASK);
        subtask.setStatus(TaskStatus.NEW);
        subtask.setId(getId());
        subtasks.put(subtask.getId(), subtask);
        subtaskArrayList.add(subtask);
        subtasksInEpic.put(epic.getId(), subtaskArrayList);
        updateEpicStatus(epic);
        epic.addSubtask(subtask);
        LocalDateTime subtaskStart = subtask.setStartTime(subtask.getStartTime());
        for (Task prioritizedTask : getPrioritizedTasks()) {
            if (prioritizedTask.getStartTime().isEqual(subtaskStart)) {
                if (!epics.containsKey(prioritizedTask.getId())) {
                    System.out.println("Выявлено пересечение. Ранее добавленная задач удалена.");
                    prioritizedTasks.remove(prioritizedTask);
                    break;
                }
            }
        }
        prioritizedTasks.add(subtask);
        updateEpic(epic);
        prioritizedTasks.removeIf(task -> task.equals(epic));
        prioritizedTasks.add(epic);
        return subtask;
    }

    @Override
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    @Override
    public void deleteAll() throws ManagerSaveException {
        subtasksInEpic.clear();
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subtasksInEpic.clear();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    @Override
    public Task getTask(int number) throws ManagerSaveException {
        Task foundTask = null;
        if (tasks.containsKey(number)) {
            for (Integer id : tasks.keySet()) {
                if (number == id) {
                    foundTask = tasks.get(id);
                    historyManager.add(foundTask);
                    break;
                }
            }
        } else {
            System.out.println("Задачи с таким идентификатором не найдено.");
        }
        return foundTask;
    }

    @Override
    public Epic getEpic(int number) throws ManagerSaveException {
        Epic foundEpic = null;
        if (epics.containsKey(number)) {
            for (Integer id : epics.keySet()) {
                if (number == id) {
                    foundEpic = epics.get(id);
                    historyManager.add(foundEpic);
                    break;
                }
            }
        } else {
            System.out.println("Эпика с таким идентификатором не найдено.");
        }
        return foundEpic;
    }

    @Override
    public Subtask getSubtask(int number) throws ManagerSaveException {
        Subtask foundSubtask = null;
        if (subtasks.containsKey(number)) {
            for (Integer id : subtasks.keySet()) {
                if (number == id) {
                    foundSubtask = subtasks.get(id);
                    historyManager.add(foundSubtask);
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
        if (tasks.containsValue(task)) {
            tasks.remove(task.getId(), task);
            task.setName(task.getName());
            task.setDescription(task.getDescription());
            task.setStatus(task.getStatus());
            task.setStartTime(task.getStartTime());
            task.setDuration(task.getDuration());
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Такой задачи не найдено.");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsValue(epic)) {
            epics.remove(epic.getId(), epic);
            epic.setName(epic.getName());
            epic.setDescription(epic.getDescription());
            epic.setStatus(epic.getStatus());
            epic.setStartTime(epic.getStartTime());
            epic.setDuration(epic.getDuration());
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Такого эпика не найдено.");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsValue(subtask)) {
            subtasks.remove(subtask.getId(), subtask);
            subtask.setName(subtask.getName());
            subtask.setDescription(subtask.getDescription());
            subtask.setStatus(subtask.getStatus());
            subtask.setStartTime(subtask.getStartTime());
            subtask.setDuration(subtask.getDuration());
            subtasks.put(subtask.getId(), subtask);
            Epic currentEpic = getEpicOfSubtask(subtask.getId());
            updateEpicStatus(currentEpic);
        } else {
            System.out.println("Такой подзадачи не найдено.");
        }
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        int newCount = 0;
        int doneCount = 0;
        ArrayList<Subtask> subtaskArrayList = getSubtasksOfEpic(epic);
        if (subtaskArrayList.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        }
        else {
            for (Subtask subtask : subtaskArrayList) {
                if (subtask.getStatus().equals(TaskStatus.NEW)) {
                    newCount++;
                }
                else if (subtask.getStatus().equals(TaskStatus.DONE)) {
                    doneCount++;
                }
            }
            if (newCount == subtasks.size()) {
                epic.setStatus(TaskStatus.NEW);
            }
            else if (doneCount == subtasks.size()) {
                epic.setStatus(TaskStatus.DONE);
            }
            else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    @Override
    public void deleteById(int idInput) {
        if (tasks.containsKey(idInput) || epics.containsKey(idInput) || subtasks.containsKey(idInput)) {
            for (Integer id : tasks.keySet()) {
                if (idInput == id) {
                    tasks.remove(id);
                    historyManager.remove(id);
                    break;
                }
            }
            for (Integer id : epics.keySet()) {
                if (idInput == id) {
                    epics.remove(id);
                    subtasksInEpic.remove(id);
                    historyManager.remove(id);
                    for (Integer integer : subtasks.keySet()) {
                        historyManager.remove(integer);
                    }
                    break;
                }
            }
            for (Integer id : subtasks.keySet()) {
                if (idInput == id) {
                    Subtask foundSubtask = subtasks.get(id);
                    Epic currentEpic = getEpicOfSubtask(foundSubtask.getId());
                    subtasks.remove(id);
                    updateEpicStatus(currentEpic);
                    subtaskArrayList.removeIf(subtask -> subtask.getId() == idInput);
                    historyManager.remove(id);
                    break;
                }
            }
        } else {
            System.out.println("Такого идентификатора не существует.");
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> currentSubtasksOfEpic = subtasksInEpic.get(epic.getId());
        if (currentSubtasksOfEpic == null) {
            System.out.println("У данного эпика нет подзадач.");
        }
        return currentSubtasksOfEpic;
    }

    @Override
    public Epic getEpicOfSubtask(int subtaskId) {
        Subtask currentSubtask = subtasks.get(subtaskId);
        for (ArrayList<Subtask> list : subtasksInEpic.values()) {
            if (list.contains(currentSubtask)) {
                int epicId = currentSubtask.epicId;
                for (Epic epic : epics.values()) {
                    if (epic.equals(epics.get(epicId))) {
                        return epic;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public Epic findEpic(int number) {
        Epic foundEpic = null;
        if (epics.containsKey(number)) {
            for (Integer id : epics.keySet()) {
                if (number == id) {
                    foundEpic = epics.get(id);
                    break;
                }
            }
        }
        return foundEpic;
    }
    @Override
    public void setStatus(Task task, TaskStatus taskStatus) {
        task.setStatus(taskStatus);
        if (subtasks.containsKey(task.getId())) {
            Subtask subtask = (Subtask) task;
            Epic epic = getEpicOfSubtask(subtask.getId());
            updateEpicStatus(epic);
        }
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public void deleteSubtasksOfEpic(Epic epic) {
        List<Subtask> subtaskList = subtasksInEpic.get(epic.getId());
        for (Subtask subtask : subtaskList) {
            subtasks.remove(subtask.getId());
        }
    }
}