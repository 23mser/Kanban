package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.io.OutputStream;

public class HistoryHandler implements HttpHandler {
    private final HttpTaskManager httpTaskManager;
    private final TaskAdapter taskAdapter;
    private final SubtaskAdapter subtaskAdapter;
    private final EpicAdapter epicAdapter;

    public HistoryHandler(HttpTaskManager httpTaskManager, TaskAdapter taskAdapter, SubtaskAdapter subtaskAdapter,
                          EpicAdapter epicAdapter) {
        this.httpTaskManager = httpTaskManager;
        this.taskAdapter = taskAdapter;
        this.subtaskAdapter = subtaskAdapter;
        this.epicAdapter = epicAdapter;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response;
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, taskAdapter)
                .registerTypeAdapter(Subtask.class, subtaskAdapter)
                .registerTypeAdapter(Epic.class, epicAdapter)
                .create();
        String method = httpExchange.getRequestMethod();
        String pathGet = httpExchange.getRequestURI().toString();
        if (method.equals("GET") && pathGet.split("/")[2].equals("history")) {
            response = gson.toJson(httpTaskManager.getHistory());
            httpExchange.sendResponseHeaders(200, 0);
        } else {
            response = "/history ждёт GET-запрос, а получил  " + method;
            httpExchange.sendResponseHeaders(404, 0);
        }
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}