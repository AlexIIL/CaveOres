package alexiil.mods.ores;

import java.util.*;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import alexiil.mods.ores.DefaultOreGenerator.ICaveOreGenSplit;

public enum EnumGenPattern {
    SPHERE(EnumGenPattern::sphereGenPattern),
    TENDRIL(EnumGenPattern::tendrilGenPattern);

    private final IOreGenPattern pattern;

    private EnumGenPattern(IOreGenPattern pattern) {
        this.pattern = pattern;
    }

    public void generatePositions(BlockPos pos, Random rand, double actualSize, ICaveOreGenSplit splitter) {
        pattern.generateOrePositions(pos, rand, actualSize, splitter);
    }

    private static void sphereGenPattern(BlockPos pos, Random rand, double actualSize, ICaveOreGenSplit splitter) {
        List<BlockPos> openSet = new ArrayList<>();
        Set<BlockPos> closedSet = new HashSet<>();
        Map<BlockPos, Double> distances = new HashMap<>();
        distances.put(pos, Double.valueOf(0));
        openSet.add(pos);
        int fails = 100;
        while (actualSize > 0 && !openSet.isEmpty() && fails > 0) {
            Collections.shuffle(openSet, rand);
            BlockPos chosen = getLowestScore(openSet, distances);
            openSet.remove(chosen);
            closedSet.add(chosen);
            if (!DefaultOreGenerator.isSameChunk(chosen, pos)) continue;
            double newSize = splitter.genOre(actualSize, chosen);
            if (newSize < actualSize) {
                actualSize = newSize;
                for (EnumFacing face : EnumFacing.VALUES) {
                    BlockPos offset = chosen.offset(face);
                    if (closedSet.contains(offset)) continue;
                    if (openSet.contains(offset)) continue;
                    openSet.add(offset);
                    distances.put(offset, offset.distanceSq(pos));
                }
            } else {
                fails--;
            }
        }
    }

    private static <K> K getLowestScore(Collection<K> set, Map<K, Double> map) {
        K lowestPoint = null;
        double lowestValue = Double.POSITIVE_INFINITY;

        for (K point : set) {
            double val = map.get(point).doubleValue();
            if (val < lowestValue) {
                lowestPoint = point;
                lowestValue = val;
            }
        }

        return lowestPoint;
    }

    private static void tendrilGenPattern(BlockPos pos, Random rand, double actualSize, ICaveOreGenSplit splitter) {
        // FIXME TEMP!
        sphereGenPattern(pos, rand, actualSize, splitter);
    }

    public interface IOreGenPattern {
        void generateOrePositions(BlockPos pos, Random rand, double actualSize, ICaveOreGenSplit splitter);
    }
}
