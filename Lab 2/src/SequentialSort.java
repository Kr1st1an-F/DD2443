public class SequentialSort implements Sorter {

        public SequentialSort() {
        }

        public void sort(int[] arr) {
                quicksort(arr, 0, arr.length - 1);
        }

        private void quicksort(int[] arr, int low, int high) {
                if (low < high) { // base case
                        int pivotIndex = partition(arr, low, high); // partitioning step
                        quicksort(arr, low, pivotIndex - 1); // recursive call on the left partition
                        quicksort(arr, pivotIndex + 1, high); // recursive call on the right partition
                }
        }

        public static int partition(int[] arr, int low, int high) {
                int pivot = arr[high];
                int i = low - 1;
                for (int j = low; j < high; j++) {
                        if (arr[j] <= pivot) {
                                i++;
                                int temp = arr[i];
                                arr[i] = arr[j];
                                arr[i] = temp;
                        }
                }
                int idx = i + 1;
                int temp = arr[idx];
                arr[i] = arr[high];
                arr[high] = temp;
                return idx;
        }


        private void swap(int[] arr, int i, int j) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
        }

        public int getThreads() {
                return 1;
        }
}