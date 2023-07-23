package test;

import com.google.gson.Gson;
import http.HttpTaskManager;
import http.HttpTaskServer;
import http.KVServer;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.ManagerSaveException;
import utils.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class HttpTaskServerTest extends TaskManagerTest<HttpTaskManager> {
    Gson gson;
    KVServer kvServer;
    HttpTaskServer server;
    HttpTaskManager fileBacked;

    @BeforeEach
    public void startServer() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = Managers.getDefault();
        server = new HttpTaskServer(taskManager);
        server.start();
        fileBacked = server.getFileBacked();
        gson = fileBacked.getGson();
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
        server.stop();
    }

    @Test
    void shouldReturnTasksWhenGetRequest() throws ManagerSaveException, IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        fileBacked.createNewTask(new Task("tn", "td"));
        fileBacked.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));
        String json = gson.toJson(fileBacked.getAllTasks().values());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals(json, response.body(), "HashMap tasks в виде Json не совпадает с Json ответом сервера.");
    }

    @Test
    void shouldNotReturnTasksWhenGetRequestAndTasksIsEmpty() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        assertEquals(0, fileBacked.getAllTasks().size(), "Размер HashMap должен быть равен 0.");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Задач нет.", response.body(),
                "HashMap tasks в виде Json не совпадает с Json ответом сервера.");
    }

    @Test
    void shouldReturnTaskByIdWhenGetRequest() throws ManagerSaveException, IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=2");
        fileBacked.createNewTask(new Task("tn", "td"));
        fileBacked.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));
        String json = gson.toJson(fileBacked.getTask(2));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals(json, response.body(), "Задача в виде Json не совпадает с Json ответом сервера.");
        System.out.println(json);
    }

    @Test
    void shouldNotReturnTaskByIdWhenGetRequestAndTaskIsMissing() throws ManagerSaveException, IOException,
            InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=3");
        fileBacked.createNewTask(new Task("tn", "td"));
        fileBacked.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Задачи 3 нет.", response.body(),"Задачи 3 не должно быть.");
    }

    @Test
    void shouldAddNewTaskWhenPostRequest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task("tn", "td");
        task.setStatus(TaskStatus.NEW);
        task.setId(1);
        assertEquals(0, fileBacked.getAllTasks().size(), "Размер HashMap должен быть равен 0.");
        String json1 = gson.toJson(task);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json1))
                .build();
        HttpResponse<String> response1 = fileBacked.getClient().getClient().send(request1,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Задача добавлена.", response1.body(),
                "Задача должна быть добавлена.");
        assertEquals(1, fileBacked.getAllTasks().size(), "Размер HashMap должен быть равен 1.");
    }

    @Test
    void shouldUpdateTaskWhenPostRequest() throws IOException, InterruptedException, ManagerSaveException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        assertEquals(0, fileBacked.getAllTasks().size(), "Размер HashMap должен быть равен 0.");
        fileBacked.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));
        Task task = new Task("tn", "td", "01.01.2000 12:00", 10);
        task.setStatus(TaskStatus.DONE);
        assertEquals(1, fileBacked.getAllTasks().size(), "Размер HashMap должен быть равен 2.");
        String json = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(1, fileBacked.getAllTasks().size(), "Размер HashMap должен быть равен 1.");
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Задача успешно обновлена.", response.body(), "Задача должна быть обновлена.");
    }

    @Test
    void shouldRemoveTaskByIdWhenDeleteRequest() throws IOException, InterruptedException, ManagerSaveException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=2");
        fileBacked.createNewTask(new Task("tn1", "td1"));
        Task task2 = fileBacked.createNewTask(new Task("tn2", "td2", "01.01.2000 12:00", 10));
        assertEquals(2, fileBacked.getAllTasks().size(), "Размер HashMap должен быть равен 2.");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Задача 2 успешно удалена.", response.body(), "Задача 2 должна быть удалена.");
        assertEquals(1, fileBacked.getAllTasks().size(), "Размер HashMap должен быть равен 1.");
        assertFalse(fileBacked.getAllTasks().containsValue(task2), "HashMap не должна содержать задачу 2.");
    }

    @Test
    void shouldRemoveTasksWhenDeleteRequest() throws IOException, InterruptedException, ManagerSaveException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task1 = fileBacked.createNewTask(new Task("tn", "td"));
        Task task2 = fileBacked.createNewTask(new Task("tn", "td", "01.01.2000 12:00", 10));
        assertEquals(2, fileBacked.getAllTasks().size(), "Размер HashMap должен быть равен 2.");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Задачи удалены.", response.body(), "Задачи должны быть удалены.");
        assertEquals(0, fileBacked.getAllTasks().size(), "Размер HashMap должен быть равен 0.");
        assertFalse(fileBacked.getAllTasks().containsValue(task1), "HashMap не должна содержать задачу1.");
        assertFalse(fileBacked.getAllTasks().containsValue(task2), "HashMap не должна содержать задачу2.");
    }

    @Test
    void shouldReturnEpicsWhenGetRequest() throws ManagerSaveException, IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic1 = fileBacked.createNewEpic(new Epic("en", "ed"));
        fileBacked.createNewSubtask(epic1,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic1.getId()));
        fileBacked.createNewSubtask(epic1,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic1.getId()));
        fileBacked.createNewEpic(new Epic("en", "ed"));
        String json = gson.toJson(fileBacked.getAllEpics());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals(json, response.body(), "HashMap epics в виде Json не совпадает с Json ответом сервера.");
    }

    @Test
    void shouldNotReturnEpicsWhenGetRequestAndEpicsIsEmpty() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        assertEquals(0, fileBacked.getAllEpics().size(), "Размер HashMap должен быть равен 0.");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Эпиков нет.", response.body(), "HashMap tasks в виде Json не совпадает с Json ответом сервера.");
    }

    @Test
    void shouldReturnEpicByIdWhenGetRequest() throws ManagerSaveException, IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        Epic epic1 = fileBacked.createNewEpic(new Epic("en", "ed"));
        fileBacked.createNewSubtask(epic1,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic1.getId()));
        fileBacked.createNewSubtask(epic1,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic1.getId()));
        fileBacked.createNewEpic(new Epic("en", "ed"));
        String json = gson.toJson(fileBacked.getEpic(epic1.getId()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals(json, response.body(), "Эпик в виде Json не совпадает с Json ответом сервера.");
    }

    @Test
    void shouldAddNewEpicWhenPostRequest() throws IOException, InterruptedException, ManagerSaveException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = new Epic("en", "ed");
        epic.setStatus(TaskStatus.NEW);
        epic.setId(1);
        assertEquals(0, fileBacked.getAllEpics().size(), "Размер HashMap должен быть равен 0.");
        String json1 = gson.toJson(epic);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json1))
                .build();
        HttpResponse<String> response1 = fileBacked.getClient().getClient().send(request1,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Эпик добавлен.", response1.body(),
                "Эпик должен быть добавлен.");
        assertEquals(1, fileBacked.getAllEpics().size(), "Размер HashMap должен быть равен 1.");
        assertEquals(epic.getName(), fileBacked.getEpic(epic.getId()).getName(), "Эпики не равны.");
    }

    @Test
    void shouldUpdateEpicWhenPostRequest() throws IOException, InterruptedException, ManagerSaveException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        assertEquals(0, fileBacked.getAllEpics().size(), "Размер HashMap должен быть равен 0.");
        Epic epic = fileBacked.createNewEpic(new Epic("en", "ed"));
        Subtask subtask = fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic.getId()));
        subtask.setStatus(TaskStatus.DONE);
        Epic epic1 = new Epic("en", "ed");
        epic1.setStatus(TaskStatus.DONE);
        assertEquals(1, fileBacked.getAllEpics().size(), "Размер HashMap должен быть равен 1.");
        String json = gson.toJson(epic1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(1, fileBacked.getAllEpics().size(), "Размер HashMap должен быть равен 1.");
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым.");
        assertEquals("Эпик успешно обновлен.", response.body(), "Эпик должен быть обновлен.");
        assertEquals(epic.getName(), epic1.getName(), "Поля названия не совпадают.");
        assertEquals(epic.getDescription(), epic1.getDescription(), "Поля описания не совпадают.");
        assertNotEquals(epic.getId(), epic1.getId(), "Идентификаторы совпадают.");
    }

    @Test
    void shouldRemoveEpicByIdWhenDeleteRequest() throws IOException, InterruptedException, ManagerSaveException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        Epic epic1 = fileBacked.createNewEpic(new Epic("en", "ed"));
        Subtask subtask1 = fileBacked.createNewSubtask(epic1,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic1.getId()));
        Subtask subtask2 = fileBacked.createNewSubtask(epic1,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic1.getId()));
        fileBacked.createNewEpic(new Epic("en", "ed"));
        assertEquals(2, fileBacked.getAllEpics().size(), "Размер HashMap должен быть равен 2.");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Эпик успешно удалён.", response.body(),
                "Эпик должен быть удален.");
        assertEquals(1, fileBacked.getAllEpics().size(), "Размер HashMap должен быть равен 1.");
        assertFalse(fileBacked.getAllSubtasks().containsValue(subtask1),
                "HashMap не должна содержать подзадачу 1.");
        assertFalse(fileBacked.getAllSubtasks().containsValue(subtask2),
                "HashMap не должна содержать подзадачу 2.");
    }

    @Test
    void shouldRemoveEpicsWhenDeleteRequest() throws IOException, InterruptedException, ManagerSaveException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic = fileBacked.createNewEpic(new Epic("en", "ed"));
        fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic.getId()));
        fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
        fileBacked.createNewEpic(new Epic("en", "ed"));
        assertEquals(2, fileBacked.getAllEpics().size(), "Размер HashMap должен быть равен 2.");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Эпики удалены.", response.body(), "Эпики должны быть удалены.");
        assertEquals(0, fileBacked.getAllEpics().size(), "Размер HashMap должен быть равен 0.");
        assertEquals(0, fileBacked.getAllSubtasks().size(), "Размер HashMap должен быть равен 0.");
    }

    @Test
    void shouldReturnSubtasksWhenGetRequest() throws ManagerSaveException, IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        Epic epic = fileBacked.createNewEpic(new Epic("en", "ed"));
        fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic.getId()));
        fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
        String json = gson.toJson(fileBacked.getAllSubtasks());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals(json, response.body(), "HashMap subtasks в виде Json не совпадает с Json ответом сервера.");
    }

    @Test
    void shouldNotReturnSubtasksWhenGetRequestAndSubtasksIsEmpty() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        assertEquals(0, fileBacked.getAllSubtasks().size(), "Размер HashMap должен быть равен 0.");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Подзадач нет.", response.body(),
                "HashMap tasks в виде Json не совпадает с Json ответом сервера.");
    }

    @Test
    void shouldReturnSubtaskByIdWhenGetRequest() throws ManagerSaveException, IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        Epic epic = fileBacked.createNewEpic(new Epic("en", "ed"));
        fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic.getId()));
        fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
        String json = gson.toJson(fileBacked.getSubtask(2));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals(json, response.body(), "Подзадача в виде Json не совпадает с Json ответом сервера.");
    }

    @Test
    void shouldAddNewSubtaskWhenPostRequest() throws IOException, InterruptedException, ManagerSaveException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        Epic epic = fileBacked.createNewEpic(new Epic("en", "ed"));
        Subtask subtask = new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic.getId());
        subtask.setStatus(TaskStatus.NEW);
        subtask.setId(2);
        assertEquals(0, fileBacked.getAllSubtasks().size(), "Размер HashMap должен быть равен 0.");
        String json1 = gson.toJson(subtask);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json1))
                .build();
        HttpResponse<String> response1 = fileBacked.getClient().getClient().send(request1,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Подзадача добавлена.", response1.body(),
                "Подзадача должна быть добавлена.");
        assertEquals(1, fileBacked.getAllSubtasks().size(), "Размер HashMap должен быть равен 1.");
    }

    @Test
    void shouldUpdateSubtaskWhenPostRequest() throws IOException, InterruptedException, ManagerSaveException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        assertEquals(0, fileBacked.getAllSubtasks().size(), "Размер HashMap должен быть равен 0.");
        Epic epic = fileBacked.createNewEpic(new Epic("en", "ed"));
        fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, 1));
        Subtask subtask = new Subtask("sn", "sd", "01.01.2000 12:00", 10, 1);
        subtask.setStatus(TaskStatus.DONE);
        assertEquals(1, fileBacked.getAllSubtasks().size(), "Размер HashMap должен быть равен 1.");
        String json = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(1, fileBacked.getAllSubtasks().size(), "Размер HashMap должен быть равен 1.");
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Подзадача успешно обновлена.", response.body(), "Задача должна быть обновлена.");
    }

    @Test
    void shouldRemoveSubtaskByIdWhenDeleteRequest() throws IOException, InterruptedException, ManagerSaveException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        Epic epic = fileBacked.createNewEpic(new Epic("en", "ed"));
        fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic.getId()));
        Subtask subtask2 = fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
        assertEquals(2, fileBacked.getAllSubtasks().size(), "Размер HashMap должен быть равен 2.");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Подзадача 2 успешно удалена.", response.body(),
                "Подзадача 2 должна быть удалена.");
        assertEquals(1, fileBacked.getAllSubtasks().size(), "Размер HashMap должен быть равен 1.");
        assertFalse(fileBacked.getAllTasks().containsValue(subtask2),
                "HashMap не должна содержать подзадачу2.");
    }

    @Test
    void shouldRemoveSubtasksWhenDeleteRequest() throws IOException, InterruptedException, ManagerSaveException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        Epic epic = fileBacked.createNewEpic(new Epic("en", "ed"));
        Subtask subtask1 = fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic.getId()));
        Subtask subtask2 = fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
        assertEquals(2, fileBacked.getAllSubtasks().size(), "Размер HashMap должен быть равен 2.");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Код статуса сервера не совпадает с ожидаемым." );
        assertEquals("Подзадачи удалены.", response.body(), "Подзадачи должны быть удалены.");
        assertEquals(0, fileBacked.getAllSubtasks().size(), "Размер HashMap должен быть равен 0.");
        assertFalse(fileBacked.getAllSubtasks().containsValue(subtask1),
                "HashMap не должна содержать подзадачу1.");
        assertFalse(fileBacked.getAllSubtasks().containsValue(subtask2),
                "HashMap не должна содержать подзадачу 2.");
    }

    @Test
    void shouldNotReturnSubtaskListWhenEpicsNotContainsEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=5");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals("Подзадач нет.", response.body(), "SubtaskList должен быть пустым.");
    }

    @Test
    void shouldReturnSubtaskListByEpicId() throws ManagerSaveException, IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
        Epic epic = fileBacked.createNewEpic(new Epic("en", "ed"));
        fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic.getId()));
        String json = gson.toJson(fileBacked.getSubtasksOfEpic(epic));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(json, response.body(), "SubtaskList в виде Json не совпадает с Json ответом сервера.");
    }

    @Test
    void shouldReturnHistoryWhenGetRequest() throws ManagerSaveException, IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/history");
        Epic epic = fileBacked.createNewEpic(new Epic("en", "ed"));
        Subtask subtask1 = fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic.getId()));
        Subtask subtask2 = fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
        Task task = fileBacked.createNewTask(new Task("tn", "td", "03.01.2000 12:00", 10));
        fileBacked.getTask(task.getId());
        fileBacked.getEpic(epic.getId());
        fileBacked.getSubtask(subtask1.getId());
        fileBacked.getSubtask(subtask2.getId());
        List<Task> history = fileBacked.getHistory();
        String jsonHistory = gson.toJson(history);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(jsonHistory, response.body(), "history в виде Json не совпадает с Json ответом сервера.");
    }

    @Test
    void shouldReturnPrioritizedTasksWhenGetRequest() throws ManagerSaveException, IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/");
        Epic epic = fileBacked.createNewEpic(new Epic("en", "ed"));
        fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "01.01.2000 12:00", 10, epic.getId()));
        fileBacked.createNewSubtask(epic,
                new Subtask("sn", "sd", "02.01.2000 12:00", 10, epic.getId()));
        fileBacked.createNewTask(new Task("tn", "td", "03.01.2000 12:00", 10));
        Set<Task> sortedTasks = fileBacked.getPrioritizedTasks();
        String json = gson.toJson(sortedTasks);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = fileBacked.getClient().getClient().send(request,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(json, response.body(), "sortedTasks в виде Json не совпадает с Json ответом сервера.");
    }
}