import java.util.Arrays;

public class ThreadSort implements Sorter {
        private final int threads;

        public ThreadSort(int threads) {
                this.threads = threads;
        }

        @Override
        public void sort(int[] arr) {
                if (threads <= 1) {
                        Arrays.sort(arr);
                } else {
                        parallelSort(arr, threads);
                }
        }

        private void parallelSort(int[] arr, int threadCount) {
                int mid = arr.length / 2;
                int[] left = Arrays.copyOfRange(arr, 0, mid);
                int[] right = Arrays.copyOfRange(arr, mid, arr.length);

                Thread leftSorter = new Thread(() -> parallelSort(left, threadCount / 2));
                Thread rightSorter = new Thread(() -> parallelSort(right, threadCount / 2));

                leftSorter.start();
                rightSorter.start();

                try {
                        leftSorter.join();
                        rightSorter.join();
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }

                merge(arr, left, right);
        }

        private void merge(int[] result, int[] left, int[] right) {
                int i = 0, j = 0, k = 0;
                while (i < left.length && j < right.length) {
                        if (left[i] <= right[j]) {
                                result[k++] = left[i++];
                        } else {
                                result[k++] = right[j++];
                        }
                }
                while (i < left.length) {
                        result[k++] = left[i++];
                }
                while (j < right.length) {
                        result[k++] = right[j++];
                }
        }

        @Override
        public int getThreads() {
                return threads;
        }
}