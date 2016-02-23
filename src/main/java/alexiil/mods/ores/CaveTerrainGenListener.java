package alexiil.mods.ores;

import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public enum CaveTerrainGenListener {
    INSTANCE;

    @SubscribeEvent
    public void initMagGenCave(InitMapGenEvent event) {
        System.out.println("event" + event.type);
        if (event.type == EventType.CAVE) {
            // TMP for testing
            event.newGen = new MapGenCustomCaves();
        }
    }
}
