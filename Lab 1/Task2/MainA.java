package Task2;

public class MainA {
    private static int sharedInt = 0;

    public static class IncrementingThread implements Runnable {
        public void run() {
            for (int i = 0; i < 1000000; i++) {
                sharedInt++;
            }
        }
    }

    public static class PrintingThread implements Runnable {
        public void run() {
            System.out.println("Value of sharedInt: " + sharedInt);
        }
    }

    public static void main(String[] args) {
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
