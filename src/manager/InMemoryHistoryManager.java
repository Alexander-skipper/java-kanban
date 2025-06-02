package manager;

import tasks.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 10;
    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public List<Task> getHistory() {
        //Получить список историй.
        return new LinkedList<>(history);
    }

    @Override
    public void add(Task task) {
        //Добавить таску в список с историей.
        if (task == null) {
            return;
        }
        if (history.size() >= MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
        history.remove(task);
        history.addLast(task);

    }
}
