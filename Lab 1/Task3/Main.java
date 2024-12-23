package Task3;

public class Main {
    public static class Producer implements Runnable {
        private final Buffer buffer; // a reference to the shared buffer

        public Producer(Buffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 1000000; i++) {
                    buffer.add(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                buffer.close();
            }
        }
    }

    public static class Consumer implements Runnable {
        private final Buffer buffer;

        public Consumer(Buffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    int value = buffer.remove(); // Constantly consume from the buffer
                    System.out.println("Consumed: " + value);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IllegalStateException e) {
                System.out.println("Buffer closed, consumer terminating.");
            }
        }
    }

    public static void main(String[] args) {
        Buffer buffer = new Buffer(100); // Buffer with capacity 100

        Thread producerThread = new Thread(new Producer(buffer));
        Thread consumerThread = new Thread(new Consumer(buffer));

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
