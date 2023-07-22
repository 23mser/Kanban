package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import utils.ManagerSaveException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class EpicHandler implements HttpHandler {
    private final HttpTaskManager httpTaskManager;
    private final EpicAdapter epicAdapter;

    public EpicHandler(HttpTaskManager httpTaskManager, EpicAdapter epicAdapter) {
        this.httpTaskManager = httpTaskManager;
        this.epicAdapter = epicAdapter;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = null;
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Epic.class, epicAdapter)
                .create();
        String method = httpExchange.getRequestMethod();
        String pathGet = httpExchange.getRequestURI().toString();
        switch (method) {
            case "GET":
                if (pathGet.contains("id=")) {
                    int id = Integer.parseInt(pathGet.split("=")[1]);
                    try {
                        if (!httpTaskManager.getAllEpics().containsKey(id)) {
                            response = "Эпика " + id + " нет.";
                            break;
                        }
                        response = gson.toJson(httpTaskManager.getEpic(id));
                    } catch (ManagerSaveException e) {
                        e.printStackTrace();
                    }
                    break;
                } else if (pathGet.split("/")[2].contains("epic")) {
                    if (httpTaskManager.getAllEpics().isEmpty()) {
                        response = "Эпиков нет.";
                        break;
                    }
                    response = gson.toJson(httpTaskManager.getAllEpics());
                    break;
                }
            case "POST":
                final String json = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                final Epic epic = gson.fromJson(json, Epic.class);
                if (pathGet.contains("id=")) {
                    int id = Integer.parseInt(pathGet.split("=")[1]);
                    httpTaskManager.updateEpic(epic);
                    if (httpTaskManager.getAllEpics().get(id).getName().equals(epic.getName())
                            && httpTaskManager.getAllEpics().get(id).getDescription().equals(epic.getDescription())) {
                        response = "Эпик успешно обновлен.";
                        break;
                    }
                    response = "Не удалось обновить эпик. Возможно время выполнения задачи пересекается с другими задачами.";
                    break;
                } else if (pathGet.split("/")[2].contains("epic")) {
                    try {
                        httpTaskManager.createNewEpic(new Epic(epic.getName(), epic.getDescription()));
                    } catch (ManagerSaveException e) {
                        e.printStackTrace();
                    }
                    response = "Эпик добавлен.";
                    break;
                }
            case "DELETE":
                if (pathGet.contains("id=")) {
                    int id = Integer.parseInt(pathGet.split("=")[1]);
                    if (!httpTaskManager.getAllEpics().containsKey(id)) {
                        response = "Эпика с идентификаторм " + id + " не найдено.";
                        break;
                    }
                    try {
                        httpTaskManager.deleteSubtasksOfEpic(httpTaskManager.getEpic(id));
                    } catch (ManagerSaveException e) {
                        throw new RuntimeException(e);
                    }
                    httpTaskManager.deleteById(id);

                    if (httpTaskManager.getAllEpics().get(id) == null) {
                        response = "Эпик успешно удалён.";
                    }
                    break;
                } else if (pathGet.split("/")[2].equals("epic")) {
                    httpTaskManager.deleteAllEpics();
                    if (httpTaskManager.getAllEpics().size() > 0) {
                        response = "Что-то пошло не так, эпики не удалены.";
                        break;
                    }
                    response = "Эпики удалены.";
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