package alexiil.mods.ores;

import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import alexiil.mods.ores.api.CaveOresAPI;
import alexiil.mods.ores.api.ICaveOreRegistry.ICaveOreEntry;
import alexiil.mods.ores.api.ICaveOreRegistry.ICaveOreGenerator;

@Mod(modid = CaveLib.MODID, version = CaveLib.VERSION, dependencies = CaveLib.DEPENDENCIES)
public class CaveOres {

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        CaveOresAPI.registry = CaveOreRegistry.INSTANCE;

        boolean denseOres = false;

        // TODO: Make size be based on a guassian(center=60, std.dev=40) [coal]
        // TODO: Make size be based on a guassian(center=40, std.dev=20) [iron]

        ICaveOreGenerator coalGen = CaveOresAPI.registry.createSimpleGen(16, 10);
        ICaveOreEntry coal = CaveOresAPI.registry.getOrCreateEntry("oreCoal", (pos) -> pos.getY() >= 23 || pos.getY() <= 200, 0.0004, coalGen);
        coal.addDefaultOre(1, Blocks.coal_ore.getDefaultState());
        if (!denseOres) coal.addDefaultOre(17, 9, Blocks.coal_block.getDefaultState());
        coal.defaultReplacements().add(Blocks.stone.getDefaultState());
        coal.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE));
        coal.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
        coal.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));

        ICaveOreGenerator ironGen = CaveOresAPI.registry.createSimpleGen(8, 3);
        ICaveOreEntry iron = CaveOresAPI.registry.getOrCreateEntry("oreIron", (pos) -> pos.getY() >= 14 || pos.getY() <= 120, 0.00024, ironGen);
        iron.addDefaultOre(1, Blocks.iron_ore.getDefaultState());
        if (!denseOres) iron.addDefaultOre(17, 9, Blocks.iron_block.getDefaultState());
        iron.defaultReplacements().add(Blocks.stone.getDefaultState());
        iron.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE));
        iron.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
        iron.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.TERRAIN_GEN_BUS.register(CaveTerrainGenListener.INSTANCE);
        MinecraftForge.ORE_GEN_BUS.register(CaveTerrainGenListener.INSTANCE);
    }
}
