package manager;

import tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    //перенести сюда список истории из InMemoryTaskManager
    private static final int MAX_HISTORY_SIZE = 10;
    private final List<Task> history = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        //Получить список историй.
        return new ArrayList<>(history);
    }

    @Override
    public void add(Task task) {
        //Добавить таску в список с историей.
        if (task == null) {
            return;
        }
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
        history.remove(task);
        history.add(task);

    }
}
