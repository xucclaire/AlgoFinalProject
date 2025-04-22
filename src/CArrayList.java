public class CArrayList<E> {
    private E[] array;
    private int size = 0;

    public CArrayList() {
        array = (E[]) new Object[10];
    }

    public CArrayList(int capacity) {
        array = (E[]) new Object[capacity];
    }

    public void add(E element) {
        add(size, element);
    }

    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds.");
        }
        if (size == array.length) {
            arrayCapacityChange(array.length * 2);
        }
        for (int i = size - 1; i >= index; i--) {
            array[i + 1] = array[i];
        }
        array[index] = element;
        size++;
    }
    public void addAll(CArrayList<E> otherList) {
        for (int i = 0; i < otherList.size(); i++) {
            this.add(otherList.get(i));
        }
    }
    private void arrayCapacityChange(int newCapacity) {
        E[] newArray = (E[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newArray[i] = array[i];
        }
        array = newArray;
    }

    public void set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds.");
        } else {
            array[index] = element;
        }
    }

    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds.");
        }
        return (E) array[index];
    }

    public void removeIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds.");
        }
        for (int i = index; i < size - 1; i++) {
            array[i] = array[i + 1];
        }
        array[size - 1] = null;
        size--;
        if (size > 0 && size <= array.length / 4) {
            arrayCapacityChange(array.length / 2);
        }
    }

    public boolean removeElement(E element) {
        for (int i = 0; i < size; i++) {
            if (array[i].equals(element)) {
                removeIndex(i);
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public E[] toArray() {
        E[] returnArray = (E[]) new Object[size];
        for (int i = 0; i < size; i++) {
            returnArray[i] = array[i];
        }
        return returnArray;
    }

    public boolean contains(E element) {
        for (int i = 0; i < size; i++) {
            if ((element == null && array[i] == null) || (element != null && element.equals(array[i]))) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        String result = "";
        for (int i = 0; i < size; i++) {
            result += array[i].toString();
            if (i < size - 1) {
                result += ", ";
            }
        }
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
