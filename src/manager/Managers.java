package manager;

import java.io.File;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        String path = "resources" + File.separator + "data.csv";
        File file = new File(path);

        File dir = new File("resources");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return new FileBackedTaskManager(file, getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
