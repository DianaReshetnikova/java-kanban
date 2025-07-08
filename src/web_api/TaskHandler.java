package web_api;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskOverlapException;
import ru.yandex.practicum.model.Task;
import ru.yandex.practicum.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private TaskManager taskManager;
    private Gson gson;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(DurationAdapter.class, new DurationAdapter())
                .create();
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
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
                break;
        }

    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET") && pathParts.length == 2 && pathParts[1].equals("tasks")) {
            return Endpoint.GET_TASKS;
        } else if (requestMethod.equals("GET") && pathParts.length == 3 && pathParts[1].equals("tasks")) {
            return Endpoint.GET_TASK_ID;
        } else if (requestMethod.equals("POST") && pathParts.length == 2 && pathParts[1].equals("tasks")) {
            return Endpoint.POST_TASK;
        } else if (requestMethod.equals("DELETE") && pathParts.length == 3 && pathParts[1].equals("tasks")) {
            return Endpoint.DELETE_TASK_ID;
        }

        return Endpoint.UNKNOWN;
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String jsonTasks = gson.toJson(taskManager.getAllTasksList());
        sendText(exchange, jsonTasks);
    }

    private void handleGetTaskId(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);

        if (taskIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
        } else {
            //проверить если ли запрошенный taskId в списке tasks
            var taskOptionalFind = taskManager.getAllTasksList().stream()
                    .filter(task -> Objects.equals(task.getId(), taskIdOpt.get()))
                    .findFirst();

            if (taskOptionalFind.isEmpty())
                writeResponse(exchange, String.format("Задача с идентификатором %d не найдена", taskIdOpt.get()), 404);
            else {
                Task task = taskOptionalFind.get();
                String jsonTask = gson.toJson(task);
                writeResponse(exchange, jsonTask, 200);
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
            writeResponse(exchange, "Передана пустая задача", 400);

        try {
            Task taskFromJson = gson.fromJson(jsonString, Task.class);

            var taskFindOpt = taskManager.getAllTasksList().stream()
                    .filter(task -> Objects.equals(task.getId(), taskFromJson.getId()))
                    .findFirst();

            try {
                if (taskFindOpt.isEmpty()) {
                    //Пытаемся создать задачу с новым Id
                    taskManager.createTask(taskFromJson);
                    sendText(exchange, "Задача добавлена");
                } else {
                    //Обновляем старую задачу с таким же Id на новую
                    taskManager.updateTask(taskFromJson);
                    sendText(exchange, "Задача обновлена");
                }
            } catch (TaskOverlapException ex) {
                sendHasInteractions(exchange, ex.getMessage());
            }
        } catch (JsonSyntaxException ex) {
            //Сервер не обнаружил запрашиваемый контент
            writeResponse(exchange, "", 404);
        }
    }


    private void handleDeleteTaskId(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(exchange);

        if (taskIdOpt.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
        } else {
            //проверить если ли запрошенный taskId в списке tasks
            var taskOptionalFind = taskManager.getAllTasksList().stream()
                    .filter(task -> Objects.equals(task.getId(), taskIdOpt.get()))
                    .findFirst();

            if (taskOptionalFind.isEmpty())
                writeResponse(exchange, String.format("Задача с идентификатором %d не найдена", taskIdOpt.get()), 404);
            else {
                Task task = taskOptionalFind.get();
                taskManager.deleteTaskById(task.getId());
                writeResponse(exchange, "Задача удалена", 200);
            }
        }
        /* Если задача найдена, удалить ее и вернуть код статуса — 200 + пустое тело.
           Если запрос был составлен неверно, верните сообщение об ошибке с кодом 400.
           Если пост с указанным идентификатором не найден, верните сообщение об этом с кодом 404. */
    }


    //МОЖНО ВЫНЕСТИ В КЛАСС BASE_HTTP
    private Optional<Integer> getTaskId(HttpExchange exchange) {
        //Если идентификатор не является числом, вернyть Optional.empty()
        String taskId = exchange.getRequestURI().getPath().split("/")[2];
        try {
            return Optional.of(Integer.parseInt(taskId));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }
}