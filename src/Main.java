import model.Epic;
import model.Subtask;
import model.Task;
import utils.Managers;

public class Main {

    public static void main(String[] args) {

        Managers managers = new Managers();

        Task task1 = new Task("отдохнуть", "набраться сил перед программированием");
        managers.getDefault().createNewTask(task1);

        Task task2 = new Task("покормить кота", "насыпать корм в миску");
        managers.getDefault().createNewTask(task2);

        Epic epic1 = new Epic("сделать ТЗ № 3", "реализовать менеджер задач");
        managers.getDefault().createNewEpic(epic1);

        Subtask epic1Subtask1 = new Subtask(3, "написать код", "реализовать логику работы программы");
        managers.getDefault().createNewSubtask(epic1, epic1Subtask1);

        Subtask epic1Subtask2 = new Subtask(3, "проверить ошибки", "добавить задачи и исправить оишбки");
        managers.getDefault().createNewSubtask(epic1, epic1Subtask2);

        Epic epic2 = new Epic("отправить на проверку", "если работа готова");
        managers.getDefault().createNewEpic(epic2);

        Subtask epic2Subtask1 = new Subtask(6, "загрузить на GitHub", "загрузить через bash");
        managers.getDefault().createNewSubtask(epic2, epic2Subtask1);

        managers.getDefault().getList();
        managers.getDefaultHistory().add(managers.getDefault().getTask(1));
        managers.getDefaultHistory().add(managers.getDefault().getTask(2));
        managers.getDefaultHistory().add(managers.getDefault().getEpic(3));
        managers.getDefaultHistory().getHistory();
        managers.getDefaultHistory().add(managers.getDefault().getSubtask(4));
        managers.getDefaultHistory().add(managers.getDefault().getSubtask(5));
        managers.getDefaultHistory().add(managers.getDefault().getEpic(6));
        managers.getDefaultHistory().add(managers.getDefault().getSubtask(7));
        managers.getDefaultHistory().add(managers.getDefault().getTask(1));
        managers.getDefaultHistory().add(managers.getDefault().getTask(2));
        managers.getDefaultHistory().add(managers.getDefault().getEpic(3));
        managers.getDefaultHistory().add(managers.getDefault().getSubtask(4));
        managers.getDefaultHistory().getHistory();
        managers.getDefault().updateTask(task1);
        managers.getDefault().updateEpic(epic2);
        managers.getDefault().updateSubtask(epic1Subtask2);
        managers.getDefault().getSubtasksOfEpic(epic1);
        managers.getDefault().deleteById(4);
        managers.getDefault().deleteAllTasks();
        managers.getDefault().deleteAllEpics();
        managers.getDefault().deleteAllSubtasks();
        managers.getDefault().deleteAll();
    }
}
