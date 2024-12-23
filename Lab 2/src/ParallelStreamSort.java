import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class ParallelStreamSort implements Sorter {

        public final int threads;
        private final ForkJoinPool pool;

        public ParallelStreamSort(int threads) {
                this.pool = new ForkJoinPool(threads);
                this.threads = threads;
        }

        @Override
        public void sort(int[] arr) {
                try {
                        pool.submit(() -> {
                                parallelQuickSort(arr, 0, arr.length - 1);
                        }).get();
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        private void parallelQuickSort(int[] arr, int low, int high) {
                if (low < high) {
                        int pivotIndex = SequentialSort.partition(arr, low, high);

                        try {
                                Arrays.stream(new int[] {0, 1}).parallel().forEach(i -> {
                                        if (i == 0) {
                                                parallelQuickSort(arr, low, pivotIndex - 1);
                                        } else {
                                                parallelQuickSort(arr, pivotIndex + 1, high);
                                        }
                                });
                        } catch (Exception e) {
                                throw new RuntimeException(e);
                        }
                }
        }

        public int getThreads() {
                return threads;
        }
}