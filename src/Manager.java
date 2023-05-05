import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    public int id = 1;

    public int getId() {
        return id++;
    }

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, ArrayList<Subtask>> subtasksInEpic = new HashMap<>();
    ArrayList<Subtask> subtaskArrayList = new ArrayList<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void createNewTask(Object o) {
        ((Task) o).setStatus("NEW");
        ((Task) o).setId(getId());
        tasks.put(((Task) o).getId(), (Task) o);
    }
    public void createNewEpic(Object o) {
        ((Epic) o).setStatus("NEW");
        ((Epic) o).setId(getId());
        epics.put(((Epic) o).getId(), (Epic) o);
        subtasksInEpic.put(((Epic) o).getId(), new ArrayList<>());
    }
    public void createNewSubtask(Epic epic, Object o) {
        int epicId = epic.getId();
        ((Subtask) o).setStatus("NEW");
        ((Subtask) o).setId(getId());
        subtasks.put(((Subtask) o).getId(), (Subtask) o);
        subtaskArrayList.add((Subtask) o);
        subtasksInEpic.put(epicId, subtaskArrayList);
    }
    public void printArrayOfTasks() {
        System.out.println("Список всех задач: ");
        if (!tasks.isEmpty() || !epics.isEmpty() || !subtasks.isEmpty()) {
            System.out.println("Задачи: ");
            for (Integer taskId : tasks.keySet()) {
                Task currentTask = tasks.get(taskId);
                System.out.println(currentTask.getName());
            }
            for (Integer epicId : epics.keySet()) {
                System.out.print("Эпик: ");
                Epic currentEpic = epics.get(epicId);
                System.out.println(currentEpic.getName());
                for (Subtask currentSubtaskInEpic : subtaskArrayList) {
                    if (currentSubtaskInEpic.epic.equals(currentEpic)) {
                        System.out.println("\tПодзадача: " + currentSubtaskInEpic.getName());
                    }
                }
            }
        }
        else {
            System.out.println("Задачи отсутствуют.");
        }
    }
    public void deleteAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        subtasksInEpic.clear();
        System.out.println("Все задачи удалены.");
    }
    public void findTask(int number) {
        for (Integer id : tasks.keySet()) {
            if (number == id) {
                Task foundTask = tasks.get(id);
                System.out.println("Идентификатор " + number + " - задача: " + foundTask.getName() + ".");
                break;
            }
        }
        for (Integer id : epics.keySet()) {
            if (number == id) {
                Epic foundEpic = epics.get(id);
                System.out.println("Идентификатор " + number + " - эпик: " + foundEpic.getName() + ".");
                break;
            }
        }
        for (Integer id : subtasks.keySet()) {
            if (number == id) {
                Subtask foundSubtask = subtasks.get(id);
                System.out.println("Идентификатор " + number + " - подзадача: " + foundSubtask.getName() + ".");
                break;
            }
        }
    }
    public void updateTask(Task task, String newStatus) {
        switch (newStatus) {
            case "IN_PROGRESS":
            case "DONE":
                task.setStatus(newStatus);
                break;
            default:
                System.out.println("Такого статуса не сущетсвует.");
        }
        tasks.put(task.getId(), task);
        System.out.println("Задача " + task.getName() + " обновлена. Новый статус - " + task.getStatus());
    }
    public void updateSubtask(Subtask subtask, String newStatus) { //отсановился здесь, пишу логику наследования статуса
        switch (newStatus) {
            case "IN_PROGRESS":
            case "DONE":
                subtask.setStatus(newStatus);
                break;
            default:
                System.out.println("Такого статуса не сущетсвует.");
        }
        subtasks.put(subtask.getId(), subtask);
        System.out.println("Задача " + subtask.getName() + " обновлена. Новый статус - " + subtask.getStatus());
        int currentEpicId = getEpicOfSubtask(subtask);
        Epic currentEpic = epics.get(currentEpicId);
        String epicStatus = "NEW";
        for (Integer subtaskId : subtasks.keySet()) {
            Subtask currentSubtask = subtasks.get(subtaskId);
            if (currentSubtask.getStatus().equals("DONE")) {
                epicStatus = currentSubtask.getStatus();
            }
            else {
                epicStatus = "IN_PROGRESS";
                break;
            }
        }
        if (subtasks.isEmpty() || epicStatus.equals("NEW")) {
            currentEpic.setStatus("NEW");
            System.out.println("Статус эпика " + currentEpic.getName() + " - " + currentEpic.getStatus());
        }
        else if (epicStatus.equals("DONE")) {
            currentEpic.setStatus("DONE");
            System.out.println("Статус эпика " + currentEpic.getName() + " - " + currentEpic.getStatus());
        }
        else {
            currentEpic.setStatus("IN_PROGRESS");
            System.out.println("Статус эпика " + currentEpic.getName() + " - " + currentEpic.getStatus());
        }
    }
    public void deleteById(int idInput) {
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
                break;
            }
        }
        for (Integer id : subtasks.keySet()) {
            if (idInput == id) {
                Subtask foundSubtask = subtasks.get(id);
                System.out.println("Задача " + foundSubtask.getName() + " удалена.");
                subtasks.remove(id);
                subtaskArrayList.removeIf(subtask -> subtask.getId() == idInput);
                break;
            }
        }
    }
    public void getSubtasksOfEpic(Epic epic) {
        ArrayList<Subtask> currentSubtasksOfEpic = subtasksInEpic.get(epic.getId());
        if (!(currentSubtasksOfEpic == null)) {
            Epic currentEpic = epics.get(epic.getId());
            System.out.println("Эпик " + epic.getName() + " включает следующие подзадачи: " );
            for (Subtask currentSubtaskInEpic : subtaskArrayList) {
                if (currentSubtaskInEpic.epic.equals(currentEpic)) {
                    System.out.println(currentSubtaskInEpic.getName());
                }
            }
        }
        else {
            System.out.println("У данного эпика нет подзадач.");
        }
    }
    public int getEpicOfSubtask(Subtask subtask) {
        int foundEpic = 0;
        for (Integer epic : subtasksInEpic.keySet()) {
            for (Integer subtaskId : subtasks.keySet()) {
                Subtask currentSubtask = subtasks.get(subtaskId);
                if (currentSubtask.equals(subtask)) {
                    foundEpic = epic;
                }
            }
        }
        return foundEpic;
    }
}
