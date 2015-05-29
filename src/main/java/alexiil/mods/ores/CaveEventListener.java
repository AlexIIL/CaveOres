package alexiil.mods.ores;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alexiil.mods.lib.SearchUtils;
import alexiil.mods.ores.OreManager.IBlockChooser;
import alexiil.mods.ores.OreManager.OreInfo;

public class CaveEventListener {
    public static final CaveEventListener INSTANCE = new CaveEventListener();
    private static final ThreadLocal<Map<WorldGenerator, OreInfo>> worldGens = new ThreadLocal<Map<WorldGenerator, OreInfo>>();
    private static final Predicate<IBlockState> replaceableBlocks = new Predicate<IBlockState>() {
        @Override
        public boolean apply(IBlockState input) {
            if (input == null)
                return false;
            return input.getBlock() == Blocks.stone || input.getBlock() == Blocks.netherrack;
        }
    };

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

        if (oreInfo == OreManager.QUARTZ) {
            oreGenPre(new OreGenEvent.Pre(event.world, event.rand, event.pos));
            oreGenPost(new OreGenEvent.Post(event.world, event.rand, event.pos));
        }
    }

    private long l = 0;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void oreGenPre(OreGenEvent.Pre event) {
        l = System.currentTimeMillis();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void oreGenPost(OreGenEvent.Post event) {
        Map<WorldGenerator, OreInfo> worldGen = getMap();
        fails = 0;
        retrys = 0;
        World world = event.world;
        world.theProfiler.startSection("caveOreGen");
        BlockPos pos = event.pos;
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        Random rand = event.rand;

        String s = world.getWorldInfo().getGeneratorOptions();
        if (s == null)
            s = "";
        ChunkProviderSettings cps = ChunkProviderSettings.Factory.func_177865_a(s).func_177864_b();

        List<BlockPos> validPositions = Lists.newArrayList();

        for (BlockPos search : SearchUtils.searchChunk(chunk)) {
            if (!replaceableBlocks.apply(world.getBlockState(search)))
                continue;
            for (BlockPos face : SearchUtils.searchFaces(search)) {
                if (!world.getChunkProvider().chunkExists(face.getX() >> 4, face.getZ() >> 4))
                    continue;
                if (world.isAirBlock(face)) {
                    validPositions.add(search);
                    break;
                }
            }
        }

        int preSize = validPositions.size();
        for (Entry<WorldGenerator, OreInfo> genInfo : worldGen.entrySet()) {
            generatePart(world, pos, rand, cps, genInfo, validPositions);
        }
        int postSize = validPositions.size();

        worldGen.clear();

        l -= System.currentTimeMillis();
        CaveOres.INSTANCE.log.info("Ore generation took " + -l + "ms, with " + fails + " fails,  " + retrys + " retrys, " + preSize + "," + postSize
            + " sizes");

        world.theProfiler.endSection();
    }

    int fails = 0;
    int retrys = 0;

    private void generatePart(World world, BlockPos pos, Random rand, ChunkProviderSettings cps, Entry<WorldGenerator, OreInfo> genInfo,
            List<BlockPos> validPositions) {
        OreInfo info = genInfo.getValue();
        WorldGenerator gen = genInfo.getKey();
        IBlockChooser chooser = info.getChooser(cps);
        int failsLeft = validPositions.size();

        for (int oresGenerated = 0; oresGenerated < info.getCount(cps); oresGenerated++) {
            if (failsLeft <= 0 || validPositions.size() == 0) {
                BlockPos aPos = chooser.getPos(pos, rand);
                gen.generate(world, rand, aPos);
                fails++;
            }
            else if (!tryGenerateOre(world, rand, chooser, gen, validPositions)) {
                failsLeft--;
                oresGenerated--;
                retrys++;
            }
        }
    }

    /** @return <code>True</code> if the ore was generated successfully */
    private boolean tryGenerateOre(World world, Random rand, IBlockChooser chooser, WorldGenerator gen, List<BlockPos> validPositions) {
        int index = rand.nextInt(validPositions.size());
        BlockPos oreLocation = validPositions.remove(index);
        if (!chooser.isValid(oreLocation))
            return false;
        gen.generate(world, rand, oreLocation);
        return true;
    }
}
