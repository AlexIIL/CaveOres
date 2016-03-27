package alexiil.mc.mod.ores;

public class CaveLib {
    public static final String MODID = "CaveOres";
    public static final String VERSION = "@VERSION@";
    public static final String MC_VERSION = "@MC_VERSION@";
    public static final String FORGE_VERSION = "@FORGE_VERSION@";

    public static final String DEPENDENCIES = ""/* "@DEPS@" */;

    public static boolean isDev() {
        return VERSION.contains("@");
    }
}
