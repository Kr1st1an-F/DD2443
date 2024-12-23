familiarize yourself with the Java API to determine the number of available processors or threads on the system you're using, 
and develop a pattern for testing concurrent programs.

A. Observe the unpredictable behavior caused by race conditions.
  * Write a program spawing n threads. Each thread should increment a shared volatile integer 1,000,000 times without synchronization.
  * After all threads have finished executing (use join()), and print the final value at the end.
  * Run the program locally with 4 threads. What results do you expect?

B. Understand the effects of the synchronized keyword.
  * Modify the program from 2.1. Use the synchronized keyword when incrementing the shared integer.
  * Run the program locally with 4 threads. What results do you expect?

C. Explore the overhead introduced by synchronization and understand the performance implications across different systems.
  * Implement a method: ```long run_experiment(int n).``` This method should:
    - Spawn, then join, n threads which increments a shared integer using synchronized (as in exercise 2B).
    - Returned the elapsed time between starting the first thread, and joining the last using ```System.nanoTime()```.
  * Create a program for measuring performance:
    - Begin a "warm-up" phase: Call ```run_experiments(n)``` for X iterations. This allows the JVM to optimize and cache as necessary.
    - Follow with the "measurement" phase: Call ```run_experiments(n)``` for Y iterations and and print out the elapsed time for each iteration.
    - Decide on values for X and Y with your own judgement.
  * Run the measurement program locally for increasing values of n (1,2,4, ..., 64). Record the average execution time and the standard deviation for each value of n.
    - Plot the results. If the standard deviation seems high, adjust the values of X and Y for better consistency.
  * Execute the measurement program on PDC for the same range of threads.
  * Analyze your findings:
    - Compare the results from your local machine and PDC.
    - Reflect on the differences. What can you infer about the systems, the overhead of synchronization, and the nature of concurrent programming?
 

### 2. Guarded blocks using wait()/notify()
In this task, we explore message passing using a while-loop, wait() and notify().

A. Implement asynchronous sender-receiver
  * Write a program with two threads and a shared integer, ```sharedInt```:
    - **incrementingThread**: Increments ```sharedInt``` 1,000,000 times (or more).
    - **printingThread**: Prints the value of ```sharedInt```.
  * Start printingThread without ensuring that incrementingThread has finished its task.
    Since there is no synchronization, the printingThread may print some other value other than 0 or 1,000,000.
    Execute the program multiple times to observe that this can happen.

B. Implement busy-waiting receiver
  * Modify the previous program, introduce a new shared boolean, ```done```, initialized to false.
    - **incrementingThread**: Set ```done``` to ```true``` when it has finished incrementing.
    - **printingThread**: Use a while-loop to continusly check if ```done``` is ```true```, Once true, print the value of ```sharedInt```.
  * Run and observe the behavior of the program.

