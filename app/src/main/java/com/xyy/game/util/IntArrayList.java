package com.xyy.game.util;


public class IntArrayList {
    /**
     * The minimum amount by which the capacity of an ArrayList will increase.
     * This tuning parameter controls a time-space tradeoff. This value (12)
     * gives empirically good results and is arguably consistent with the
     * RI's specified default initial capacity of 10: instead of 10, we start
     * with 0 (sans allocation) and jump to 12.
     */
    private static final int MIN_CAPACITY_INCREMENT = 12;

    /**
     * The number of elements in this list.
     */
    int size;

    /**
     * The elements in this list, followed by nulls.
     */
    transient int[] array;

    /**
     * Constructs a new instance of {@code ArrayList} with the specified
     * initial capacity.
     *
     * @param capacity
     *            the initial capacity of this {@code ArrayList}.
     */
    public IntArrayList(int capacity) {
        array = new int[capacity];
    }

    /**
     * Constructs a new {@code ArrayList} instance with zero initial capacity.
     */
    public IntArrayList() {
        array = new int[0];
    }

    /**
     * Adds the specified object at the end of this {@code ArrayList}.
     *
     * @param object
     *            the object to add.
     * @return always true
     */
    public boolean add(int object) {
        int[] a = array;
        int s = size;
        if (s == a.length) {
            int[] newArray = new int[s +
                    (s < (MIN_CAPACITY_INCREMENT / 2) ?
                            MIN_CAPACITY_INCREMENT : s >> 1)];
            System.arraycopy(a, 0, newArray, 0, s);
            array = a = newArray;
        }
        a[s] = object;
        size = s + 1;
        return true;
    }

    /**
     * Adds the objects in the specified collection to this {@code ArrayList}.
     *
     * @param collection
     *            the collection of objects.
     */
    public void addAll(IntArrayList collection) {
        int[] newPart = collection.getArray();
        int newPartSize = collection.size();
        int[] a = array;
        int s = size;
        int newSize = s + newPartSize; // If add overflows, arraycopy will fail
        if (newSize > a.length) {
            int newCapacity = (newSize-1) +
                    ((newSize-1) < (MIN_CAPACITY_INCREMENT / 2) ?
                            MIN_CAPACITY_INCREMENT : (newSize-1) >> 1);  // ~33% growth room
            int[] newArray = new int[newCapacity];
            System.arraycopy(a, 0, newArray, 0, s);
            array = a = newArray;
        }
        System.arraycopy(newPart, 0, a, s, newPartSize);
        size = newSize;
    }

    /**
     * Removes the object at the specified location from this list.
     *
     * @param index
     *            the index of the object to remove.
     * @return the removed object.
     */
    public int remove(int index) {
        int[] a = array;
        int s = size;
        int result = a[index];
        System.arraycopy(a, index + 1, a, index, --s - index);
        size = s;
        return result;
    }

    /**
     * Removes the object at the last of this list.
     *
     * @return the removed object.
     */
    public int removeLast(){
        return array[--size];
    }

    /**
     * Removes all elements from this {@code ArrayList}, leaving it empty.
     *
     * @see #isEmpty
     * @see #size
     */
    public void clear() {
            size = 0;
    }

    /**
     * return the element in the index of ArrayList
     * @param index the index of element
     * @return the element in index
     */
    public int get(int index) {
        return array[index];
    }

    /**
     * Returns the number of elements in this {@code ArrayList}.
     *
     * @return the number of elements in this {@code ArrayList}.
     */
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private int[] getArray() {
        return array;
    }

}

