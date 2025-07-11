package server.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.IncorrectTaskException;
import exception.NotFoundException;
import exception.TaskOverlapException;
import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.service.TaskManager;
import server.BaseHttpHandler;
import server.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS:
                handleGetEpics(exchange);
                break;
            case GET_EPIC_ID:
                handleGetEpicId(exchange);
                break;
            case POST_EPIC:
                handlePostEpic(exchange);
                break;
            case DELETE_EPIC_ID:
                handleDeleteEpicId(exchange);
                break;
            case GET_SUBTASKS_OF_EPIC:
                handleGetSubtasksOfEpic(exchange);
                break;
            case UNKNOWN:
                sendNoSuchEndpoint(exchange);
                break;
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String jsonEpics = gson.toJson(taskManager.getAllEpicsList());
        send200(exchange, jsonEpics);
    }

    private void handleGetSubtasksOfEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getTaskId(exchange);

        if (epicIdOpt.isEmpty()) {
            send400(exchange, "Передан некорректный идентификатор эпика");
        } else {
            try {
                List<SubTask> result = taskManager.getSubTasksOfEpicById(epicIdOpt.get());
                String jsonEpic = gson.toJson(result);
                send200(exchange, jsonEpic);
            } catch (NotFoundException ex) {
                send404NotFound(exchange, ex.getMessage());
            }
        }
    }

    private void handleGetEpicId(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getTaskId(exchange);

        if (epicIdOpt.isEmpty()) {
            send400(exchange, "Передан некорректный идентификатор эпика");
        } else {
            try {
                Epic epic = taskManager.getEpicById(epicIdOpt.get());
                String jsonEpic = gson.toJson(epic);
                send200(exchange, jsonEpic);
            } catch (NotFoundException ex) {
                send404NotFound(exchange, ex.getMessage());
            }
        }
        /* Вернуть задачу если она найдена, код статуса — 200.
           Если запрос был составлен неверно, верните сообщение об ошибке с кодом 400.
           Если задача с указанным идентификатором не найдена, верните сообщение об этом с кодом 404. */
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String jsonString = new String(inputStream.readAllBytes(), defaultCharset);

        //Сервер не обнаружил запрашиваемый контент
        if (jsonString.isEmpty() || jsonString.isBlank())
            send400(exchange, "Передан пустой эпик");

        try {
            Epic epicFromJson = gson.fromJson(jsonString, Epic.class);

            //Если в теле запроса задаче не был присвоен Id, пытаемся создать задачу
            if (epicFromJson.getId() == null) {
                taskManager.createEpic(epicFromJson);
                send201(exchange, "Эпик добавлен");
            } else {
                taskManager.updateEpic(epicFromJson);
                send201(exchange, "Эпик обновлен");
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

    private void handleDeleteEpicId(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOpt = getTaskId(exchange);

        if (epicIdOpt.isEmpty()) {
            send400(exchange, "Передан некорректный идентификатор эпика");
        } else {
            try {
                taskManager.deleteEpicById(epicIdOpt.get());
                send200(exchange, "Эпик удален");
            } catch (NotFoundException ex) {
                send404NotFound(exchange, ex.getMessage());
            }
        }
        /* Если задача найдена, удалить ее и вернуть код статуса — 200.
           Если запрос был составлен неверно, верните сообщение об ошибке с кодом 400.
           Если задача с указанным идентификатором не найден, верните сообщение об этом с кодом 404. */
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET") && pathParts.length == 2 && pathParts[1].equals("epics")) {
            return Endpoint.GET_EPICS;
        } else if (requestMethod.equals("GET") && pathParts.length == 3 && pathParts[1].equals("epics")) {
            return Endpoint.GET_EPIC_ID;
        } else if (requestMethod.equals("GET") && pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
            return Endpoint.GET_SUBTASKS_OF_EPIC;
        } else if (requestMethod.equals("POST") && pathParts.length == 2 && pathParts[1].equals("epics")) {
            return Endpoint.POST_EPIC;
        } else if (requestMethod.equals("DELETE") && pathParts.length == 3 && pathParts[1].equals("epics")) {
            return Endpoint.DELETE_EPIC_ID;
        }

        return Endpoint.UNKNOWN;
    }
}