package manager;

import tasks.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    // Внутренний класс для узла двусвязного списка.
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    // Добавление задачи в историю просмотров.
    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());

        Node node = new Node(task);
        LinkLast(node);
        nodeMap.put(task.getId(), node);
    }

    // Удаление задачи из истории по ID.
    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);

        if (node != null) {
            removeNode(node);
        }
    }

    // Получение списка задач из истории просмотров.
    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();

        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    // Добавление узла в конец двусвязного списка.
    private void LinkLast(Node node) {
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    // Удаление узла из двусвязного списка
    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }
}

