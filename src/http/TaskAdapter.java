package http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.Task;
import model.TaskStatus;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class TaskAdapter extends TypeAdapter<Task> {
    @Override
    public void write(JsonWriter jsonWriter, Task task) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("Class").value("Task");
        jsonWriter.name("name").value(task.getName());
        jsonWriter.name("description").value(task.getDescription());
        jsonWriter.name("id").value(task.getId());
        jsonWriter.name("status").value(task.getStatus().toString());
        jsonWriter.name("startTime").value(task.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        jsonWriter.name("duration").value(task.getDuration().toMinutes());
        jsonWriter.name("endTime").value(task.getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        jsonWriter.endObject();
    }

    @Override
    public Task read(JsonReader jsonReader) {
        JsonElement jsonElement = JsonParser.parseReader(jsonReader);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task task;
        if (jsonObject.get("startTime") == null) {
            task = new Task(jsonObject.get("name").getAsString(),
                    jsonObject.get("description").getAsString());
        } else {
            task = new Task(jsonObject.get("name").getAsString(),
                    jsonObject.get("description").getAsString(),
                    jsonObject.get("startTime").getAsString(),
                    jsonObject.get("duration").getAsInt());
        }
        if (jsonObject.get("status") != null) {
            task.setStatus(TaskStatus.valueOf(jsonObject.get("status").getAsString()));
        }
        return task;
    }
}
