package Task1;

public class MainB {
    private static int counter = 0;
    private static final Object lock = new Object();

    public static class Incrementer implements Runnable {
        private int increments;

        public Incrementer(int increments) {
            this.increments = increments;
        }

        public void run() {
            for (int i = 0; i < increments; i++) {
                incrementCounter();
            }
        }

        private void incrementCounter() {
            synchronized (lock) {
                counter++;
            }
        }
    }

    public static void main(String[] args) {
        int n = 4;
        int increments = 1000000;
        Thread[] threads = new Thread[n];

        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(new Incrementer(increments));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Counter value: " + counter);
    }
}