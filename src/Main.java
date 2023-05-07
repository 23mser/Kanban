import model.Epic;
import model.Subtask;
import model.Task;
import service.Manager;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task1 = new Task("отдохнуть", "набраться сил перед программированием");
        manager.createNewTask(task1);

        Task task2 = new Task("покормить кота", "насыпать корм в миску");
        manager.createNewTask(task2);

        Epic epic1 = new Epic("сделать ТЗ № 3", "реализовать менеджер задач");
        manager.createNewEpic(epic1);

        Subtask epic1Subtask1 = new Subtask(3, "написать код", "реализовать логику работы программы");
        manager.createNewSubtask(epic1, epic1Subtask1);

        Subtask epic1Subtask2 = new Subtask(3, "проверить ошибки", "добавить задачи и исправить оишбки");
        manager.createNewSubtask(epic1, epic1Subtask2);

        Epic epic2 = new Epic("отправить на проверку", "если работа готова");
        manager.createNewEpic(epic2);

        Subtask epic2Subtask1 = new Subtask(6, "загрузить на GitHub", "загрузить через bash");
        manager.createNewSubtask(epic2, epic2Subtask1);

        manager.getList();
        manager.getAllTasks();
        manager.getAllEpics();
        manager.getAllSubtasks();
        manager.getTaskById(2);
        manager.getEpicById(6);
        manager.getSubtaskById(4);
        manager.updateTask(task1);
        manager.updateEpic(epic2);
        manager.updateSubtask(epic1Subtask2);
        manager.getSubtasksOfEpic(epic1);
        manager.deleteById(4);
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
        manager.deleteAll();
    }
}
