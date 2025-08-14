package manager.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.TaskManager;
import java.net.http.HttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class CommonTestSetup {
    protected HttpTaskServer server;
    protected TaskManager taskManager;
    protected Gson gson;
    protected HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new HttpTaskServer();
        server.start();
        taskManager = server.getTaskManager();

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new TimeAdapters.LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new TimeAdapters.DurationAdapter())
                .create();

        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }
}

