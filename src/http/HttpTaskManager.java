package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;
import model.Subtask;
import model.Task;
import service.FileBackedTasksManager;
import utils.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient client;
    private final Gson gson;
    private final TaskAdapter taskAdapter;
    private final SubtaskAdapter subtaskAdapter;
    private final EpicAdapter epicAdapter;

    public HttpTaskManager() throws IOException, InterruptedException {
        client = new KVTaskClient(URI.create("http://localhost:8078"));
        taskAdapter = new TaskAdapter();
        subtaskAdapter = new SubtaskAdapter(getAllEpics());
        epicAdapter = new EpicAdapter();
        gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, taskAdapter)
                .registerTypeAdapter(Subtask.class, subtaskAdapter)
                .registerTypeAdapter(Epic.class, epicAdapter)
                .create();
    }

    public String load(String key) throws IOException, InterruptedException {
        return client.load(key);
    }

    @Override
    public void save() {
        try {
            super.save();
            client.put("tasks", gson.toJson(getAllTasks()));
            client.put("epics", gson.toJson(getAllEpics()));
            client.put("subtasks", gson.toJson(getAllSubtasks()));
            List<Task> history = getHistory();
            ArrayList<Integer> historyId = new ArrayList<>();
            for (Task task : history) {
                historyId.add(task.getId());
            }
            client.put("history", gson.toJson(historyId));
        } catch (IOException | InterruptedException | ManagerSaveException e) {
            e.printStackTrace();
        }
    }

    public KVTaskClient getClient() {
        return client;
    }

    public Gson getGson() {
        return gson;
    }

    public TaskAdapter getTaskAdapter() {
        return taskAdapter;
    }

    public SubtaskAdapter getSubtaskAdapter() {
        return subtaskAdapter;
    }

    public EpicAdapter getEpicAdapter() {
        return epicAdapter;
    }
}