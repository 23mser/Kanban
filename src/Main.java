import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManager;
import utils.Managers;

public class Main {

    public static void main(String[] args) {

        Managers managers = new Managers();

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

        Epic epic2 = new Epic("отправить на проверку", "если работа готова");
        taskManager.createNewEpic(epic2);

        Subtask epic2Subtask1 = new Subtask(6, "загрузить на GitHub", "загрузить через bash");
        taskManager.createNewSubtask(epic2, epic2Subtask1);

        Managers.getDefault().getList();
        managers.getDefaultHistory().add(taskManager.getTask(1));
        managers.getDefaultHistory().add(taskManager.getTask(2));
        managers.getDefaultHistory().add(taskManager.getEpic(3));
        System.out.println(managers.getDefaultHistory().getHistory());
        managers.getDefaultHistory().add(taskManager.getSubtask(4));
        managers.getDefaultHistory().add(taskManager.getSubtask(5));
        managers.getDefaultHistory().add(taskManager.getEpic(6));
        managers.getDefaultHistory().add(taskManager.getSubtask(7));
        managers.getDefaultHistory().add(taskManager.getTask(1));
        managers.getDefaultHistory().add(taskManager.getTask(2));
        managers.getDefaultHistory().add(taskManager.getEpic(3));
        managers.getDefaultHistory().add(taskManager.getSubtask(4));
        System.out.println(managers.getDefaultHistory().getHistory());
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
