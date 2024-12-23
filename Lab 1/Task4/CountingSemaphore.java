package Task4;

public class CountingSemaphore {
    private int value;

    public CountingSemaphore(int n) {
        this.value = n;
    }

    public synchronized void signal() {
        value++;
        notify();
    }

    public synchronized void s_wait() throws InterruptedException {
        while (value <= 0) {
            wait();
        }
        value--;
    }
}