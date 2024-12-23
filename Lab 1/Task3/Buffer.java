package Task3;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
    private final LinkedList<Integer> buffer; // Store the elements in the buffer
    private final int capacity; // Maximum number of elements the buffer can hold
    private final ReentrantLock lock; // Lock to protect the buffer
    private final Condition notFull; // Signalled when the buffer is not full
    private final Condition notEmpty; // Signalled when the buffer is not empty
    private boolean isClosed; // Flag to indicate that the buffer is closed

    // Constructor
    public Buffer(int size) {
        this.capacity = size;
        this.buffer = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
        this.isClosed = false;
    }

    public void add(int i) throws InterruptedException {
        lock.lock();
        try { // Try to add an element to the buffer
            while (buffer.size() == capacity && !isClosed) { // Wait until the buffer is not fulL
                notFull.await(); // Release the lock and wait
            }

            if (isClosed) {
                throw new IllegalStateException("Buffer is closed");
            }
            buffer.add(i); // Add the element to the buffer
            notEmpty.signal();
        } finally {
            lock.unlock(); // Release the lock
        }
    }

    public int remove() throws InterruptedException {
        lock.lock();
        try { // Try to remove an element from the buffer
            while (buffer.isEmpty()) { // Wait until the buffer is not empty
                if (isClosed) {
                    throw new IllegalStateException("Buffer is closed and empty");
                }
                notEmpty.await();
            }
            int value = buffer.removeFirst(); // Change to removeLast() for LIFO
            notFull.signal(); // Signal to producers that the buffer is not full
            return value;
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        lock.lock();
        try { // Close the buffer
            if (isClosed) { // Check if the buffer is already closed
                throw new IllegalStateException("Buffer is already closed");
            }
            isClosed = true;
            notFull.signalAll();
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }
}