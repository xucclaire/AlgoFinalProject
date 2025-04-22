import java.util.Comparator;
import java.util.NoSuchElementException;

public class PriorityQueue<T extends Comparable<T>> {
    private final Comparator<T> comparator;
    private final CArrayList<T> queue;
    private int size;


    public PriorityQueue(int capacity, Comparator<T> comparator) {
        this.queue = new CArrayList<>(capacity);
        this.comparator = comparator;
        this.size = 0;
    }
    public PriorityQueue() {
        this(10, null);
    }
    public PriorityQueue(int capacity) {
        this(capacity, null);
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int size() {
        return this.size;
    }

    public T min() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty.");
        }
        return this.queue.get(0);
    }

    public T removeMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty.");
        }
        T result = this.queue.get(0);
        this.queue.set(0, this.queue.get(this.queue.size() - 1));
        this.queue.removeIndex(this.queue.size() - 1);
        size--;
        this.sink(0);
        return result;
    }

    public void add(T item) {
        this.queue.add(this.queue.size(), item);
        this.swim(this.size++);
    }

    private boolean isLess(T a, T b) {
        if (this.comparator != null) {
            return this.comparator.compare(a, b) < 0;
        } else {
            //  !!! TODO !!!
            return a.compareTo(b) < 0;
        }
    }

    private void swap(int i, int j) {
        T temp = this.queue.get(i);
        this.queue.set(i, this.queue.get(j));
        this.queue.set(j, temp);
    }

    private static int left(int index) {
        // Returns the index of the left child for the node at the specified index
        // !!! TODO !!!
        return 2 * index + 1;
    }

    private static int right(int index) {
        // Returns the index of the right child for the node at the specified index
        // !!! TODO !!!
        return 2 * index + 2;
    }

    private static int parent(int index) {
        // Returns the index of the parent node for the node at the specified index
        // !!! TODO !!!
        if (index <= 0) {
            throw new IllegalArgumentException("Root node has no parent.");
        }
        return (index - 1) / 2;
    }

    private static boolean isRoot(int index) {
        // !!! TODO !!!
        return index == 0;
    }

    private boolean isLeaf(int index) {
        // !!! TODO !!!
        return left(index) >= size;
    }

    private int arity(int index) {
        // Returns the number of children for the node at the specified index
        // !!! TODO !!!
        if (right(index) < size) {
            return 2;
        }
        if (left(index) < size) {
            return 1;
        }
        return 0;
    }

    private void sink(int index) {
        // Move the node at the specified index down the three
        // until its value is less than than of its children.
        // !!! TODO !!!
        while (!isLeaf(index)) {
            int leftIndex = left(index);
            int rightIndex = right(index);
            int smallestChild = leftIndex;

            if (rightIndex < this.queue.size() && isLess(this.queue.get(rightIndex), this.queue.get(leftIndex))) {

                smallestChild = rightIndex;
            }

            if (isLess(this.queue.get(index), this.queue.get(smallestChild))) {
                break;
            }

            swap(index, smallestChild);
            index = smallestChild;
        }
    }

    private void swim(int index) {
        // Move the node at the specified index up the tree
        // until its value is less than that of its children.
        // !!! TODO !!!
        while (!isRoot(index) && isLess(this.queue.get(index), this.queue.get(parent(index)))) {
            swap(index, parent(index));
            index = parent(index);
        }
    }
}
