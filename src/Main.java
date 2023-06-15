import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManager;
import utils.Managers;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("отдохнуть", "набраться сил перед программированием");
        taskManager.createNewTask(task1);

        Task task2 = new Task("покормить кота", "насыпать корм в миску");
        taskManager.createNewTask(task2);

        Epic epic1 = new Epic("сделать ТЗ № 3", "реализовать менеджер задач");
        taskManager.createNewEpic(epic1);

        Subtask epic1Subtask1 = new Subtask(3, "написать код", "реализовать логику работы программы");
        taskManager.createNewSubtask(epic1, epic1Subtask1);

        Subtask epic1Subtask2 = new Subtask(3, "проверить ошибки", "добавить задачи и исправить оишбки");
        taskManager.createNewSubtask(epic1, epic1Subtask2);

        Subtask epic1Subtask3 = new Subtask(3, "загрузить на GitHub", "загрузить через bash");
        taskManager.createNewSubtask(epic1, epic1Subtask3);

        Epic epic2 = new Epic("отправить на проверку", "если работа готова");
        taskManager.createNewEpic(epic2);

        taskManager.getList();
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
        taskManager.deleteById(1);
        taskManager.getHistory();
        taskManager.deleteById(3);
        taskManager.getHistory();
        System.out.println(taskManager.getTask(2));
        System.out.println(taskManager.getEpic(7));
        System.out.println(taskManager.getSubtask(5));
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
