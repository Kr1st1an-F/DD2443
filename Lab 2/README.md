# Java Parallel Sorting

## Lab 2 Skeleton
* ```REPORT.pdf```: Report template.
* ```MeasureMain.java```: The main file of a program doing performance measurements.
  - ```java MeasureMain <SorterName> <Threads> <Array Size> <Warm-up rounds> <Measurement rounds> <PRNG seed>```
  - During the warm-up rounds, the program also validate that the sorting algorithms work correctly.
* ```Auxiliary.java```: Contains helper functions for taking measurements and doing sanity checks.
* ```Sorter.java```: An interface for the sorting algorithms. Classes that implements Sorter are:
  - ```SequentialSort.java```: Sequential sorting algorithm.
  - ```ExecutorServiceSort.java```: Implementation using ExecutorService.
  - ```ForkJoinPoolSort.java```: Implementation using ForkJoinPool.
  - ```ParallelStreamSort.java```: Implementation using ParallelStream.
  - ```ThreadSort.java```: Implementation using Thread’s start()/join().
  - ```JavaSort.java```: Implementation using Java’s native library.
* ```test_JavaSort.sh```: Bash script testing JavaSort.java.

The sorting algorithms must be packaged as a class implementing the following interface (as in the skeleton code):
```
public interface Sorter {
        // Sort 'arr'.
        public void sort(int[] arr);
        // Number of threads used by the sorter.
        public int getThreads();
}
```

## Sanity check
When implementing the sequential sort or the parallel sorts, remember to sanity check your solutions.

* Does the implementation actually sort the array?
  - Ensure that the array is unsorted before applying the sorting algorithm.
  - Ensure that the array is sorted after applying the sorting algorithm.
  - Ensure that all of the sorter’s threads have terminated.
* Are you measuring execution time in the right way?
* When you increase the number of threads for the parallel sorts, does the execution time decrease as predicted? (It must decrease!)
* Do you have enough available cores? Check with:
  - ```System.err.println("Available Processors: " + Runtime.getRuntime().availableProcessors());```
To get reproducible results, remember to warm-up the JVM before taking measurements.

## Tasks
When implementing the parallel sorting algorithms, you may resort to sequentially sorting (sub-)arrays if further parallelization 
does not improve performance or makes sense. For instance, creating a thread/task to sort an array of size <16 is probably unecessary. 
Use your own judgement.

**When implementing the following sorting algorithms, sanity check them!**

### 1. Sequential Sort
Implement a sequential sort of your algorithm of choice, mergesort or quicksort, sorting integer arrays.

### 2. Amdahl’s law
Amdahl’s law cannot be applied naively to the algorithm.

* Formulate your version of Amdahl’s law for the algorithms for 2, 4, 8, and 16 threads. It does not have to be exact, but do your best.
* Motivate why your version may be better.
* Plot the speedup given by your solution with 2, 4, 8, and 16 threads, and p (parallelizable part) equal to 0.2, 0.4, 0.6, and 0.8.

### 3. ExecutorSerice
Implement a parallelized sort using Java’s ```ExecutorService``` (```Executors.newFixedThreadPool(n)```).

### 4. ForkJoinPool and RecursiveAction
Implement a parallelized sort using Java’s ```ForkJoinPool``` and ```RecursiveAction```.

### 5. ParallelStream and Lambda Functions
Implement a parallelized sort using Java’s ```ParallelStream``` and Lambda functions.

### 6. Thread start() and join()
Implement a parallelized sort using the Thread class (using ```start()``` and ```join()```).

### 7. Performance measurements
Instrument (setting up measurements) your implementations from task 1, 3, 4, 5, 6 to measure the execution time. 
Test your instrumentation locally. Ensure you get consistent outputs and that the sorting actually work. 
If you have sanity checked your solutions, this should have been done.

Measure the execution time of your parallelized implementations on PDC. Sort a suitably large integer array, for example, 10,000,000 elements

Use 2, 4, 8, 16, 32, 48, 64 and 96 threads. Also, measure the execution time of the sequential implementation as baseline, 
and the Java library implementation (```JavaSort.java```). Plot the speedup of the implementations, normalizing using your sequential implementation’s execution time.

Explain the results/plots. For instance, 
- Are the performance gains/drops between different numbers of threads what you expected? 
- What implementation ran the fastest/slowest? Why?
- What method was the easiest to implement?
- What method do you prefer?

