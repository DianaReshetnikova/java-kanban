package web_api;

import com.sun.net.httpserver.HttpExchange;

import javax.swing.text.DefaultStyledDocument;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

//Чтобы не дублировать код, можно использовать общий для всех HTTP-обработчиков класс.
//содержит общие методы для чтения и отправки данных
public class BaseHttpHandler {
    private static final Charset CharsetDefault = StandardCharsets.UTF_8;

    //для отправки общего ответа в случае успеха;
    public void sendText(HttpExchange httpExchange, String responseBody) throws IOException {
        httpExchange.getResponseHeaders()
                .add("Content-Type", "application/json; charset=utf-8");

        if (httpExchange.getRequestMethod().equals("GET"))
            httpExchange.sendResponseHeaders(200, 0);
        else if (httpExchange.getRequestMethod().equals("POST"))
            httpExchange.sendResponseHeaders(201, 0);

        try (OutputStream out = httpExchange.getResponseBody()) {
            out.write(responseBody.getBytes(CharsetDefault));
        }

        httpExchange.close();
    }

    protected void sendCreated(HttpExchange h, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");

        h.sendResponseHeaders(201, response.length);
        h.getResponseBody().write(response);
        h.close();
    }

    //для отправки ответа в случае, если объект не был найден;
    public void sendNotFound(HttpExchange httpExchange, String responseBody) throws IOException {

        httpExchange.getResponseHeaders()
                .add("Content-Type", "application/json; charset=utf-8");

        httpExchange.sendResponseHeaders(404, 0);
        try (OutputStream out = httpExchange.getResponseBody()) {
            out.write(responseBody.getBytes(CharsetDefault));
        }

        httpExchange.close();
    }

    //для отправки ответа, если при создании или обновлении задача пересекается с уже существующими.
    public void sendHasInteractions(HttpExchange httpExchange, String responseBody) throws IOException {
        httpExchange.getResponseHeaders()
                .add("Content-Type", "application/json; charset=utf-8");

        //Not Acceptable — запрошенный URI не может удовлетворить переданным в заголовке характеристикам
        httpExchange.sendResponseHeaders(406, 0);
        try (OutputStream out = httpExchange.getResponseBody()) {
            out.write(responseBody.getBytes(CharsetDefault));
        }

        httpExchange.close();
    }
}
