import java.util.Iterator;
import java.util.NoSuchElementException;

public class SinglyLinkedList<E> implements Iterable<E> {
    class Node {
        private final E item;
        private Node next;

        public Node(E item) {
            this.item = item;
            this.next = null;
        }

        public E getItem() {
            return item;
        }
    }

    private Node head;
    private Node tail;
    private int size = 0;

    public void add(E item, int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        Node newNode = new Node(item);

        if (index == 0) {
            newNode.next = head;
            head = newNode;
            if (tail == null) {
                tail = newNode;
            }
        } else if (index == size) {
            tail.next = newNode;
            tail = newNode;
        } else {
            Node current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }

        size++;
    }

    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        Node removedNode;

        if (index == 0) {
            removedNode = head;
            head = head.next;
            if (head == null) {
                tail = null;
            }
        } else {
            Node current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            removedNode = current.next;
            current.next = removedNode.next;
            if (index == size - 1) {
                tail = current;
            }
        }

        size--;
        return removedNode.item;
    }

    public Node getNodeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    public int size() {
        return size;
    }

    public E head() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }
        return head.item;
    }

    public E tail() {
        if (tail == null) {
            throw new NoSuchElementException("List is empty");
        }
        return tail.item;
    }

    public void append(E item) {
        add(item, size);
    }

    public void prepend(E item) {
        add(item, 0);
    }

    public E removeHead() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }
        return remove(0);
    }

    public E removeTail() {
        if (tail == null) {
            throw new NoSuchElementException("List is empty");
        }
        return remove(size - 1);
    }

    public void removeAll(E item) {
        while (head != null && head.item.equals(item)) {
            head = head.next;
            size--;
        }
        if (head == null) {
            tail = null;
        } else {
            Node current = head;
            while (current.next != null) {
                if (current.next.item.equals(item)) {
                    current.next = current.next.next;
                    size--;
                } else {
                    current = current.next;
                }
            }
            tail = current;
        }
    }

    public boolean contains(E item) {
        Node current = head;
        while (current != null) {
            if (current.item.equals(item)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public int occurrences(E item) {
        int count = 0;
        Node current = head;
        while (current != null) {
            if (current.item.equals(item)) {
                count++;
            }
            current = current.next;
        }
        return count;
    }

    public int[] toArray() {
        int[] arr = new int[size];
        Node current = head;
        int index = 0;
        while (current != null) {
            arr[index++] = (Integer) current.item;
            current = current.next;
        }
        return arr;
    }

    public void print() {
        Node current = head;
        while (current != null) {
            System.out.print(current.item + " ");
            current = current.next;
        }
        System.out.println();
    }

    public Iterator<E> iterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<E> {
        private Node current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E item = current.item;
            current = current.next;
            return item;
        }
    }
}