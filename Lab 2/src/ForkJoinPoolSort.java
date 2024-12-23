import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinPoolSort implements Sorter {
        private final int threads;

        public ForkJoinPoolSort(int threads) {
                this.threads = threads;
        }

        public void sort(int[] arr) {
                ForkJoinPool pool = new ForkJoinPool(threads);
                pool.invoke(new Worker(arr, 0, arr.length - 1));
                pool.shutdown();
        }

        public int getThreads() {
                return threads;
        }

        private static class Worker extends RecursiveAction {
                private final int[] arr;
                private final int low;
                private final int high;
                private static final int THRESHOLD = 1000;

                public Worker(int[] arr, int low, int high) {
                        this.arr = arr;
                        this.low = low;
                        this.high = high;
                }

                @Override
                protected void compute() {
                        if (high - low < THRESHOLD) {
                                sequentialSort(arr, low, high);
                        } else {
                                int pivotIndex = partition(arr, low, high);
                                invokeAll(new Worker(arr, low, pivotIndex - 1), new Worker(arr, pivotIndex + 1, high));
                        }
                }

                private void sequentialSort(int[] arr, int low, int high) {
                        if (low < high) {
                                int pivotIndex = partition(arr, low, high);
                                sequentialSort(arr, low, pivotIndex - 1);
                                sequentialSort(arr, pivotIndex + 1, high);
                        }
                }

                private int partition(int[] arr, int low, int high) {
                        int pivot = arr[high];
                        int i = low - 1;
                        for (int j = low; j < high; j++) {
                                if (arr[j] <= pivot) {
                                        i++;
                                        swap(arr, i, j);
                                }
                        }
                        swap(arr, i + 1, high);
                        return i + 1;
                }

                private void swap(int[] arr, int i, int j) {
                        int temp = arr[i];
                        arr[i] = arr[j];
                        arr[j] = temp;
                }
        }
}