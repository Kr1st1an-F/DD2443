package Task5;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        int numberOfPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
        Lock[] chopsticks = new ReentrantLock[numberOfPhilosophers];

        for (int i = 0; i < numberOfPhilosophers; i++) {
            chopsticks[i] = new ReentrantLock();
        }

        for (int i = 0; i < numberOfPhilosophers; i++) {
            Lock leftChopstick = chopsticks[i];
            Lock rightChopstick = chopsticks[(i + 1) % numberOfPhilosophers];
            philosophers[i] = new Philosopher(i, leftChopstick, rightChopstick);
            new Thread(philosophers[i]).start();
        }
    }
}
