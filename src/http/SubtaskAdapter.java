package http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.Epic;
import model.Subtask;
import model.TaskStatus;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class SubtaskAdapter extends TypeAdapter<Subtask> {
    HashMap<Integer, Epic> epics;

    public SubtaskAdapter(HashMap<Integer, Epic> epics) {
        this.epics = epics;
    }

    @Override
    public void write(JsonWriter jsonWriter, Subtask subtask) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("Class").value("Subtask");
        jsonWriter.name("name").value(subtask.getName());
        jsonWriter.name("description").value(subtask.getDescription());
        jsonWriter.name("id").value(subtask.getId());
        jsonWriter.name("status").value(subtask.getStatus().toString());
        jsonWriter.name("startTime").value(subtask.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        jsonWriter.name("duration").value(subtask.getDuration().toMinutes());
        jsonWriter.name("endTime").value(subtask.getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        jsonWriter.name("epicId").value(subtask.getEpicId());
        jsonWriter.endObject();
    }

    @Override
    public Subtask read(JsonReader jsonReader) {
        JsonElement jsonElement = JsonParser.parseReader(jsonReader);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Subtask subtask;
        subtask = new Subtask(jsonObject.get("name").getAsString(),
                jsonObject.get("description").getAsString(),
                String.format(jsonObject.get("startTime").getAsString(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                jsonObject.get("duration").getAsInt(),
                jsonObject.get("epicId").getAsInt());
        if (jsonObject.get("status") != null) {
            subtask.setStatus(TaskStatus.valueOf(jsonObject.get("status").getAsString()));
        }
        return subtask;
    }
}
