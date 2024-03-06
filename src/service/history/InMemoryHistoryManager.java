package service.history;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> taskHistory;
    private Node first;
    private Node last;

    public InMemoryHistoryManager() {
        this.taskHistory = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        Node node = taskHistory.get(task.getId());
        if (node != null) {
            removeNode(node);
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = taskHistory.get(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> list = new ArrayList<>();
        Node current = first;
        while (current != null) {
            list.add(current.element);
            current = current.next;
        }
        return list;
    }

    void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(task, l, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }

        taskHistory.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        taskHistory.remove(node.element.getId());
    }

    private static class Node {
        private final Task element;
        private Node prev;
        private Node next;

        public Node(Task element, Node prev, Node next) {
            this.element = element;
            this.prev = prev;
            this.next = next;
        }
    }
}
