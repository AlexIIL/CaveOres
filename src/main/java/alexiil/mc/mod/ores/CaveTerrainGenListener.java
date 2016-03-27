package alexiil.mc.mod.ores;

import java.util.*;

import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import alexiil.mc.mod.ores.CaveOreRegistry.OreEntry;

public enum CaveTerrainGenListener {
    INSTANCE;

    private final Map<Integer, Map<ChunkCoordIntPair, BlockPos[]>> worldCaveData = new HashMap<>();

    @SubscribeEvent
    public void initMapGenCave(InitMapGenEvent event) {
        System.out.println("event" + event.type);
        if (event.type == InitMapGenEvent.EventType.CAVE) {
            // TMP for testing
            event.newGen = new MapGenCustomCaves();
        }
    }

    public void addCaveData(World world, int chunkX, int chunkZ, List<BlockPos> positions) {
        if (!worldCaveData.containsKey(world.provider.getDimensionId())) {
            worldCaveData.put(world.provider.getDimensionId(), new HashMap<>());
        }
        Map<ChunkCoordIntPair, BlockPos[]> map = worldCaveData.get(world.provider.getDimensionId());
        BlockPos[] array = positions.toArray(new BlockPos[positions.size()]);
        map.put(new ChunkCoordIntPair(chunkX, chunkZ), array);
    }

    @SubscribeEvent
    @SuppressWarnings("static-method")
    public void generateOreEvent(GenerateMinable gen) {
        if (gen.type == GenerateMinable.EventType.COAL) gen.setResult(Result.DENY);
        if (gen.type == GenerateMinable.EventType.IRON) gen.setResult(Result.DENY);
    }

    /** We technically violate the principle of ore generation (we might generate ores before a listener actually
     * recieves the event), so we will have the lowest priority */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void decorateChunk(OreGenEvent.Pre pre) {
        long start = System.currentTimeMillis();
        World world = pre.world;
        // TODO: Move logging to a seperate class, and have time stats or something
        Map<ChunkCoordIntPair, BlockPos[]> map = worldCaveData.get(world.provider.getDimensionId());
        if (map == null) {
            System.out.println("decorateChunk|map==null|" + pre.pos);
            return;
        }
        BlockPos offset = pre.pos;
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(offset.getX() >> 4, offset.getZ() >> 4);
        BlockPos[] arr = map.remove(chunk);
        if (arr == null || arr.length == 0) {
            System.out.println("decorateChunk|arr==BlockPos[0000]|" + pre.pos);
            return;
        }
        int successes = 0;
        // Make our own random for this so that changes to our algorithms don't change other/later generation
        long x = pre.pos.getX() >> 4;
        long z = pre.pos.getZ() >> 4;
        Random rand = new Random(x + z ^ pre.world.getSeed());
        for (BlockPos pos : arr) {
            if (genOre(world, pos.add(offset), new Random(rand.nextLong()))) successes++;
        }
        long diff = System.currentTimeMillis() - start;
        System.out.println("decorateChunk|arr==BlockPos[" + arr.length + "]|" + pre.pos + "|" + successes + "|" + diff + "ms");
    }

    private static boolean genOre(World world, BlockPos pos, Random rand) {
        double c = rand.nextDouble();
        List<OreEntry> sortedEntries = CaveOreRegistry.INSTANCE.getEntriesForGen().sequential().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        for (OreEntry o : sortedEntries) {
            if (c < o.chance()) {
                OreGenerator.genOre(o, world, pos, rand);
                return true;
            } else {
                c -= o.chance();
            }
        }
        return false;
    }
}
