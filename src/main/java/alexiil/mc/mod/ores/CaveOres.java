package alexiil.mc.mod.ores;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import alexiil.mc.mod.ores.api.CaveOresAPI;
import alexiil.mc.mod.ores.api.ICaveOreRegistry.ICaveOreEntry;
import alexiil.mc.mod.ores.api.ICaveOreRegistry.ICaveOreGeneratorParams;

@Mod(modid = CaveLib.MODID, version = CaveLib.VERSION, dependencies = CaveLib.DEPENDENCIES)
public class CaveOres {
    public static final double DEFAULT_FACTOR = 0.00001;

    @EventHandler
    @SuppressWarnings("static-method")
    public void preinit(FMLPreInitializationEvent event) {
        CaveOresAPI.registry = CaveOreRegistry.INSTANCE;
        CaveOresCompat.preInit();

        boolean denseOres = Loader.isModLoaded("denseores");

        // TODO: Make size be based on a guassian(center=60, std.dev=40) [coal]
        // TODO: Make size be based on a guassian(center=40, std.dev=20) [iron]

        ICaveOreGeneratorParams coalGen = CaveOresAPI.registry.createOreGenWithNormalDistribution(16, 10);
        ICaveOreEntry coal = CaveOresAPI.registry.getOrCreateEntry("oreCoal", (pos) -> pos.getY() >= 23 || pos.getY() <= 200, DEFAULT_FACTOR * 13, coalGen);
        coal.addDefaultOre(1, Blocks.coal_ore.getDefaultState());
        if (!denseOres) coal.addDefaultOre(17, 9, Blocks.coal_block.getDefaultState());
        coal.defaultReplacements().add(Blocks.stone.getDefaultState());
        coal.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE));
        coal.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
        coal.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));

        ICaveOreGeneratorParams ironGen = CaveOresAPI.registry.createOreGenWithNormalDistribution(8, 3);
        ICaveOreEntry iron = CaveOresAPI.registry.getOrCreateEntry("oreIron", (pos) -> pos.getY() >= 14 || pos.getY() <= 120, DEFAULT_FACTOR * 6, ironGen);
        iron.addDefaultOre(1, Blocks.iron_ore.getDefaultState());
        if (!denseOres) iron.addDefaultOre(17, 9, Blocks.iron_block.getDefaultState());
        iron.defaultReplacements().add(Blocks.stone.getDefaultState());
        iron.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE));
        iron.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
        iron.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));

        boolean optionWood = true;

        if (optionWood) registerWoodAsOre(denseOres);

        boolean optionClay = true;
        if (optionClay) registerClayAsOre();
    }

    private static void registerWoodAsOre(boolean denseOres) {
        IBlockState[][] arr = { { null, null }, { null, null }, { null, null }, { null, null }, { null, null }, { null, null } };
        for (int i = 0; i < 4; i++) {
            BlockPlanks.EnumType type = BlockPlanks.EnumType.values()[i];
            arr[i][0] = Blocks.planks.getDefaultState().withProperty(BlockPlanks.VARIANT, type);
            arr[i][1] = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, type);
        }
        for (int i = 0; i < 2; i++) {
            BlockPlanks.EnumType type = BlockPlanks.EnumType.values()[i + 4];
            arr[i + 4][0] = Blocks.planks.getDefaultState().withProperty(BlockPlanks.VARIANT, type);
            arr[i + 4][1] = Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, type);
        }

        for (int i = 0; i < arr.length; i++) {
            BlockPlanks.EnumType type = BlockPlanks.EnumType.values()[i];
            IBlockState[] ar = arr[i];
            ICaveOreGeneratorParams gen = CaveOresAPI.registry.createOreGenWithNormalDistribution(16, 10);
            ICaveOreEntry entry = CaveOresAPI.registry.getOrCreateEntry("plank" + StringUtils.capitalize(type.name()), (pos) -> pos.getY() >= 14 && pos.getY() <= 120, DEFAULT_FACTOR * 5, gen);
            entry.addDefaultOre(1, ar[0]);
            if (!denseOres) entry.addDefaultOre(13, 5, ar[1]);
            entry.defaultReplacements().add(Blocks.stone.getDefaultState());
            entry.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE));
            entry.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
            entry.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));
        }
    }

    private static void registerClayAsOre() {
        ICaveOreGeneratorParams gen = CaveOresAPI.registry.createOreGenWithNormalDistribution(16, 10);
        ICaveOreEntry entry = CaveOresAPI.registry.getOrCreateEntry("oreClay", (pos) -> pos.getY() >= 19 && pos.getY() <= 43, DEFAULT_FACTOR * 3, gen);
        entry.addDefaultOre(1, Blocks.clay.getDefaultState());
        entry.defaultReplacements().add(Blocks.stone.getDefaultState());
    }

    @EventHandler
    @SuppressWarnings("static-method")
    public void init(FMLInitializationEvent event) {
        MinecraftForge.TERRAIN_GEN_BUS.register(CaveTerrainGenListener.INSTANCE);
        MinecraftForge.ORE_GEN_BUS.register(CaveTerrainGenListener.INSTANCE);

        CaveOresCompat.init();
    }

    @EventHandler
    @SuppressWarnings("static-method")
    public void postInit(FMLInitializationEvent event) {
        CaveOresCompat.postInit();
    }
}
