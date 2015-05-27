package alexiil.mods.ores;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import alexiil.mods.lib.SearchUtils;

public class CaveEventListener {
    public static final CaveEventListener INSTANCE = new CaveEventListener();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void event(OreGenEvent.Post event) {
        World world = event.world;
        Chunk chunk = world.getChunkFromBlockCoords(event.pos);
        Random rand = event.rand;
        CaveOres.INSTANCE.log.info("Searching for ores in " + event.pos);

        int i = 0;

        for (BlockPos pos : SearchUtils.searchChunk(chunk)) {
            searchBlocksInChunk(world, rand, pos);
            i++;
        }
        CaveOres.INSTANCE.log.info(i);
    }

    private void searchBlocksInChunk(World world, Random rand, BlockPos pos) {
        IBlockState block = world.getBlockState(pos);
        if (!OreBlockHandler.isOre(block))
            return;
        boolean foundAir = false;
        CaveOres.INSTANCE.log.info("Found ore at " + pos);
        for (BlockPos around : SearchUtils.searchAround(pos, 5)) {
            if (world.isAirBlock(around)) {
                CaveOres.INSTANCE.log.info("Found air at " + around);
                foundAir = true;
                break;
            }
        }
        if (!foundAir) {
            double dbl = rand.nextDouble();
            if (dbl < 0.75D) {
                world.setBlockState(pos, Blocks.stone.getDefaultState(), 0);
            }
        }
    }
}
