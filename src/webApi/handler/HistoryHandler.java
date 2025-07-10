package webApi.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.service.TaskManager;
import webApi.BaseHttpHandler;
import webApi.Endpoint;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY:
                handleGetHistory(exchange);
                break;
            case UNKNOWN:
                sendNoSuchEndpoint(exchange);
                break;
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String jsonTasks = gson.toJson(taskManager.getHistory());
        send200(exchange, jsonTasks);
    }
}
