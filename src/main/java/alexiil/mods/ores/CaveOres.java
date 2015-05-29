package alexiil.mods.ores;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import alexiil.mods.lib.AlexIILMod;

@Mod(modid = Lib.Mod.ID)
public class CaveOres extends AlexIILMod {
    @Instance
    public static CaveOres INSTANCE;

    private static float percentageCave = 1;

    @Override
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.ORE_GEN_BUS.register(CaveEventListener.INSTANCE);
        MinecraftForge.TERRAIN_GEN_BUS.register(CaveEventListener.INSTANCE);

        String comment = "The percentage of ores which will attempt to generate by caves instead of generating normally";
        percentageCave = cfg.cfg().getFloat("percentageCave", "general", 100, 0, 100, comment) / 100F;

        cfg.saveAll();
    }

    public static float getPercentageCaved() {
        return percentageCave;
    }

    @Override
    public String getCommitHash() {
        return Lib.Mod.COMMIT_HASH;
    }

    @Override
    public int getBuildType() {
        return Lib.Mod.buildType();
    }

    @Override
    public String getUser() {
        return "AlexIIL";
    }

    @Override
    public String getRepo() {
        return "CaveOres";
    }
}
