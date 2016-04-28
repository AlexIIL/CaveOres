package alexiil.mc.mod.ores;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import alexiil.mc.mod.ores.api.ICaveOreRegistry.ICaveOre;
import alexiil.mc.mod.ores.api.ICaveOreRegistry.ICaveOreEntry;

public enum OreGenerator {
    INSTANCE;

    public static void genOre(ICaveOreEntry entry, World world, BlockPos pos, Random rand) {
        EnumGenPattern pattern = EnumGenPattern.values()[rand.nextInt(EnumGenPattern.values().length)];
        double size = entry.genParams().nextSize(pos, rand);
        pattern.generatePositions(pos, rand, size, new OreGenSplitter(world, entry, pos));
    }

    private static class OreGenSplitter implements ICaveOreGenSplit {
        private final World world;
        private final ICaveOreEntry entry;
        private final BlockPos center;

        public OreGenSplitter(World world, ICaveOreEntry entry, BlockPos center) {
            this.world = world;
            this.entry = entry;
            this.center = center;
        }

        @Override
        public double genOre(double size, BlockPos pos) {
            IBlockState current = world.getBlockState(pos);
            List<ICaveOre> toGen = entry.customReplacements().get(current);
            if (toGen == null) toGen = entry.defaultOres();
            if (toGen == null) return size;
            toGen = new ArrayList<>(toGen);
            toGen.sort((a, b) -> a.sizeRequired() > b.sizeRequired() ? -1 : (a.sizeRequired() < b.sizeRequired() ? 1 : 0));
            if (entry.allReplacables().contains(current)) {
                for (ICaveOre ore : toGen) {
                    if (ore.sizeRequired() > size) continue;
                    world.setBlockState(pos, ore.ore(), 2);
                    return size - ore.sizeCost();
                }
            }
            return size;
        }
    }

    public static boolean isSameChunk(BlockPos a, BlockPos b) {
        if (a.getX() >> 4 != b.getX() >> 4) return false;
        if (a.getZ() >> 4 != b.getZ() >> 4) return false;
        return true;
    }

    public interface ICaveOreGenSplit {
        double genOre(double size, BlockPos pos);
    }
}
