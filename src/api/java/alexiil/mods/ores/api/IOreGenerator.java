package alexiil.mods.ores.api;

import java.util.Random;

import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;

public interface IOreGenerator {
    void genOre(ChunkPrimer primer, BlockPos pos, Random rand);
}
