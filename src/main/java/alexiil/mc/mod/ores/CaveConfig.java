package alexiil.mc.mod.ores;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CaveConfig {
    public static File cfgFile;
    public static Configuration cfg;

    public static double noAirChance;
    public static boolean genWood;
    public static boolean genClay;
    public static boolean clump;

    private static Property PROP_NO_AIR_CHANCE;
    private static Property PROP_GEN_WOOD;
    private static Property PROP_GEN_CLAY;
    private static Property PROP_CLUMP;

    public static void preInit(FMLPreInitializationEvent event) {
        cfgFile = event.getSuggestedConfigurationFile();
        cfg = new Configuration(cfgFile);
        cfg.load();

        String comment = "What multiplier should be given to non-cave blocks for generating ore.  Values above ";
        PROP_NO_AIR_CHANCE = cfg.get("general", "noAirGenChance", 0.1, comment, 0, 1);
        PROP_GEN_WOOD = cfg.get("general", "genPlanks", false, "Should wooden planks generate underground?");
        PROP_GEN_CLAY = cfg.get("general", "genClay", false, "Should clay generate underground?");
        PROP_CLUMP = cfg.get("general", "clump", true, "Should some ores generate clumped into denser block forms?");

        cfg.save();

        reloadConfig();
    }

    private static void reloadConfig() {
        noAirChance = PROP_NO_AIR_CHANCE.getDouble();
        genWood = PROP_GEN_WOOD.getBoolean();
        genClay = PROP_GEN_CLAY.getBoolean();
        clump = PROP_CLUMP.getBoolean();
    }
}
