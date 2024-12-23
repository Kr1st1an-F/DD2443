package Task1;

import java.io.FileWriter;
import java.util.ArrayList;

public class MainC {
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

    long run_experiments(int n) {
        long startTime = System.nanoTime();
        // TODO
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
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public static void main(String [] args) {
        MainC main = new MainC();
        int[] counts = {1, 2, 4, 8, 16, 32, 64};
        int warmup = 10;
        int measurements = 100;

        try(FileWriter csv = new FileWriter("resultsLocal.csv")) {
            csv.append("n,average,stdDev\n");

            for (int n : counts) {
                csv.append(n + ",");
                ArrayList<Long> times = new ArrayList<>();

                //Warmup
                for (int i = 0; i < warmup; i++) {
                    main.run_experiments(n);
                }

                for (int i = 0; i < measurements; i++) {
                    long time = main.run_experiments(n);
                    times.add(time);
                    System.out.println("n = " + n + ", Iteration " + (i + 1) + " time taken: " + time + " ns");
                }

                double average = times.stream().mapToLong(val -> val).average().orElse(0.0);
                double stdDev = Math.sqrt(times.stream().mapToDouble(val -> Math.pow(val - average, 2)).sum() / times.size());
                System.out.println("n = " + n + ", Average time: " + average + " ns, Standard deviation: " + stdDev + " ns");
                csv.append(average + "," + stdDev + "\n");
            }

            System.out.println("Counter value: " + counter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
