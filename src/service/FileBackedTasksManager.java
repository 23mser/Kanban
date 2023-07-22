package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskType;
import utils.ManagerSaveException;
import utils.Managers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private File saveFile;

    public FileBackedTasksManager(File saveFile) {
        this.saveFile = saveFile;
    }

    public FileBackedTasksManager() {
    }

    public static void main(String[] args) throws ManagerSaveException, IOException {
        FileBackedTasksManager fileBacked = Managers.getFile(new File("FileBackedTasksManager.csv"));
        fileBacked.createNewTask(new Task("Task1", "D Task1", "01.01.2000 12:00", 10));
        fileBacked.createNewTask(new Task("Task2", "D Task2", "02.01.2000 12:00", 15));
        fileBacked.createNewTask(new Task("Task3", "D Task3", "03.01.2000 12:00", 20));
        Epic epic1 = fileBacked.createNewEpic(new Epic("Epic1", "D Epic1"));
        fileBacked.createNewEpic(new Epic("Epic2", "D Epic2"));
        fileBacked.createNewSubtask(epic1, new Subtask(
                "Subtask1", "D Subtask1", "06.01.2000 12:00", 35, epic1.getId()));
        fileBacked.createNewSubtask(epic1, new Subtask(
                "Subtask2", "D Subtask2", "07.01.2000 12:00", 40, epic1.getId()));
        fileBacked.createNewSubtask(epic1, new Subtask(
                "Subtask3", "D Subtask3", "08.01.2000 12:00", 45, epic1.getId()));
        fileBacked.getTask(1);
        fileBacked.getEpic(5);
        fileBacked.getSubtask(7);

        FileBackedTasksManager fileBacked1 = loadFromFile(new File("FileBackedTasksManager.csv"));
        fileBacked1.createNewTask(new Task("Task4", "Description Task4", "09.01.2000 12:00", 50));
        Epic epic2 = fileBacked1.createNewEpic(new Epic("Epic3", "Description Epic3"));
        fileBacked1.createNewSubtask(epic2, new Subtask(
                "Subtask4", "Description Subtask4", "11.01.2000 12:00", 60, epic2.getId()));
        fileBacked1.getEpic(epic2.getId());
    }

    public static FileBackedTasksManager loadFromFile(File file) throws IOException, ManagerSaveException {
        FileBackedTasksManager fileBacked = new FileBackedTasksManager(file);
        List<String> list = Files.readAllLines(file.toPath());
        for (int i = 1; i < list.size() - 1; i++) {
            if (list.get(i).isBlank()) {
                break;
            }
            Task taskFromFile = fromString(list.get(i));
            if (taskFromFile.getClass().equals(Task.class)) {
                fileBacked.createNewTask(taskFromFile);
            } else if (taskFromFile.getClass().equals(Epic.class)) {
                fileBacked.createNewEpic((Epic) taskFromFile);
            } else if (taskFromFile.getClass().equals(Subtask.class)) {
                fileBacked.createNewSubtask(fileBacked.findEpic(((Subtask) taskFromFile).epicId), (Subtask) taskFromFile);
            }
        }
        List<Integer> historyId = historyFromString(list.get(list.size() - 1));
        for (int id : historyId) {
            if (fileBacked.getAllTasks().containsKey(id)) {
                fileBacked.historyManager.add(fileBacked.getAllTasks().get(id));
            }
            if (fileBacked.getAllEpics().containsKey(id)) {
                fileBacked.historyManager.add(fileBacked.getAllEpics().get(id));
            }
            if (fileBacked.getAllSubtasks().containsKey(id)) {
                fileBacked.historyManager.add(fileBacked.getAllSubtasks().get(id));
            }
        }
        return fileBacked;
    }

    private static Task fromString(String value) {
        if (value != null) {
            String[] data = value.split(",");
            switch (data[1]) {
                case "TASK":
                    String startTime = String.format(data[5], DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    Duration duration = Duration.ofMinutes(Long.parseLong(data[6]));
                    return new Task(data[2], data[4], startTime, (int) duration.toMinutes());
                case "SUBTASK":
                    startTime = String.format(data[5], DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    duration = Duration.ofMinutes(Long.parseLong(data[6]));
                    return new Subtask(data[2], data[4], startTime, (int) duration.toMinutes(), Integer.parseInt(data[8]));
                case "EPIC":
                    return new Epic(data[2], data[4]);
            }
        }
        return (Task) Collections.emptyList();
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder builder = new StringBuilder();
        for (Task task : manager.getHistory()) {
            int taskId = task.getId();
            builder.append(taskId).append(",");
        }
        return String.valueOf(builder);
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> idList = new LinkedList<>();
        String[] arrayOfId = value.split(",");
        for (String id : arrayOfId) {
            if (!id.isBlank()) {
                idList.add(Integer.valueOf(id));
            }
        }
        return idList;
    }

    private String toString(Task task) {
        TaskType type;
        if (getAllTasks().containsKey(task.getId())) {
            type = TaskType.TASK;
            String startTime = task.startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            String endTime = task.getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            return String.format("%d,%S,%s,%S,%s,%s,%s,%s", task.getId(), type, task.getName(), task.getStatus(),
                    task.description, startTime, task.duration.toMinutes(), endTime);
        } else if (getAllEpics().containsKey(task.getId())) {
            type = TaskType.EPIC;
            String startTime = task.startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            String endTime = task.getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            return String.format("%d,%S,%s,%S,%s,%s,%s,%s", task.getId(), type, task.getName(), task.getStatus(),
                    task.description, startTime, task.duration.toMinutes(), endTime);
        } else if (getAllSubtasks().containsKey(task.getId())) {
            type = TaskType.SUBTASK;
            Epic epic = getEpicOfSubtask(task.getId());
            String startTime = task.startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            String endTime = task.getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            return String.format("%d,%S,%s,%S,%s,%s,%s,%s,%d", task.getId(), type, task.getName(), task.getStatus(),
                    task.description, startTime, task.duration.toMinutes(), endTime, epic.getId());
        }
        return null;
    }

    public void save() throws ManagerSaveException {
        if (saveFile != null) {
            try (FileWriter fileWriter = new FileWriter(saveFile, StandardCharsets.UTF_8)) {
                fileWriter.append("id,type,name,status,description,startTime,duration,epic" + "\n");
                for (Task task : getAllTasks().values()) {
                    fileWriter.write(toString(task) + "\n");
                }
                for (Epic epic : getAllEpics().values()) {
                    fileWriter.write(toString(epic) + "\n");
                }
                for (Subtask subtask : getAllSubtasks().values()) {
                    fileWriter.write(toString(subtask) + "\n");
                }
                fileWriter.append("\n");
                fileWriter.write(historyToString(historyManager));
            } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage());
            }
        }
    }

    @Override
    public Task createNewTask(Task task) throws ManagerSaveException {
        super.createNewTask(task);
        save();
        return task;
    }

    @Override
    public Epic createNewEpic(Epic epic) throws ManagerSaveException {
        super.createNewEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createNewSubtask(Epic epic, Subtask subtask) throws ManagerSaveException {
        super.createNewSubtask(epic, subtask);
        save();
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public Task getTask(int number) throws ManagerSaveException {
        Task task = super.getTask(number);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int number) throws ManagerSaveException {
        Epic epic = super.getEpic(number);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int number) throws ManagerSaveException {
        Subtask subtask = super.getSubtask(number);
        save();
        return subtask;
    }

    @Override
    public HashMap<Integer, Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public HashMap<Integer, Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public HashMap<Integer, Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
    }

    @Override
    public void deleteAll() throws ManagerSaveException {
        super.deleteAll();
        save();
    }

    @Override
    public Epic getEpicOfSubtask(int subtaskId) {
        return super.getEpicOfSubtask(subtaskId);
    }

    @Override
    public Epic findEpic(int number) {
        return super.findEpic(number);
    }
}