C. Implement a waiting with guarded block
  * Further modify your previous program. Replace the busy-waiting with ```synchronized```, ```wait()``` and ```notify()```.
    - **incrementingThread**: After finishing the increments, use a synchronized block to notify the ```printingThread``` of completion.
    - **printingThread**: Instead of busy-waiting, use a synchronized block to wait for a notification from the ```incrementingThread``` before printing the value.
  * Java threads calling ```wait()``` may [wake up spuriously](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/lang/Object.html#wait(long,int)) To solve this task,
    you need additional control code to handle spurious wake ups.

D. Explore the effects of guarded block on performance.
  * Modify the programs of 3B and 3C to measure the delay between completion of increments by the ```incrementingThread```, and ```printingThreads``` receiving the notification. Use System.nanoTime().
  * Run the measuring program locally. Remember to warm-up JVM and do repeated measurements.
  * Analyze the impact of guarded blocks on performance in contrast to the busy-waiting approach.
 

### 3. Producer-Consumer Buffer using Condition Variables
In this task, you will gain practical experience in utilizing [ReentrantLock](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html).
and [Condition](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/locks/Condition.html) variables. 
These constructs share similarities with ```synchronized()```, ```notify()```, and ```wait()```, but they allow for multiple waiting sets and offer enhanced versatility.

A. Implement a producer-consumer buffer class with limited capacity N.
  * The buffer should be implemented with the help of ```ReentrantLock``` and ```Condition``` classes.
  * You are free to be inspired by the code in the documentation of [Condition Variables](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/locks/Condition.html). However,
    note that it can not be copied outright.
  * The order of consumption is either LIFO or FIFO.
  * You should implement the following methods: 
    - ```Buffer(int N)```: Constructor, initializing a buffer of size N.
    - ```void add(int i)```: Insert an integer into the buffer. If the buffer is full, wait until space becomes available. Throws an exception only if the buffer is closed.
    - ```int remove()```: Removes and returns an integer from the buffer. If the buffer is empty, wait until an item is available. Throws an exception only if the buffer is closed and empty.
    - ```void close()```: Closes the buffer. No more items can be pushed after closure, but items can be popped until the buffer is empty. Throws an exception if the buffer is already closed.
  * The methods should not throw any exception for cases not specified above.

B. Write a program using the buffer class.
  * The program should have two threads
    - **producerThread**: Adds 1,000,000 integers into the buffer. After all integers have been added, close the buffer and terminate the thread.
    - **consumerThread**: Continuously remove integers from the buffer and prints them. The consumer should handle any exceptions thrown due to the buffer being closed and should terminate gracefully upon buffer closure.
  * All synchronization code of the producer and consumer should be inside the buffer's methods.
    There should be no lock, synchronization, await, signal, etc., visible within the Producer's or Consumer's code.
    The main thread is of course allowed to start() and join() the producer and consumer.
 
### 4. Counting Semaphore
In this task, we assess your attention to detail, as programming concurrent objects can prove deceptively intricate.

A (counting) semaphore is a shared integer n with two atomic operations ```signal()``` and ```s_wait()``` (to avoid confusing with the Java wait() primitive). 
Semaphores are used to govern access to some shared resource. A positive value n of the semaphore indicates that there are n amounts of resource available. 
Similarly, a negative value of the semaphore indicates the number of threads waiting for a resource. When n is zero, then no resources are available, 
and no threads are waiting.

A. Implement the counting semaphore using ```synchronized()```, ```wait()```, and ```notify()``` in Java, or using Lock and Condition. 
The counting semaphore should have the following methods:
  * ```signal()```: Increments the value of the semaphore by 1 to indicate that a resource has been made available. If the value was negative prior to the increment, wake one thread waiting on the semaphore.
  * ```s_wait()```: Decrements the value of the semaphore by 1. If the new value is negative, then the thread should wait on the semaphore.
  * Note that your solution should be able to handle spurious wakeups.

B. Create a program allowing you to test your semaphore with various counter values and thread counts.
  * Test the your semaphore thoroughly and document any interesting semaphore mistakes you have made (if any), excluding compilation errors.
  * Ensure the number of threads that acquire the semaphore does not exceed the initial counter value n.
  * Ensure that there is no deadlock where there should be no deadlock, and deadlocks where there should be a deadlock.
 

### 5. Dining Philosophers
In the classic Dining Philosophers problem, philosophers spend their lives thinking and eating. 
They sit around a circular dining table with one chopstick between each pair of adjacent philosophers. 
Each philosopher must pick up two chopsticks before eating and place them down after eating. While thinking, 
a philosopher does not hold onto any chopsticks.

Your task is to model this problem and come up with a solution that ensures that every philosopher 
gets to eat without leading to a deadlock situation or causing any philosopher to starve.

A. Model the Dining Philosophers
  * Using your primitives you learned about from earlier exercises, implement the Dining Philosophers scenario with an arbitrary number of philosophers.
  * Test your model with five philosophers.
  * Your simulation should deadlock. Observer and record the situations under which a deadlock is reached.
  * Test with more philosphers, does it deadlock faster?

B. What debugging tools does the Java environment offer that might help us debug this deadlock?
  * Find and use a tool for Java that automatically finds deadlocks and their cause.

C. Implement a solution to the Dining Philosophers problem.
  * Your solution should ensure:
    - **Deadlock-free**. There should be no situation where the system becomes stuck.
    - **Starvation-free**. In any infinitely long run, each philosopher should get to eat infinitely often. I.e., there should be no scenario where an philosopher has to wait infinitely long.
  * The only shared variables of a philospher is the chopsticks they share with their neighbor.
  * Test your solution.
  * Argue carefully why your solution meets these requirements.
