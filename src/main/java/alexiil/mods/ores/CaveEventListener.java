package alexiil.mods.ores;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CaveEventListener {
    public static final CaveEventListener INSTANCE = new CaveEventListener();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void event(OreGenEvent.Post event) {
        World world = event.world;
        BlockPos eventPos = event.pos;
        Chunk chunk = world.getChunkFromBlockCoords(eventPos);

        for (BlockPos pos : SearchUtils.searchChunk(chunk)) {
            IBlockState b = world.getBlockState(pos);
            if (OreBlockHandler.isOre(b))
                for (BlockPos pos2 : SearchUtils.searchAround(pos)) {

                }
        }
    }
}
