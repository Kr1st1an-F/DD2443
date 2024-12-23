import java.util.*;

public class Log {
        private Log() {
                // Do not implement
        }

        public static int[] validate(Log.Entry[] log) {
                if (log == null) return new int[]{0, 1};

                int discrepancyCount = 0;
                Set<Integer> referenceSet = new HashSet<>();

                log = Arrays.stream(log) // convert to stream
                        .filter(entry -> entry.method != Method.EMPTY)
                        .sorted(Comparator.comparingLong(entry -> entry.timestamp)) // sort by timestamp
                        .toArray(Log.Entry[]::new); // convert back to array

                adjustFakeRemove(log);

                for (Log.Entry entry : log) {
                        boolean resultFromSet;
                        switch (entry.method) {
                                case ADD:
                                        resultFromSet = referenceSet.add(entry.arg);
                                        break;
                                case REMOVE:
                                        resultFromSet = referenceSet.remove(entry.arg);
                                        break;
                                case CONTAINS:
                                        resultFromSet = referenceSet.contains(entry.arg);
                                        break;
                                default:
                                        throw new RuntimeException("Error: Invalid entry.method in HashSet.");
                        }

                        if (resultFromSet != entry.ret) {
                                discrepancyCount++;
                        }
                }

                return new int[]{discrepancyCount, log.length};
        }

        private static void adjustFakeRemove(Log.Entry[] log) {
                Map<Integer, Long> lastRemoveTimestamps = new HashMap<>();

                for (Log.Entry entry : log) {
                        if (entry.method == Method.REMOVE) {
                                lastRemoveTimestamps.put(entry.arg, entry.timestamp);
                        } else if (entry.method == Method.FAKE_REMOVE) {
                                Long lastTimestamp = lastRemoveTimestamps.get(entry.arg);
                                if (lastTimestamp != null) {
                                        entry.method = Method.REMOVE;
                                        entry.timestamp = lastTimestamp + 1;
                                } else {
                                        throw new RuntimeException("Atomic Action Error");
                                }
                        }
                }
        }

        public static class Entry {
                public Method method;
                public int arg;
                public boolean ret;
                public long timestamp;

                public Entry(Method method, int arg, boolean ret, long timestamp) {
                        this.method = method;
                        this.arg = arg;
                        this.ret = ret;
                        this.timestamp = timestamp;
                }
        }

        public enum Method {
                ADD, REMOVE, CONTAINS, EMPTY, FAKE_REMOVE
        }
}