package web_api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.model.Task;
import ru.yandex.practicum.service.Managers;
import ru.yandex.practicum.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static TaskManager taskManager;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;

        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
//        httpTaskServer.stopHttpServer();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.startHttpServer();
    }

    public void startHttpServer() {
        httpServer.start();
    }

    public void stopHttpServer() {
        httpServer.stop(0);
    }
}