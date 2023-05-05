public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        Task task1 = new Task("отдохнуть", "набраться сил перед программированием");
        manager.createNewTask(task1);

        Task task2 = new Task("покормить кота", "насыпать корм в миску");
        manager.createNewTask(task2);

        Epic epic1 = new Epic("сделать ТЗ № 3", "реализовать менеджер задач");
        manager.createNewEpic(epic1);

        Subtask epic1Subtask1 = new Subtask(epic1,"написать код", "реализовать логику работы программы");
        manager.createNewSubtask(epic1, epic1Subtask1);

        Subtask epic1Subtask2 = new Subtask(epic1,"проверить ошибки", "добавить задачи и исправить оишбки");
        manager.createNewSubtask(epic1, epic1Subtask2);

        Epic epic2 = new Epic("Отправить на проверку", "если работа готова");
        manager.createNewEpic(epic2);

        Subtask epic2Subtask1 = new Subtask(epic2,"Загрузить на GitHub", "загрузить через bash");
        manager.createNewSubtask(epic2, epic2Subtask1);

        manager.printArrayOfTasks();
        manager.findTask(3);
        manager.updateTask(task2, "IN_PROGRESS");
        manager.updateSubtask(epic1Subtask1, "DONE");
        manager.updateSubtask(epic2Subtask1, "IN_PROGRESS");
        manager.getSubtasksOfEpic(epic1);
        manager.deleteById(4);
        manager.printArrayOfTasks();
        manager.deleteAllTasks();
        manager.printArrayOfTasks();
    }
}
