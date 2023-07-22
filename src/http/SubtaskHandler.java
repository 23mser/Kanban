package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Subtask;
import utils.ManagerSaveException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class SubtaskHandler implements HttpHandler {
    private final HttpTaskManager httpTaskManager;
    private final SubtaskAdapter subtaskAdapter;

    public SubtaskHandler(HttpTaskManager httpTaskManager, SubtaskAdapter subtaskAdapter) {
        this.httpTaskManager = httpTaskManager;
        this.subtaskAdapter = subtaskAdapter;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = null;
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Subtask.class, subtaskAdapter)
                .create();
        String method = httpExchange.getRequestMethod();
        String pathGet = httpExchange.getRequestURI().toString();
        switch(method) {
            case "GET":
                if (pathGet.split("/")[2].equals("subtask") && pathGet.split("/").length == 3) {
                    if (httpTaskManager.getAllSubtasks().isEmpty()) {
                        response = "Подзадач нет.";
                        break;
                    }
                    response = gson.toJson(httpTaskManager.getAllSubtasks());
                    break;
                } else if (pathGet.split("/")[2].equals("subtask")
                        && pathGet.split("/")[3].equals("epic")
                        && pathGet.split("/")[4].contains("?id=")) {
                    int id = Integer.parseInt(pathGet.split("=")[1]);
                    if (httpTaskManager.getAllSubtasks().isEmpty()) {
                        response = "Подзадач нет.";
                        break;
                    }
                    response = gson.toJson(httpTaskManager.getAllEpics().get(id).getSubtasks());
                    break;
                } else if (pathGet.split("/")[3].contains("id=")) {
                    int id = Integer.parseInt(pathGet.split("=")[1]);
                    if (!httpTaskManager.getAllSubtasks().containsKey(id)) {
                        response = "Подзадачи с таким идентификатором не найдено.";
                        break;
                    }
                    try {
                        response = gson.toJson(httpTaskManager.getSubtask(id));
                    } catch (ManagerSaveException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            case "POST":
                final String json = new String(httpExchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8);
                final Subtask subtask = gson.fromJson(json, Subtask.class);
                if (pathGet.contains("id=")) {
                    int id = Integer.parseInt(pathGet.split("=")[1]);
                    httpTaskManager.updateSubtask(subtask);
                    if (httpTaskManager.getAllSubtasks().get(id).getName().equals(subtask.getName())
                            && httpTaskManager.getAllSubtasks().get(id).getDescription().equals(subtask.getDescription())
                            && httpTaskManager.getAllSubtasks().get(id).getStartTime().equals(subtask.getStartTime())
                            && httpTaskManager.getAllSubtasks().get(id).getDuration().equals(subtask.getDuration())) {
                        response = "Подзадача успешно обновлена.";
                        break;
                    }
                    response = "Не удалось обновить задачу. Возможно время выполнения задачи пересекается с другими задачами.";
                    break;
                } else if (pathGet.split("/")[2].equals("subtask")) {
                    try {
                        Epic epic = httpTaskManager.getEpic(subtask.getEpicId());
                        if (epic == null) {
                            response = "Эпик не найден.";
                            break;
                        }
                        else {
                            httpTaskManager.createNewSubtask(epic,
                                    new Subtask(subtask.getName(),
                                            subtask.getDescription(),
                                            subtask.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                                            (int) subtask.getDuration().toMinutes(),
                                            subtask.getEpicId()));
                            httpTaskManager.setStatus(subtask, subtask.getStatus());
                        }

                    } catch (ManagerSaveException e) {
                        e.printStackTrace();
                    }
                    response = "Подзадача добавлена.";
                    break;
                }
            case "DELETE":
                if (pathGet.contains("id=")) {
                    int id = Integer.parseInt(pathGet.split("=")[1]);
                    if (!httpTaskManager.getAllSubtasks().containsKey(id)) {
                        response = "Подзадачи с id " + id + " нет.";
                        break;
                    }
                    httpTaskManager.deleteById(id);
                    if (httpTaskManager.getAllSubtasks().get(id) == null) {
                        response = "Подзадача " + id + " успешно удалена.";
                    }
                    break;
                } else if (pathGet.split("/")[2].equals("subtask")) {
                    httpTaskManager.deleteAllSubtasks();
                    if (!httpTaskManager.getAllSubtasks().isEmpty()) {
                        response = "Что-то пошло не так, подзадачи не удалены.";
                        break;
                    }
                    response = "Подзадачи удалены.";
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