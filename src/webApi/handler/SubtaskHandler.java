package webApi.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.IncorrectTaskException;
import exception.NotFoundException;
import exception.TaskOverlapException;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.service.TaskManager;
import webApi.BaseHttpHandler;
import webApi.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS:
                handleGetSubtasks(exchange);
                break;
            case GET_SUBTASK_ID:
                handleGetSubtaskId(exchange);
                break;
            case POST_SUBTASK:
                handlePostSubtask(exchange);
                break;
            case DELETE_SUBTASK_ID:
                handleDeleteSubtaskId(exchange);
                break;
            case UNKNOWN:
                sendNoSuchEndpoint(exchange);
                break;
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String jsonSubtasks = gson.toJson(taskManager.getAllSubTasksList());
        send200(exchange, jsonSubtasks);
    }

    private void handleGetSubtaskId(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskIdOpt = getTaskId(exchange);

        if (subtaskIdOpt.isEmpty()) {
            send400(exchange, "Передан некорректный идентификатор подзадачи");

        } else {
            try {
                SubTask subtask = taskManager.getSubTaskById(subtaskIdOpt.get());
                String jsonSubtask = gson.toJson(subtask);
                send200(exchange, jsonSubtask);
            } catch (NotFoundException ex) {
                send404NotFound(exchange, ex.getMessage());
            }
        }
        /* Вернуть задачу если она найдена, код статуса — 200.
           Если запрос был составлен неверно, верните сообщение об ошибке с кодом 400.
           Если задача с указанным идентификатором не найдена, верните сообщение об этом с кодом 404. */
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String jsonString = new String(inputStream.readAllBytes(), defaultCharset);

        //Сервер не обнаружил запрашиваемый контент
        if (jsonString.isEmpty() || jsonString.isBlank())
            send400(exchange, "Передана пустая подзадача");

        try {
            SubTask subtaskFromJson = gson.fromJson(jsonString, SubTask.class);

            //Если в теле запроса задаче не был присвоен Id, пытаемся создать задачу
            if (subtaskFromJson.getId() == null) {
                taskManager.createSubTask(subtaskFromJson);
                send201(exchange, "Подзадача добавлена");
            } else {
                taskManager.updateSubTask(subtaskFromJson);
                send201(exchange, "Подзадача обновлена");
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

    private void handleDeleteSubtaskId(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskIdOpt = getTaskId(exchange);

        if (subtaskIdOpt.isEmpty()) {
            send400(exchange, "Передан некорректный идентификатор подзадачи");
        } else {
            try {
                taskManager.deleteSubTaskById(subtaskIdOpt.get());
                send200(exchange, "Подзадача удалена");
            } catch (NotFoundException ex) {
                send404NotFound(exchange, ex.getMessage());
            }
        }
        /* Если задача найдена, удалить ее и вернуть код статуса — 200.
           Если запрос был составлен неверно, верните сообщение об ошибке с кодом 400.
           Если задача с указанным идентификатором не найден, верните сообщение об этом с кодом 404. */
    }
}