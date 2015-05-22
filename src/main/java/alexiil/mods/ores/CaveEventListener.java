package alexiil.mods.ores;

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
        BlockPos eventPos = event.pos;
        Chunk chunk = world.getChunkFromBlockCoords(eventPos);

        for (BlockPos pos : SearchUtils.searchChunk(chunk)) {
              SearchUtils.searchAround(pos, 5);

            // TODO: make this search around for 5 blocks to find valid air, or valid ore blocks that are next to air
            // HINT: Use the updated SearchUtils.searchAround(pos,radius)
        }
    }
}
