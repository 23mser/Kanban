import model.Epic;
import model.Subtask;
import model.Task;
import service.HistoryManager;
import service.TaskManager;
import utils.Managers;

public class Main {

    public static void main(String[] args) {

        Managers managers = new Managers();

        TaskManager taskManager = Managers.getDefault();

        HistoryManager historyManager = managers.getDefaultHistory();

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

        historyManager.add(taskManager.getTask(1));
        historyManager.add(taskManager.getTask(2));
        historyManager.add(taskManager.getEpic(3));
        System.out.println(historyManager.getHistory());
        historyManager.add(taskManager.getSubtask(4));
        historyManager.add(taskManager.getSubtask(5));
        historyManager.add(taskManager.getSubtask(6));
        historyManager.add(taskManager.getEpic(7));
        historyManager.add(taskManager.getTask(1));
        historyManager.add(taskManager.getTask(2));
        historyManager.add(taskManager.getEpic(3));
        System.out.println(historyManager.getHistory());
        taskManager.getList();
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
