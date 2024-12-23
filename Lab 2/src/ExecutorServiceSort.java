import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class ExecutorServiceSort implements Sorter {
        private final int threads;

        public ExecutorServiceSort(int threads) {
                this.threads = threads;
        }

        @Override
        public void sort(int[] arr) {
                if (arr.length <= 1) {
                        return;
                }

                int[][] subarrays = splitArray(arr);
                ExecutorService executor = Executors.newFixedThreadPool(threads);
                List<Future<?>> futures = new ArrayList<>();

                for (int[] subarray : subarrays) {
                        futures.add(executor.submit(new Worker(subarray)));
                }

                for (Future<?> future : futures) {
                        try {
                                future.get(); // Wait for each task to complete
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }

                executor.shutdown();
                mergeMultipleSubarrays(arr, subarrays);
        }

        public int getThreads() {
                return threads;
        }

        private static class Worker implements Runnable {
                private final int[] arr;

                Worker(int[] arr) {
                        this.arr = arr;
                }

                @Override
                public void run() {
                        mergeSort(arr);
                }

                private void mergeSort(int[] arr) {
                        if (arr.length <= 1) return;

                        int mid = arr.length / 2;
                        int[] left = Arrays.copyOfRange(arr, 0, mid);
                        int[] right = Arrays.copyOfRange(arr, mid, arr.length);

                        mergeSort(left);
                        mergeSort(right);

                        int[] merged = merge(left, right);
                        System.arraycopy(merged, 0, arr, 0, arr.length);
                }

                private int[] merge(int[] left, int[] right) {
                        int[] result = new int[left.length + right.length];
                        int i = 0, j = 0, k = 0;
                        while (i < left.length && j < right.length) {
                                if (left[i] <= right[j]) result[k++] = left[i++];
                                else result[k++] = right[j++];
                        }
                        while (i < left.length) result[k++] = left[i++];
                        while (j < right.length) result[k++] = right[j++];
                        return result;
                }
        }

        private int[][] splitArray(int[] arr) {
                int chunkSize = arr.length / threads;
                int remainder = arr.length % threads;

                int[][] subarrays = new int[threads][];
                int startIndex = 0;

                for (int i = 0; i < threads; i++) {
                        int size = chunkSize + (i < remainder ? 1 : 0);
                        subarrays[i] = Arrays.copyOfRange(arr, startIndex, startIndex + size);
                        startIndex += size;
                }

                return subarrays;
        }

        private void mergeMultipleSubarrays(int[] original, int[][] arrays) {
                int[] merged = mergeRecursively(arrays, 0, arrays.length - 1);
                System.arraycopy(merged, 0, original, 0, original.length);
        }

        private int[] mergeRecursively(int[][] arrays, int left, int right) {
                if (left == right) return arrays[left];

                int mid = (left + right) / 2;
                int[] leftMerged = mergeRecursively(arrays, left, mid);
                int[] rightMerged = mergeRecursively(arrays, mid + 1, right);
                return merge(leftMerged, rightMerged);
        }

        private int[] merge(int[] left, int[] right) {
                int[] result = new int[left.length + right.length];
                int i = 0, j = 0, k = 0;
                while (i < left.length && j < right.length) {
                        if (left[i] <= right[j]) result[k++] = left[i++];
                        else result[k++] = right[j++];
                }
                while (i < left.length) result[k++] = left[i++];
                while (j < right.length) result[k++] = right[j++];
                return result;
        }
}