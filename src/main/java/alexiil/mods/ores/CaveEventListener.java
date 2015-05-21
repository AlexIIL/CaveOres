package alexiil.mods.ores;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import alexiil.mods.lib.SearchBox;

public class CaveEventListener {
    public static final CaveEventListener INSTANCE = new CaveEventListener();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void event(OreGenEvent.Post event) {
        World world = event.world;
        BlockPos eventPos = event.pos;
        Chunk chunk = world.getChunkFromBlockCoords(eventPos);
        SearchBox box = new SearchBox(eventPos, eventPos.add(15, 0, 15));
        for (BlockPos pos : box) {
            
        }

    }
}
