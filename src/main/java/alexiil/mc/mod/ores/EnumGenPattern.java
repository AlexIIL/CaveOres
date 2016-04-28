package alexiil.mc.mod.ores;

import java.util.*;
import java.util.function.ToDoubleFunction;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import alexiil.mc.mod.ores.OreGenerator.ICaveOreGenSplit;

public enum EnumGenPattern {
    SPHERE(EnumGenPattern::sphereGenPattern),
    FAT_TENDRIL(EnumGenPattern::fatTendrilGenPattern),
    TENDRIL(EnumGenPattern::tendrilGenPattern);

    private final IOreGenPattern pattern;

    private EnumGenPattern(IOreGenPattern pattern) {
        this.pattern = pattern;
    }

    public void generatePositions(BlockPos pos, Random rand, double actualSize, ICaveOreGenSplit splitter) {
        pattern.generateOrePositions(pos, rand, actualSize, splitter);
    }

    private static double fatTendrilGenPattern(BlockPos pos, Random rand, double actualSize, ICaveOreGenSplit splitter) {
        // Generate 3 faces
        EnumFacing face1 = EnumFacing.values()[rand.nextInt(6)];
        EnumFacing face2 = EnumFacing.values()[rand.nextInt(6)];
        EnumFacing face3 = EnumFacing.values()[rand.nextInt(6)];

        // Add up the directions
        Vec3i direction = BlockPos.ORIGIN.add(face1.getDirectionVec()).add(face2.getDirectionVec()).add(face3.getDirectionVec());

        Vec3d normalized = new Vec3d(direction).normalize();
        final int mult = 2;
        Vec3d dir = new Vec3d(normalized.xCoord * mult, normalized.yCoord * mult, normalized.zCoord * mult);

        // Make 3 block offsets
        BlockPos offset2 = new BlockPos(new Vec3d(pos).add(dir));

        BlockPos offset3 = new BlockPos(new Vec3d(offset2).add(dir));

        double forOne = actualSize / 3;
        double leftOver = genPatternGeneric(pos, pos, rand, forOne, splitter, pos::distanceSq);
        double forTwo = actualSize / 3 + leftOver;
        leftOver = genPatternGeneric(pos, offset2, rand, forTwo, splitter, offset2::distanceSq);
        double forThree = actualSize / 3 + leftOver;
        return genPatternGeneric(pos, offset3, rand, forThree, splitter, offset3::distanceSq);
    }

    private static double tendrilGenPattern(BlockPos pos, Random rand, double actualSize, ICaveOreGenSplit splitter) {
        // Generate 3 faces
        EnumFacing face1 = EnumFacing.values()[rand.nextInt(6)];
        EnumFacing face2 = EnumFacing.values()[rand.nextInt(6)];
        EnumFacing face3 = EnumFacing.values()[rand.nextInt(6)];

        // Add up the directions
        Vec3i direction = BlockPos.ORIGIN.add(face1.getDirectionVec()).add(face2.getDirectionVec()).add(face3.getDirectionVec());

        BlockPos target = new BlockPos(direction.getX() * 3, direction.getY() * 3, direction.getZ() * 3);

        return genPatternGeneric(pos, pos, rand, actualSize, splitter, offset -> {
            // Manhatten disatnce
            return Math.abs((target.getX() - offset.getX()) + (target.getY() - offset.getY()) + (target.getZ() - offset.getZ()));
        });
    }

    private static double sphereGenPattern(BlockPos pos, Random rand, double actualSize, ICaveOreGenSplit splitter) {
        return genPatternGeneric(pos, pos, rand, actualSize, splitter, pos::distanceSq);
    }

    private static double genPatternGeneric(BlockPos original, BlockPos from, Random rand, double actualSize, ICaveOreGenSplit splitter, ToDoubleFunction<BlockPos> scoreFunc) {
        List<BlockPos> openSet = new ArrayList<>();
        Set<BlockPos> closedSet = new HashSet<>();
        Map<BlockPos, Double> distances = new HashMap<>();
        distances.put(from, Double.valueOf(0));
        openSet.add(from);
        int fails = 100;
        while (actualSize > 0 && !openSet.isEmpty() && fails > 0) {
            Collections.shuffle(openSet, rand);
            BlockPos chosen = getLowestScore(openSet, distances);
            openSet.remove(chosen);
            closedSet.add(chosen);
            if (!OreGenerator.isSameChunk(chosen, original)) continue;
            double newSize = splitter.genOre(actualSize, chosen);
            if (newSize < actualSize) {
                actualSize = newSize;
                for (EnumFacing face : EnumFacing.VALUES) {
                    BlockPos offset = chosen.offset(face);
                    if (closedSet.contains(offset)) continue;
                    if (openSet.contains(offset)) continue;
                    openSet.add(offset);
                    distances.put(offset, scoreFunc.applyAsDouble(offset));
                }
            } else {
                fails--;
            }
        }
        return actualSize;
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

    public interface IOreGenPattern {
        double generateOrePositions(BlockPos pos, Random rand, double actualSize, ICaveOreGenSplit splitter);
    }
}
