package web_api.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.IncorrectTaskException;
import exception.NotFoundException;
import exception.TaskOverlapException;
import ru.yandex.practicum.model.Task;
import ru.yandex.practicum.service.TaskManager;
import web_api.BaseHttpHandler;
import web_api.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS:
                handleGetTasks(exchange);
                break;
            case GET_TASK_ID:
                handleGetTaskId(exchange);
                break;
            case POST_TASK:
                handlePostTask(exchange);
                break;
            case DELETE_TASK_ID:
                handleDeleteTaskId(exchange);
                break;
            case UNKNOWN:
                sendNoSuchEndpoint(exchange);
                break;
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String jsonTasks = gson.toJson(taskManager.getAllTasksList());
        send200(exchange, jsonTasks);
    }

    private void handleGetTaskId(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);

        if (taskIdOpt.isEmpty()) {
            send400(exchange, "Передан некорректный идентификатор задачи");
        } else {
            try {
                Task task = taskManager.getTaskById(taskIdOpt.get());
                String jsonTask = gson.toJson(task);
                send200(exchange, jsonTask);
            } catch (NotFoundException ex) {
                send404NotFound(exchange, ex.getMessage());
            }
        }
        /* Вернуть задачу если она найдена, код статуса — 200.
           Если запрос был составлен неверно, верните сообщение об ошибке с кодом 400.
           Если пост с указанным идентификатором не найден, верните сообщение об этом с кодом 404. */
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String jsonString = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

        //Сервер не обнаружил запрашиваемый контент
        if (jsonString.isEmpty() || jsonString.isBlank())
            send400(exchange, "Передана пустая задача");

        try {
            Task taskFromJson = gson.fromJson(jsonString, Task.class);

            //Если в теле запроса задаче не был присвоен Id, пытаемся создать задачу
            if (taskFromJson.getId() == null) {
                taskManager.createTask(taskFromJson);
                send201(exchange, "Задача добавлена");
            } else {
                taskManager.updateTask(taskFromJson);
                send201(exchange, "Задача обновлена");
            }
        } catch (IncorrectTaskException ex) {
            send400(exchange, ex.getMessage());
        } catch (TaskOverlapException ex) {
            send406HasInteractions(exchange, ex.getMessage());
        } catch (NotFoundException ex) {
            send404NotFound(exchange, ex.getMessage());
        } catch (JsonSyntaxException ex) {
            //Сервер не обнаружил запрашиваемый контент
            send400(exchange, "Передан некорректный формат запроса");
        }
    }


    private void handleDeleteTaskId(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);

        if (taskIdOpt.isEmpty()) {
            send400(exchange, "Передан некорректный идентификатор задачи");
        } else {
            try {
                taskManager.deleteTaskById(taskIdOpt.get());
                send200(exchange, "Задача удалена");
            } catch (NotFoundException ex) {
                send404NotFound(exchange, ex.getMessage());
            }
        }
        /* Если задача найдена, удалить ее и вернуть код статуса — 200.
           Если запрос был составлен неверно, верните сообщение об ошибке с кодом 400.
           Если пост с указанным идентификатором не найден, верните сообщение об этом с кодом 404. */
    }
}