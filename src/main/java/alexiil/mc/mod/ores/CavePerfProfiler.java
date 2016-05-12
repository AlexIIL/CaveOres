package alexiil.mc.mod.ores;

public class CavePerfProfiler {
    private static final int POSSIBLE_SHIFT_FACTOR = 11;
    private static final int USED_SHIFT_FACTOR = 5;

    private static final int MAX_BLOCKS_PER_CHUNK = 16 * 256 * 16;

    private static final int ARRAY_SIZE_FOR_POSSIBLE = getArrayIndexForPossible(MAX_BLOCKS_PER_CHUNK) + 1;
    private static final int ARRAY_SIZE_FOR_USED = getArrayIndexForUsed(MAX_BLOCKS_PER_CHUNK) + 1;

    private static int count = 0;
    private static final int MAX_COUNT = 1000;

    private static final long[][] TIMINGS_ARRAY;
    private static final int[][] COUNT_ARRAY;

    private static final boolean profilingEnabled = CaveLib.isDev();

    static {
        if (profilingEnabled) {
            CaveLog.info("Maximum array size (possible): " + ARRAY_SIZE_FOR_POSSIBLE);
            CaveLog.info("Maximum array size (used) : " + ARRAY_SIZE_FOR_USED);

            TIMINGS_ARRAY = new long[ARRAY_SIZE_FOR_POSSIBLE][ARRAY_SIZE_FOR_USED];
            COUNT_ARRAY = new int[ARRAY_SIZE_FOR_POSSIBLE][ARRAY_SIZE_FOR_USED];
        } else {
            TIMINGS_ARRAY = null;
            COUNT_ARRAY = null;
        }
    }

    public static void logPerf(int possible, int used, long nanoSeconds) {
        if (profilingEnabled) {
            int arrPoss = getArrayIndexForPossible(possible);
            int arrUsed = getArrayIndexForUsed(used);

            TIMINGS_ARRAY[arrPoss][arrUsed] += nanoSeconds / 1000;
            COUNT_ARRAY[arrPoss][arrUsed]++;
            count++;
            if (count % MAX_COUNT == 0) {
                dump();
            }
        }
    }

    private static void dump() {
        CaveLog.info("Timing report dump for the last " + count + " chunks");
        for (int i = 0; i < ARRAY_SIZE_FOR_POSSIBLE; i++) {
            int possibleGens = (i + 1) << POSSIBLE_SHIFT_FACTOR;
            for (int j = 0; j < ARRAY_SIZE_FOR_USED; j++) {
                int usedGens = (j + 1) << USED_SHIFT_FACTOR;
                long time = TIMINGS_ARRAY[i][j];
                int count = COUNT_ARRAY[i][j];
                if (count > 0) {
                    CaveLog.info("  - Out of " + possibleGens + " poss, up to " + usedGens + " ores genned, took " + (time / 1000) + "ms total for " + count + " gens.");
                }
            }
        }
    }

    private static int getArrayIndexForPossible(int possible) {
        return possible >> POSSIBLE_SHIFT_FACTOR;
    }

    private static int getArrayIndexForUsed(int used) {
        return used >> USED_SHIFT_FACTOR;
    }
}
