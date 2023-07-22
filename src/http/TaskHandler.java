package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import utils.ManagerSaveException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TaskHandler implements HttpHandler {
    private final HttpTaskManager httpTaskManager;
    private final TaskAdapter taskAdapter;

    public TaskHandler(HttpTaskManager httpTaskManager, TaskAdapter taskAdapter) {
        this.httpTaskManager = httpTaskManager;
        this.taskAdapter = taskAdapter;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = null;
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, taskAdapter)
                .create();
        String method = httpExchange.getRequestMethod();
        String pathGet = httpExchange.getRequestURI().toString();
        switch(method) {
            case "GET":
                if (pathGet.contains("id=")) {
                    int id = Integer.parseInt(pathGet.split("=")[1]);
                    if (!httpTaskManager.getAllTasks().containsKey(id)) {
                        response = "Задачи " + id + " нет.";
                        break;
                    }
                    try {
                        response = gson.toJson(httpTaskManager.getTask(id));
                    } catch (ManagerSaveException e) {
                        e.printStackTrace();
                    }
                    break;
                } else if (pathGet.split("/")[2].contains("task")) {
                    if (httpTaskManager.getAllTasks().isEmpty()) {
                        response = "Задач нет.";
                        break;
                    }
                    response = gson.toJson(httpTaskManager.getAllTasks().values());
                    break;
                }
            case "POST":
                final String json = new String(httpExchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8);
                final Task task = gson.fromJson(json, Task.class);
                if (pathGet.contains("id=")) {
                    int id = Integer.parseInt(pathGet.split("=")[1]);
                    httpTaskManager.updateTask(task);
                    if (httpTaskManager.getAllTasks().get(id).getName().equals(task.getName())
                            && httpTaskManager.getAllTasks().get(id).getDescription().equals(task.getDescription())
                            && httpTaskManager.getAllTasks().get(id).getStartTime().equals(task.getStartTime())
                            && httpTaskManager.getAllTasks().get(id).getDuration().equals(task.getDuration())) {
                        response = "Задача успешно обновлена.";
                        break;
                    }
                    response = "Не удалось обновить задачу. Возможно время выполнения задачи пересекается с другими задачами.";
                    break;
                } else if (pathGet.split("/")[2].contains("task")) {
                    try {
                        if (task.getStartTime().isEqual(Task.DEFAULT_START)) {
                            httpTaskManager.createNewTask(new Task(task.getName(),
                                    task.getDescription()));
                        } else {
                            httpTaskManager.createNewTask(new Task(task.getName(),
                                    task.getDescription(),
                                    task.getStartTime().toString(),
                                    (int) task.getDuration().toMinutes()));
                        }
                        httpTaskManager.setStatus(task, task.getStatus());
                    } catch (ManagerSaveException e) {
                        e.printStackTrace();
                    }
                    response = "Задача добавлена.";
                    break;
                }
            case "DELETE":
                if (pathGet.contains("id=")) {
                    int id = Integer.parseInt(pathGet.split("=")[1]);
                    if (!httpTaskManager.getAllTasks().containsKey(id)) {
                        response = "Задачи с id" + id + " нет.";
                        break;
                    }
                    httpTaskManager.deleteById(id);
                    if (httpTaskManager.getAllTasks().get(id) == null) {
                        response = "Задача " + id + " успешно удалена.";
                    }
                    break;
                } else if (pathGet.split("/")[2].contains("task")) {
                    httpTaskManager.deleteAllTasks();
                    if (!httpTaskManager.getAllTasks().isEmpty()) {
                        response = "Что-то пошло не так, задачи не удалены.";
                        break;
                    }
                    response = "Задачи удалены.";
                    break;
                }
            default:
                response = "Некорректный метод " + method;
                httpExchange.sendResponseHeaders(404, 0);
        }
        httpExchange.sendResponseHeaders(200, 0);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(Objects.requireNonNull(response).getBytes());
        }
    }
}