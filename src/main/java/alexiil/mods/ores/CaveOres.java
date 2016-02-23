package alexiil.mods.ores;

import net.minecraft.init.Blocks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import alexiil.mods.ores.api.CaveOresAPI;
import alexiil.mods.ores.api.ICaveOreRegistry.ICaveOre;

@Mod(modid = CaveLib.MODID, version = CaveLib.VERSION, dependencies = CaveLib.DEPENDENCIES)
public class CaveOres {

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        CaveOresAPI.registry = CaveOreRegistry.INSTANCE;

        ICaveOre coalOre = CaveOresAPI.registry.construct(0.01, 10, 200, Blocks.coal_ore.getDefaultState(), 10);
        CaveOresAPI.registry.registerOre(Blocks.stone.getDefaultState(), coalOre);
        // CaveOresAPI.registry.registerOre(Blocks.hardened_clay.getDefaultState(), coalOre);
        // CaveOresAPI.registry.registerOre(Blocks.stained_hardened_clay, coalOre);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.TERRAIN_GEN_BUS.register(CaveTerrainGenListener.INSTANCE);
    }
}
