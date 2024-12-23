package Task5;

import java.util.concurrent.locks.Lock;

public class Philosopher implements Runnable {
    private final Lock leftChopstick;
    private final Lock rightChopstick;
    private final int id;

    public Philosopher(int id, Lock leftChopstick, Lock rightChopstick) {
        this.id = id;
        this.leftChopstick = leftChopstick;
        this.rightChopstick = rightChopstick;
    }

    @Override
    public void run() {
        try {
            while (true) {
                think();
                pickUpChopsticks();
                eat();
                putDownChopsticks();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " is thinking.");
        Thread.sleep((long) (Math.random() * 1000));
    }

    private void pickUpChopsticks() {
        if (id % 2 == 0) {
            leftChopstick.lock();
            rightChopstick.lock();
        } else {
            rightChopstick.lock();
            leftChopstick.lock();
        }
        System.out.println("Philosopher " + id + " picked up chopsticks.");
    }

    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + id + " is eating.");
        Thread.sleep((long) (Math.random() * 1000));
    }

    private void putDownChopsticks() {
        leftChopstick.unlock();
        rightChopstick.unlock();
        System.out.println("Philosopher " + id + " put down chopsticks.");
    }
}
