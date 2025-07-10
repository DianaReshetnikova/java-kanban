package web_api.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime != null) {
            jsonWriter.value(localDateTime.format(dtf));
        } else {
            jsonWriter.value("");
        }
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String startTimeStr = jsonReader.nextString();
        if (!startTimeStr.isEmpty()) {
            return LocalDateTime.parse(startTimeStr, dtf);
        }
        return null;
    }
}
