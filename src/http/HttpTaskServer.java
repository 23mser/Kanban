package http;

import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Subtask;
import model.Task;
import utils.ManagerSaveException;
import utils.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HttpTaskServer {
    public HttpTaskManager fileBacked;
    private final HttpServer httpServer;

    public static void main(String[] args) throws IOException, InterruptedException, ManagerSaveException {
        new KVServer().start();
        HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        server.start();
        server.fileBacked.createNewTask(new Task("tn", "td"));
        Epic epic = server.fileBacked.createNewEpic(new Epic("en", "ed"));
        server.fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic.getId()));
        server.fileBacked.getTask(1);
        server.fileBacked.getSubtask(3);
        server.fileBacked.getEpic(2);
        server.fileBacked.getAllTasks().clear();
        server.fileBacked.getAllSubtasks().clear();
        server.fileBacked.getAllEpics().clear();
        server.fileBacked.getHistory().clear();
        server.fileBacked.getPrioritizedTasks().clear();
        server.backup();
    }

    public HttpTaskServer(HttpTaskManager taskManager) throws IOException {
        fileBacked = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        createContext();
    }

    private void createContext() {
        httpServer.createContext("/tasks/task", new TaskHandler(fileBacked, fileBacked.getTaskAdapter()));
        httpServer.createContext("/tasks/subtask", new SubtaskHandler(fileBacked, fileBacked.getSubtaskAdapter()));
        httpServer.createContext("/tasks/epic", new EpicHandler(fileBacked, fileBacked.getEpicAdapter()));
        httpServer.createContext("/tasks/history", new HistoryHandler(fileBacked, fileBacked.getTaskAdapter(),
                fileBacked.getSubtaskAdapter(),
                fileBacked.getEpicAdapter()));
        httpServer.createContext("/tasks/", new PrioritizedHandler(fileBacked, fileBacked.getTaskAdapter(),
                fileBacked.getSubtaskAdapter(), fileBacked.getEpicAdapter()));
    }

    public void start() throws IOException, InterruptedException {
        httpServer.start();
        System.out.println("TaskServer запущен.");
        backup();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("TaskServer остановлен.");
    }

    public void backup() throws IOException, InterruptedException {
        HashMap<Integer, Task> tasks = fileBacked.getGson().fromJson(fileBacked.load("tasks"),
                new TypeToken<HashMap<Integer, Task>>() {}.getType());
        if (tasks != null) {
            for (Task task : tasks.values()) {
                fileBacked.getAllTasks().put(task.getId(), task);
                fileBacked.getPrioritizedTasks().add(task);
            }
        }
        HashMap<Integer, Epic> epics = fileBacked.getGson().fromJson(fileBacked.load("epics"),
                new TypeToken<HashMap<Integer, Epic>>() {}.getType());
        if (epics != null) {
            for (Epic epic : epics.values()) {
                fileBacked.getAllEpics().put(epic.getId(), epic);
                fileBacked.getPrioritizedTasks().add(epic);
            }
        }
        HashMap<Integer, Subtask> subtasks = fileBacked.getGson().fromJson(fileBacked.load("subtasks"),
                new TypeToken<HashMap<Integer, Subtask>>() {}.getType());
        if (subtasks != null) {
            for (Subtask subtask : subtasks.values()) {
                fileBacked.getAllSubtasks().put(subtask.getId(), subtask);
                fileBacked.getPrioritizedTasks().add(subtask);
            }
        }
        List<Integer> historyId = fileBacked.getGson().fromJson(fileBacked.load("history"),
                new TypeToken<ArrayList<Integer>>() {}.getType());
        if (historyId != null) {
            for (Integer id : historyId) {
                if (fileBacked.getAllTasks().containsKey(id)) {
                    fileBacked.getHistory().add(fileBacked.getAllTasks().get(id));
                } else if (fileBacked.getAllSubtasks().containsKey(id)) {
                    fileBacked.getHistory().add(fileBacked.getAllSubtasks().get(id));
                } else if (fileBacked.getAllEpics().containsKey(id)) {
                    fileBacked.getHistory().add(fileBacked.getAllEpics().get(id));
                }
            }
        }
    }
}