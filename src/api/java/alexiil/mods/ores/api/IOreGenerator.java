package alexiil.mods.ores.api;

import java.util.Random;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IOreGenerator {
    void genOre(World world, BlockPos pos, Random rand);
}
