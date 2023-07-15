import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManager;
import utils.ManagerSaveException;
import utils.Managers;

import java.util.Set;

public class Main {

    public static void main(String[] args) throws ManagerSaveException {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = taskManager.createNewTask(new Task("отдохнуть", "набраться сил перед программированием",
                "21.01.2000 12:00", 10));

        taskManager.createNewTask(new Task("покормить кота", "насыпать корм в миску",
                "22.01.2000 12:00", 5));

        Epic epic1 = taskManager.createNewEpic(new Epic("сделать ТЗ № 3", "реализовать менеджер задач"));

        taskManager.createNewSubtask(epic1, new Subtask("написать код", "реализовать логику работы программы",
                "02.01.2000 12:00", 10, epic1.getId()));

        Subtask epic1Subtask2 = taskManager.createNewSubtask(epic1, new Subtask("проверить ошибки",
                "добавить задачи и исправить оишбки", "18.01.2000 12:00", 10, epic1.getId()));

        taskManager.createNewSubtask(epic1, new Subtask("загрузить на GitHub",
                "загрузить через bash", "18.01.2000 12:00", 10, epic1.getId()));

        Epic epic2 = taskManager.createNewEpic(new Epic("отправить на проверку", "если работа готова"));

        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        for (Task prioritizedTask : prioritizedTasks) {
            System.out.println(prioritizedTask.getName());
        }

        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getHistory();
        taskManager.getSubtask(4);
        taskManager.getSubtask(5);
        taskManager.getSubtask(6);
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(3);
        taskManager.getHistory();
        taskManager.updateTask(task1);
        taskManager.updateEpic(epic2);
        taskManager.updateSubtask(epic1Subtask2);
        taskManager.getSubtasksOfEpic(epic1);
        taskManager.deleteById(4);
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAll();
    }
}
