package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.service.TaskManager;
import server.BaseHttpHandler;
import server.Endpoint;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_PRIORITIZED:
                handleGetPrioritized(exchange);
                break;
            case UNKNOWN:
                sendNoSuchEndpoint(exchange);
                break;
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        String jsonTasks = gson.toJson(taskManager.getPrioritizedTasks());
        send200(exchange, jsonTasks);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET") && pathParts.length == 2 && pathParts[1].equals("prioritized")) {
            return Endpoint.GET_PRIORITIZED;
        }

        return Endpoint.UNKNOWN;
    }
}