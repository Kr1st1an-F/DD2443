package Task2;

public class MainC {
    private static int sharedInt = 0;
    private static boolean done = false;
    private static long completionTime = 0;
    private static final Object lock = new Object(); // used for synchronization

    public static class IncrementingThread implements Runnable {
        public void run() {
            for (int i = 0; i < 1000000; i++) {
                sharedInt++;
            }
            synchronized (lock) {
                completionTime = System.nanoTime();
                done = true;
                lock.notify();
            }
        }
    }

    public static class PrintingThread implements Runnable {
        public void run() {
            synchronized (lock) { // wait for the incrementing thread to finish
                while (!done) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                long printTime = System.nanoTime();
                System.out.println("Value of sharedInt: " + sharedInt);
                System.out.println("Delay: " + (printTime - completionTime) + " ns");
            }
        }
    }

    public static void main(String[] args) {
        int warmupIterations = 10;
        int measurementIterations = 25;

        // Warm-up phase
        for (int i = 0; i < warmupIterations; i++) {
            Thread incrementingThread = new Thread(new IncrementingThread());
            Thread printingThread = new Thread(new PrintingThread());

            incrementingThread.start();
            printingThread.start();

            try {
                incrementingThread.join();
                printingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Measurement phase
        for (int i = 0; i < measurementIterations; i++) {
            Thread incrementingThread = new Thread(new IncrementingThread());
            Thread printingThread = new Thread(new PrintingThread());

            incrementingThread.start();
            printingThread.start();

            try {
                incrementingThread.join();
                printingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}