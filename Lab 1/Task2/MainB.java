package Task2;

public class MainB {
    private static int sharedInt = 0; // a shared variable, one is reading, one is writing
    private static boolean done = false; // flag to indicate that the incrementing thread has completed
    private static long completionTime = 0; // time when the incrementing thread completes

    public static class IncrementingThread implements Runnable {
        public void run() {
            for (int i = 0; i < 1000000; i++) { // increment the shared variable 1,000,000 times
                sharedInt++;
            }
            completionTime = System.nanoTime(); // record the time when the incrementing thread completes
            done = true; // set the flag to indicate that the incrementing thread has completed
        }
    }

    public static class PrintingThread implements Runnable {
        public void run() {
            while (!done) { // busy-wait until done is true
                System.out.println("Waiting for completion...");
            }
            long printTime = System.nanoTime(); // record the time when the printing thread starts
            System.out.println("Value of sharedInt: " + sharedInt);
            System.out.println("Delay: " + (printTime - completionTime) + " ns");
        }
    }

    public static void main(String[] args) {
        int warmupIterations = 10;
        int measurementIterations = 25;

        // Warm-up phase
        for (int i = 0; i < warmupIterations; i++) {
            Thread incrementingThread = new Thread(new MainC.IncrementingThread());
            Thread printingThread = new Thread(new MainC.PrintingThread());

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
            Thread incrementingThread = new Thread(new MainC.IncrementingThread());
            Thread printingThread = new Thread(new MainC.PrintingThread());

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