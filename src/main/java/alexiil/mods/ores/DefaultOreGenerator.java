package alexiil.mods.ores;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;

import alexiil.mods.ores.api.IOreGenerator;

public class DefaultOreGenerator implements IOreGenerator {
    private final IBlockState toGen;
    private final int maxPerSeam;

    public DefaultOreGenerator(IBlockState toGen, int maxPerSeam) {
        this.toGen = toGen;
        this.maxPerSeam = maxPerSeam;
    }

    @Override
    public void genOre(ChunkPrimer primer, BlockPos pos, Random rand) {
        // TODO: Expand!
        primer.setBlockState(pos.getX(), pos.getY(), pos.getZ(), toGen);
    }
}
