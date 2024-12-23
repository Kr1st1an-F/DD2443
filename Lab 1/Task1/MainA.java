package Task1;

import java.lang.Object;
import java.lang.Thread;

public class MainA {
    private static int counter = 0; // shared variable between threads

    public static class Incrementer implements Runnable {
        private int increments; // Store the number of increments to be performed by each thread

        // Constructor
        public Incrementer(int increments) {
            this.increments = increments;
        }

        // Run method for the thread
        public void run() {
            for (int i = 0; i < increments; i++) { // Increment the counter by increments
                incrementCounter();
            }
        }

        // synchronized method to increment the counter
        private synchronized void incrementCounter() {
            counter++;
        }
    }

    public static void main(String[] args) {
        int n = 4;
        int increments = 1000000;
        Thread[] threads = new Thread[n]; // Array to store the threads

        // Create and start the threads
        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(new Incrementer(increments));
            threads[i].start();
        }

        for (Thread thread : threads) { // Wait for all threads to finish
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Counter value: " + counter);
    }
}
