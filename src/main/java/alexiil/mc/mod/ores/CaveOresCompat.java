package alexiil.mc.mod.ores;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.Loader;

import alexiil.mc.mod.ores.compat.CompatDenseOres;
import alexiil.mc.mod.ores.compat.CompatExampleMod;
import alexiil.mc.mod.ores.compat.ICaveOreCompat;

/* Compat registration classes. Add your own compat class in here */
public class CaveOresCompat {
    private static final List<ICaveOreCompat> compats = new ArrayList<>();

    // Called by CaveOres internally. Don't call these
    static void preInit() {
        // Example registration
        tryCompat(CompatExampleMod.INSTANCE, "examplemod");
        // You can also use multiple modid qualifiers to make multi-mod compat
        // tryCompat(Compat_BuildCraft_and_DenseOres.INSTANCE, "BuildCraft|Core", "denseores");

        // Fellow modders: Add your compat registration AFTER THIS
        tryCompat(CompatDenseOres.INSTANCE, "denseores");
        // AND BEFORE THIS
    }

    public static void init() {
        for (ICaveOreCompat compat : compats) {
            compat.init();
        }
    }

    public static void postInit() {
        for (ICaveOreCompat compat : compats) {
            compat.postInit();
        }
    }

    private static void tryCompat(ICaveOreCompat compat, String... modids) {
        for (String modid : modids) {
            if (!Loader.isModLoaded(modid)) return;
        }
        compats.add(compat);
        compat.preInit();
    }
}
