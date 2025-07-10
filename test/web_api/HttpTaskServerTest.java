package web_api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;
import ru.yandex.practicum.service.Managers;
import ru.yandex.practicum.service.Status;
import ru.yandex.practicum.service.TaskManager;
import web_api.adapter.DurationAdapter;
import web_api.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private TaskManager taskManager = Managers.getDefault();
    private HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    private Epic epic;
    private SubTask subtask1;
    private SubTask subtask2;
    private Task task1;
    private Task task2;
    private Task task3;

    HttpTaskServerTest() throws IOException {

    }

    @BeforeEach
    void setUp() throws IOException {
        epic = new Epic("epic", "desc");
        taskManager.createEpic(epic);

        subtask1 = new SubTask("subtask 1", "description", Status.DONE, epic.getId(),
                LocalDateTime.of(2025, 3, 2, 14, 20),
                Duration.ofMinutes(30));
        subtask2 = new SubTask("subtask 2", "subtask without time", Status.NEW, epic.getId());

        task1 = new Task("task 1", "description", Status.NEW,
                LocalDateTime.of(2025, 3, 5, 23, 45),
                Duration.ofMinutes(30));
        task2 = new Task("task 2", "description", Status.NEW,
                LocalDateTime.of(2025, 3, 5, 15, 45),
                Duration.ofMinutes(60));
        task3 = new Task("task 3", "Task without time", Status.NEW);

        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        httpTaskServer = new HttpTaskServer(taskManager);

        httpTaskServer.startHttpServer();
    }

    private void clearManager() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllEpics();
    }

    @AfterEach
    void stopServer() {
        httpTaskServer.stopHttpServer();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        httpTaskServer.stopHttpServer();
        clearManager();

        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getAllTasksList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        clearManager();

        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 2",
                LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> epicsFromManager = taskManager.getAllEpicsList();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 2", epicsFromManager.get(0).getTitle(), "Некорректное имя эпика");
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        clearManager();

        Task task1 = new Task(
                "Task 1",
                "Test task 1 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                Duration.ofHours(1)
        );

        Task task2 = new Task(
                "Task 2",
                "Test task 2 description",
                Status.NEW,
                LocalDateTime.of(2025, 1, 2, 8, 0),
                Duration.ofHours(1)
        );

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),
                "Ожидался код 200 в процессе получения списка задач");
        assertEquals(response.body(), gson.toJson(taskManager.getAllTasksList()),
                "Тело ответа от сервера должно совпадать со списком taskManager.getAllTasksList()");
    }

    @Test
    public void getEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic(
                "Epic 1",
                "Test epic 1 description",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                Duration.ofHours(1)
        );
        Epic epic2 = new Epic(
                "Epic 2",
                "Test epic 2 description",
                LocalDateTime.of(2025, 1, 2, 0, 0),
                Duration.ofHours(1)
        );

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),
                "Ожидался код 200 в процессе получения списка эпиков");

        assertEquals(response.body(), gson.toJson(taskManager.getAllEpicsList()),
                "Тело ответа от сервера должно совпадать со списком taskManager.getAllEpicsList()");
    }


}