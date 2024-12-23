package Task4;

public class Main {

    public static class SemaphoreTest implements Runnable {
        private final CountingSemaphore semaphore;
        private final int id;

        public SemaphoreTest(CountingSemaphore semaphore, int id) {
            this.semaphore = semaphore;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                System.out.println("Thread " + id + " attempting to acquire semaphore.");
                semaphore.s_wait();
                System.out.println("Thread " + id + " acquired semaphore.");
                // Simulate some work with the semaphore
                Thread.sleep(1000);
                semaphore.signal();
                System.out.println("Thread " + id + " released semaphore.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        int initialCounterValue = 3; // Initial semaphore value
        int numberOfThreads = 5; // Number of threads to test

        CountingSemaphore semaphore = new CountingSemaphore(initialCounterValue);

        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(new SemaphoreTest(semaphore, i));
            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("All threads have finished execution.");
    }
}
