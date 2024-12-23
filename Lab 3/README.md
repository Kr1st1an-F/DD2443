# Linearizability of Lock-free Skiplists
The aim of this lab is to explore a complex lock-free concurrent data structure. 
You will assess its performance, measure its scalability, and learn methods to test its correctness.

## Lab 3 Skeleton
* ```Distribution.java```
  - ```Distribution.Uniform(seed, min, max)```: Uniform distribution over [min, max).
  - ```Distribution.Discrete(seed, prob)```: Discrete distribution over [0, prob.length), where the probability of i is prob[i]/total where total is the sum of prob.
  - ```Distribution.Normal(seed, sample, min, max)```: Approximation of the normal distribution using the sum of uniform distributions [min, max). Sample is the number of random variables. This is based on the central limit theorem.
* ```Experiment.java```: Small example of how an experimental setup may look like.
* ```LockFreeSet.java```: Interface to a LockFreeSet. Add/Remove/Contains has threadId for assisting with the local log sampling methods.
  - ```boolean add(threadId, x)```: Add x to set.
  - ```boolean remove(threadId, x)```: Removes x from set.
  - ```boolean contains(threadId, x)```: Check x is in set.
  - ```Log.Entry[] getLog()```: Get the linearization points as a log.
  - ```void reset()```: Clear the log and the set.
* ```LockFreeSkipList.java```: Our default implementation of LockFreeSet (does implement the log).
* ```Log.java```: Contains Log related methods and classes.
    - ```Log.Entry```: Class for keeping Log information
    - ```boolean validate(Log.Entry[] log)```: Class method for validating the sequential consistency of a log.
* ```Main.java```: Program for testing the LockFreeSkipList.

You are free to modify the provided code!

## Tasks

### 1. Measuring execution time
#### 1.1. Measurement program

Firstly, look at all of the provided Java source code, understand how they work.

We have provided you with a simple program for testing ```Main.java```. The arguments of the program is the following:
```
# <T>  Number of threads to use.
# <S>  Default, Locked, LocalLog, GlobalLog version of LockFreeSkipList.
# <D>  Normal or Uniform of sampling.
# <V>  Max value to sample (samples 0-MaxValue).
# <A>:<R>:<C>  Distribution of adds, removes, and contains.
# <O>  Number of operations to execute per thread.
# <W>  Measurement rounds to warm up the JVM.
# <M>  Number of measurements for the final statistics.
java Main <T> <S> <D> <V> <A>:<R>:<C> <O> <W> <M>
```
Test the program locally with 1, 2, 4, and 8 threads with values sampled using Normal and Uniform distribution. 
Each thread should invoke 100,000 operations each using the following mixtures of operations:

10% add, 10% remove, and 80% contains, corresponds to ```1:1:8```.

50% add and 50% remove, corresponds to ```1:1:0```.

#### 1.2 Dardel experiments
Run the program on Dardel with 1, 2, 4, 8, 16, 32, and 48 threads, with each thread performing 1 000 000 operations as follows:

A. Values sampled from the **_uniform distribution_** from 0 to 100 000.
  - 1. 10% add, 10% remove, and 80% contains.
  - 2. 50% add and 50% remove.
B. Values sampled from the **_normal distribution_** from 0 to 100 000.
  - 1. 10% add, 10% remove and 80% contains.
  - 2. 50% add and 50% remove.

For each case, A.1, A.2, B.1, and B.2, plot the average execution time. Explain your observations. Do they make sense?

In your plots, the y-axis should be the execution time and the x-axis should be the number of threads.

### 2. Identify and validate linearization points
We hypothesize that our skiplist implementation is linearizable and aim to validate this claim. 
To achieve this, we’ll use ```System.nanoTime()``` to pinpoint when the linearization point occurs. 
This step requires determining how to measure these points accurately and logging them by integrating ```System.nanoTime()``` calls at strategic positions in the code.

However, relying on ```System.nanoTime()``` isn’t error-free. Since the measurement isn’t atomic, 
it’s susceptible to inaccuracies, as another thread may interleave the time sampling and the linearization point. 
Such interleaving can lead to inconsistencies in the time-ordered trace, causing it to deviate from the expected sequential behavior. For instance, 
a ```contains(x)``` operation might erroneously return true if the last action on ```x``` was its removal. 
The subsequent parts of the lab is about refining our technique to check the skiplist’s linearizability and quantify any measurement errors.

#### 2.1 Identify linearization points
Consult HSLS, chapter 14, to identify the linearization points of the methods in the LockFreeSkipList class. 
Describe, in your own words, the locations of these points and discuss possible techniques to capture them. In particular, 
look at the linearization point of ```remove``` when the ```find()``` returns true, but ```iMarkedIt``` is false.

#### 2.2 Develop a validation method
Look at the ```LogEntry``` class for capturing a linearization point from the LockFreeSkipList. 
For a given linearization point, ```Log.Entry``` should record the method’s name, the arguments, the return value, and the linearization time (```System.nanoTime()```).

Implement the validation method (```Log.validate```) that takes an array (```Log.Entry[]```), and returns the number of discrepancies in the log. 
We suggest you replay the log events on a data structure such as HashSet. 
Measure the correctness of the log by counting the number of discrepancies between the HashSet’s and the Log’s return values. 
For instance, if ```add(x)``` returns true in ```HashSet```, but the return value is false in your ```Log.Entry```, you have a discrepancy. 
If the discrepancy count is 0, then the log is sequentially consistant.

#### 2.3. Locked time sampling
It is now time to capture the linearization points. Use a lock around the linearization point and the time sampling, for instance:
```
lock.acquire();
(My Linearization point);
timestamp = System.nanoTime();
lock.release();
```
This lock should prevent other threads from time sampling between the linearization point and the time sampling.

Determine the performance and accuracy of your locked time sampling using the experimental cases from Task 1.1 (measuring locally) 
and the validation method from Task 2.2. How significantly does the lock-protected implementation lag behind? 
What is the accuracy of your sampling implementation?

If your time sampling and linearization point are accurate, the discrepancy count should ideally be 0, 
but to pass this task we require it to be less than 100. If you have discrepancies, 
explain from where these may come from.

#### 2.4. Lock-free time sampling with local log
Opting for a less invasive method, we aim to log time samples without a global lock. 
However, this approach will introduce errors, given that the interval between linearization points and time sampling isn’t protected. 
The next phase of the lab focuses on understanding the extent of such errors.

Replicate the experiments from Task 2.3, but this time without the global lock. 
Use per-thread logs, saving samples into a sufficiently large thread-local array or linked list. 
Note that time sampling and the actual logging can be performed separately. Once an experiment concludes, 
merge the local logs into a global one, and sort the entries by timestamp and make corrections to the log if necessary. 
Assume that ```System.nanoTime()``` offers a granularity that prevents overlapping time samples.

#### 2.5. Lock-free Time Sampling with Global Log
In this task, record your log entries to a global lock-free log, and again replicate the experiments from Task 2.3. 
We recommend that you implement your global log using a lock-free queue (or similar data structure) from ```java.util.concurrent```. 
Implement the log carefully to introduce as little extra synchronization as possible.
Use your knowledge of the Java Memory Model and the happens-before relation to determine what new execution constraints, 
if any, are introduced by your new logging method.

#### 2.6. Dardel experiments
On Dardel, using the same experimental cases as in Task 1.2, measure both the execution time and the accuracy of the logs of the sampling implementations 
from Task 2.4 and Task 2.5.

For each case, plot the execution time and the accuracy.
