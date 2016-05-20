package alexiil.mc.mod.ores;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

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
    public static final double DEFAULT_FACTOR = 0.0001;

    public static ICaveOreEntry COAL_ENTRY;
    public static ICaveOreEntry IRON_ENTRY;
    public static ICaveOreEntry GOLD_ENTRY;
    public static ICaveOreEntry LAPIS_ENTRY;
    public static ICaveOreEntry REDSTONE_ENTRY;
    public static ICaveOreEntry DIAMOND_ENTRY;
    public static ICaveOreEntry EMERALD_ENTRY;
    public static ICaveOreEntry QUARTZ_ENTRY;

    @EventHandler
    @SuppressWarnings("static-method")
    public void preinit(FMLPreInitializationEvent event) {
        CaveConfig.preInit(event);

        CaveOresAPI.registry = CaveOreRegistry.INSTANCE;
        CaveOresCompat.preInit();

        boolean clump = CaveConfig.clump && !Loader.isModLoaded("denseores");

        // TODO: Make size be based on a guassian(center=60, std.dev=40) [coal]
        // TODO: Make size be based on a guassian(center=40, std.dev=20) [iron]

        ICaveOreGeneratorParams coalGen = CaveOresAPI.registry.createOreGenWithNormalDistribution(16, 10);
        COAL_ENTRY = CaveOresAPI.registry.getOrCreateEntry("oreCoal", heightValidator(23, 200), DEFAULT_FACTOR * 13, coalGen);
        COAL_ENTRY.addDefaultOre(1, Blocks.COAL_ORE.getDefaultState());
        if (clump) COAL_ENTRY.addDefaultOre(17, 9, Blocks.COAL_BLOCK.getDefaultState());
        addDefaultStoneReplacements(COAL_ENTRY);

        ICaveOreGeneratorParams ironGen = CaveOresAPI.registry.createOreGenWithNormalDistribution(8, 3);
        IRON_ENTRY = CaveOresAPI.registry.getOrCreateEntry("oreIron", heightValidator(14, 120), DEFAULT_FACTOR * 6, ironGen);
        IRON_ENTRY.addDefaultOre(1, Blocks.IRON_ORE.getDefaultState());
        if (clump) IRON_ENTRY.addDefaultOre(17, 9, Blocks.IRON_BLOCK.getDefaultState());
        addDefaultStoneReplacements(IRON_ENTRY);

        ICaveOreGeneratorParams goldGen = CaveOresAPI.registry.createOreGenWithNormalDistribution(7, 6);
        GOLD_ENTRY = CaveOresAPI.registry.getOrCreateEntry("oreGold", heightValidator(5, 35), DEFAULT_FACTOR * 5, goldGen);
        GOLD_ENTRY.addDefaultOre(1, Blocks.GOLD_ORE.getDefaultState());
        if (clump) GOLD_ENTRY.addDefaultOre(17, 9, Blocks.GOLD_BLOCK.getDefaultState());
        addDefaultStoneReplacements(GOLD_ENTRY);

        ICaveOreGeneratorParams lapisGen = CaveOresAPI.registry.createOreGenWithNormalDistribution(5, 3);
        LAPIS_ENTRY = CaveOresAPI.registry.getOrCreateEntry("oreLapis", heightValidator(3, 30), DEFAULT_FACTOR * 3, lapisGen);
        LAPIS_ENTRY.addDefaultOre(1, Blocks.LAPIS_ORE.getDefaultState());
        if (clump) LAPIS_ENTRY.addDefaultOre(17, 9, Blocks.LAPIS_BLOCK.getDefaultState());
        addDefaultStoneReplacements(LAPIS_ENTRY);

        ICaveOreGeneratorParams redstoneGen = CaveOresAPI.registry.createOreGenWithNormalDistribution(9, 2);
        REDSTONE_ENTRY = CaveOresAPI.registry.getOrCreateEntry("oreRedstone", heightValidator(5, 20), DEFAULT_FACTOR * 4, redstoneGen);
        REDSTONE_ENTRY.addDefaultOre(1, Blocks.REDSTONE_ORE.getDefaultState());
        if (clump) REDSTONE_ENTRY.addDefaultOre(17, 9, Blocks.REDSTONE_BLOCK.getDefaultState());
        addDefaultStoneReplacements(REDSTONE_ENTRY);

        ICaveOreGeneratorParams diamondGen = CaveOresAPI.registry.createOreGenWithNormalDistribution(6, 3);
        DIAMOND_ENTRY = CaveOresAPI.registry.getOrCreateEntry("oreDiamond", heightValidator(5, 23), DEFAULT_FACTOR * 2, diamondGen);
        DIAMOND_ENTRY.addDefaultOre(1, Blocks.DIAMOND_ORE.getDefaultState());
        if (clump) DIAMOND_ENTRY.addDefaultOre(17, 9, Blocks.DIAMOND_BLOCK.getDefaultState());
        addDefaultStoneReplacements(DIAMOND_ENTRY);

        ICaveOreGeneratorParams emeraldGen = CaveOresAPI.registry.createOreGenWithUniformDistribution(1, 2);
        BiPredicate<World, BlockPos> validator = biomeValidator(Biomes.EXTREME_HILLS);
        EMERALD_ENTRY = CaveOresAPI.registry.getOrCreateEntry("oreEmerald", validator, DEFAULT_FACTOR, emeraldGen);
        EMERALD_ENTRY.addDefaultOre(1, Blocks.EMERALD_ORE.getDefaultState());
        if (clump) EMERALD_ENTRY.addDefaultOre(17, 9, Blocks.EMERALD_BLOCK.getDefaultState());
        addDefaultStoneReplacements(EMERALD_ENTRY);

        ICaveOreGeneratorParams quartzGen = CaveOresAPI.registry.createOreGenWithNormalDistribution(6, 5);
        QUARTZ_ENTRY = CaveOresAPI.registry.getOrCreateEntry("oreQuartz", (world, pos) -> true, DEFAULT_FACTOR * 7, quartzGen);
        QUARTZ_ENTRY.addDefaultOre(1, Blocks.QUARTZ_ORE.getDefaultState());
        if (clump) QUARTZ_ENTRY.addDefaultOre(9, 4, Blocks.QUARTZ_BLOCK.getDefaultState());
        QUARTZ_ENTRY.defaultReplacements().add(Blocks.NETHERRACK.getDefaultState());

        if (CaveConfig.genWood) registerWoodAsOre(clump);

        if (CaveConfig.genClay) registerClayAsOre();
    }

    public static BiPredicate<World, BlockPos> biomeValidator(Biome biome) {
        return (world, pos) -> world.getBiomeGenForCoords(pos) == biome;
    }

    public static Predicate<BlockPos> heightValidator(int min, int max) {
        return (pos) -> pos.getY() >= min && pos.getY() <= max;
    }

    public static void addDefaultStoneReplacements(ICaveOreEntry ore) {
        ore.defaultReplacements().add(Blocks.STONE.getDefaultState());
        ore.defaultReplacements().add(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE));
        ore.defaultReplacements().add(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
        ore.defaultReplacements().add(Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));
        ore.defaultReplacements().add(Blocks.SANDSTONE.getDefaultState());
    }

    private static void registerWoodAsOre(boolean clump) {
        IBlockState[][] arr = { { null, null }, { null, null }, { null, null }, { null, null }, { null, null }, { null, null } };
        for (int i = 0; i < 4; i++) {
            BlockPlanks.EnumType type = BlockPlanks.EnumType.values()[i];
            arr[i][0] = Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, type);
            arr[i][1] = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, type);
        }
        for (int i = 0; i < 2; i++) {
            BlockPlanks.EnumType type = BlockPlanks.EnumType.values()[i + 4];
            arr[i + 4][0] = Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, type);
            arr[i + 4][1] = Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, type);
        }

        for (int i = 0; i < arr.length; i++) {
            BlockPlanks.EnumType type = BlockPlanks.EnumType.values()[i];
            IBlockState[] ar = arr[i];
            ICaveOreGeneratorParams gen = CaveOresAPI.registry.createOreGenWithNormalDistribution(16, 10);
            ICaveOreEntry entry = CaveOresAPI.registry.getOrCreateEntry("plank" + StringUtils.capitalize(type.name()), (pos) -> pos.getY() >= 14 && pos.getY() <= 120, DEFAULT_FACTOR * 5, gen);
            entry.addDefaultOre(1, ar[0]);
            if (clump) entry.addDefaultOre(13, 5, ar[1]);
            addDefaultStoneReplacements(entry);
        }
    }

    private static void registerClayAsOre() {
        ICaveOreGeneratorParams gen = CaveOresAPI.registry.createOreGenWithNormalDistribution(16, 10);
        ICaveOreEntry entry = CaveOresAPI.registry.getOrCreateEntry("oreClay", (pos) -> pos.getY() >= 19 && pos.getY() <= 43, DEFAULT_FACTOR * 3, gen);
        entry.addDefaultOre(1, Blocks.CLAY.getDefaultState());
        addDefaultStoneReplacements(entry);
    }

    @EventHandler
    @SuppressWarnings("static-method")
    public void init(FMLInitializationEvent event) {
        MinecraftForge.TERRAIN_GEN_BUS.register(CaveTerrainGenListener.INSTANCE);
        MinecraftForge.ORE_GEN_BUS.register(CaveTerrainGenListener.INSTANCE);
        MinecraftForge.EVENT_BUS.register(CaveTerrainGenListener.INSTANCE);

        CaveOresCompat.init();
    }

    @EventHandler
    @SuppressWarnings("static-method")
    public void postInit(FMLInitializationEvent event) {
        CaveOresCompat.postInit();
    }
}
