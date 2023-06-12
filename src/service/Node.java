package service;

class Node<Task> {

    public Node<Task> next;
    public Node<Task> prev;
    public Task task;

    public Node(Node<Task> prev, Task task, Node<Task> next) {
        this.next = next;
        this.prev = prev;
        this.task = task;
    }
}