package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {

    HashMap<Integer, Node<Task>> taskHistoryNodes = new HashMap<>();

    Node<Task> head;
    Node<Task> tail;
    int size = 0;

    void linkLast(Task task) {
        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        }
        else {
            oldTail.next = newNode;
        }
        size++;
        Task newTask = newNode.task;
        taskHistoryNodes.put(newTask.id, newNode);
    }

    List<Task> getTasks() {
        ArrayList<Task> taskHistory= new ArrayList<>();
        Node<Task> taskNode = head;
        while (taskNode != null) {
            taskHistory.add(taskNode.task);
            taskNode = taskNode.next;
        }
        return taskHistory;
    }

    void removeNode(Node<Task> taskNode) {
        Node<Task> prevNode = taskNode.prev;
        Node<Task> nextNode = taskNode.next;

        if (prevNode == null) {
            head = nextNode;
        } else {
            prevNode.next = nextNode;
            taskNode.prev = null;
        }

        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.prev = prevNode;
            taskNode.next = null;
        }

        taskNode.task = null;
        size--;
    }

    @Override
    public void add(Task task) {
        if (taskHistoryNodes.containsKey(task.getId())) {
            remove(task.getId());
            linkLast(task);
        }
        else {
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        if (taskHistoryNodes.containsKey(id)) {
            getTasks().remove(id);
            removeNode(taskHistoryNodes.get(id));
            taskHistoryNodes.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {

        return getTasks();
    }
}