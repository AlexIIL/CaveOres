package alexiil.mods.ores;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.google.common.collect.Maps;

import alexiil.mods.lib.SearchUtils;
import alexiil.mods.ores.OreManager.IBlockChooser;
import alexiil.mods.ores.OreManager.OreInfo;

public class CaveEventListener {
    public static final CaveEventListener INSTANCE = new CaveEventListener();
    private static final ThreadLocal<Map<WorldGenerator, OreInfo>> worldGens = new ThreadLocal<Map<WorldGenerator, OreInfo>>();

    private static Map<WorldGenerator, OreInfo> getMap() {
        if (worldGens.get() != null)
            return worldGens.get();
        Map<WorldGenerator, OreInfo> map = Maps.newHashMap();
        worldGens.set(map);
        return map;
    }

    @SubscribeEvent
    public void genTerrain(GenerateMinable event) {
        OreInfo oreInfo = OreManager.getOre(event.type);
        if (oreInfo != null) {
            event.setResult(Result.DENY);
            getMap().put(event.generator, oreInfo);
        }
    }

    private long l = 0;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void pre(OreGenEvent.Pre event) {
        l = System.currentTimeMillis();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void oreGenPost(OreGenEvent.Post event) {
        Map<WorldGenerator, OreInfo> worldGen = getMap();
        fails = 0;
        retrys = 0;
        World world = event.world;
        BlockPos pos = event.pos;
        Random rand = event.rand;

        String s = world.getWorldInfo().getGeneratorOptions();
        if (s == null)
            s = "";
        ChunkProviderSettings cps = ChunkProviderSettings.Factory.func_177865_a(s).func_177864_b();

        for (Entry<WorldGenerator, OreInfo> genInfo : worldGen.entrySet()) {
            generatePart(world, pos, rand, cps, genInfo);
        }
        worldGen.clear();

        l -= System.currentTimeMillis();
        CaveOres.INSTANCE.log.info("Ore generation took " + -l + "ms, with " + fails + " fails and " + retrys + " retrys");
    }

    int fails = 0;
    int retrys = 0;

    private void generatePart(World world, BlockPos pos, Random rand, ChunkProviderSettings cps, Entry<WorldGenerator, OreInfo> genInfo) {
        OreInfo info = genInfo.getValue();
        WorldGenerator gen = genInfo.getKey();
        IBlockChooser chooser = info.getChooser(cps);
        int failsLeft = 4096;

        for (int oresGenerated = 0; oresGenerated < info.getCount(cps); oresGenerated++) {
            if (failsLeft <= 0) {
                BlockPos aPos = chooser.getPos(pos, rand);
                gen.generate(world, rand, aPos);
                fails++;
            }
            else if (!tryGenerateOre(world, pos, rand, chooser, gen)) {
                failsLeft--;
                oresGenerated--;
                retrys++;
            }
        }
    }

    /** @return <code>True</code> if the ore was generated successfully */
    private boolean tryGenerateOre(World world, BlockPos origin, Random rand, IBlockChooser chooser, WorldGenerator gen) {
        BlockPos oreLocation = chooser.getPos(origin, rand);
        for (BlockPos search : SearchUtils.searchFaces(oreLocation)) {
            if (!world.getChunkProvider().chunkExists(search.getX() >> 4, search.getZ() >> 4))
                continue;
            if (world.isAirBlock(search)) {
                gen.generate(world, rand, oreLocation);
                return true;
            }
        }
        return false;
    }
}
