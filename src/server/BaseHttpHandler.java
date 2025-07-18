package server;

import server.adapter.DurationAdapter;
import server.adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

//Чтобы не дублировать код, можно использовать общий для всех HTTP-обработчиков класс.
//содержит общие методы для чтения и отправки данных
public class BaseHttpHandler {
    protected final Charset defaultCharset = StandardCharsets.UTF_8;

    protected Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();


    protected void sendNoSuchEndpoint(HttpExchange httpExchange) throws IOException {
        writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
    }

    protected void send200(HttpExchange httpExchange, String responseBody) throws IOException {
        writeResponse(httpExchange, responseBody, 200);
    }

    protected void send201(HttpExchange httpExchange, String responseBody) throws IOException {
        writeResponse(httpExchange, responseBody, 201);
    }

    protected void send400(HttpExchange httpExchange, String responseBody) throws IOException {
        writeResponse(httpExchange, responseBody, 400);
    }

    //для отправки ответа в случае, если объект не был найден;
    protected void send404NotFound(HttpExchange httpExchange, String responseBody) throws IOException {
        writeResponse(httpExchange, responseBody, 404);
    }

    //для отправки ответа, если при создании или обновлении задача пересекается с уже существующими.
    protected void send406HasInteractions(HttpExchange httpExchange, String responseBody) throws IOException {
        writeResponse(httpExchange, responseBody, 406);
    }

    //Если идентификатор не является числом, вернyть Optional.empty()
    //Вынесен в общие методы, т.к. Id Task/Epic/Subtask в запросе всегда с индексом = 2
    protected Optional<Integer> getTaskId(HttpExchange exchange) {
        String taskId = exchange.getRequestURI().getPath().split("/")[2];
        try {
            return Optional.of(Integer.parseInt(taskId));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        exchange.getResponseHeaders()
                .add("Content-Type", "application/json; charset=utf-8");

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(defaultCharset));
        }
        exchange.close();
    }
}
