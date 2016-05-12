package alexiil.mc.mod.ores;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import alexiil.mc.mod.ores.CaveOreRegistry.OreEntry;

public enum CaveTerrainGenListener {
    INSTANCE;

    @SubscribeEvent
    @SuppressWarnings("static-method")
    public void generateOreEvent(GenerateMinable gen) {
        EventType type = gen.getType();
        if (type == GenerateMinable.EventType.COAL) gen.setResult(Result.DENY);
        else if (type == GenerateMinable.EventType.IRON) gen.setResult(Result.DENY);
        else if (type == GenerateMinable.EventType.GOLD) gen.setResult(Result.DENY);
        else if (type == GenerateMinable.EventType.LAPIS) gen.setResult(Result.DENY);
        else if (type == GenerateMinable.EventType.DIAMOND) gen.setResult(Result.DENY);
        else if (type == GenerateMinable.EventType.EMERALD) gen.setResult(Result.DENY);
        else if (type == GenerateMinable.EventType.REDSTONE) gen.setResult(Result.DENY);
        else if (type == GenerateMinable.EventType.QUARTZ) gen.setResult(Result.DENY);
    }

    /** We technically violate the principle of ore generation (we might generate ores before a listener actually
     * receives the event), so we will have the lowest priority */
    @SubscribeEvent
    public void decorateChunk(PopulateChunkEvent.Post event) {
        World world = event.getWorld();

        long start = System.nanoTime();
        world.theProfiler.startSection("caveores");

        Chunk chunk = world.getChunkFromChunkCoords(event.getChunkX(), event.getChunkZ());
        BlockPos offset = new BlockPos(chunk.xPosition * 16, 0, chunk.zPosition * 16);

        int[] successes = new int[] { 0 };
        int[] possible = new int[] { 0 };

        // Make our own random for this so that changes to our algorithms don't change other/later generation
        long x = offset.getX() >> 4;
        long z = offset.getZ() >> 4;
        Random rand = new Random(x + z ^ world.getSeed());

        forEachSolid(offset, chunk.getBlockStorageArray(), (pos) -> {
            possible[0]++;
            if (genOre(world, pos, new Random(rand.nextLong()))) {
                successes[0]++;
            }
        }, (pos) -> {
            float val = rand.nextFloat();
            if (val < 0.1) {
                genOre(world, pos, new Random(rand.nextLong()));
            }
        });

        long diff = System.nanoTime() - start;
        world.theProfiler.endSection();
        CavePerfProfiler.logPerf(possible[0], successes[0], diff);
    }

    private enum EnumGenValidity {
        AIR,
        SOLID_ACCESSIBLE,
        SOILD_SURROUNDED
    }

    private static void forEachSolid(BlockPos offset, ExtendedBlockStorage[] data, Consumer<BlockPos> consumerAccessible, Consumer<BlockPos> consumerSurrounded) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == null) continue;
            for (int y = i * 16; y < i * 16 + 16; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        EnumGenValidity validity = getValidity(data, x, y, z);
                        if (validity == EnumGenValidity.SOLID_ACCESSIBLE) {
                            consumerAccessible.accept(new BlockPos(x, y, z).add(offset));
                        } else if (validity == EnumGenValidity.SOILD_SURROUNDED) {
                            consumerSurrounded.accept(new BlockPos(x, y, z).add(offset));
                        }
                    }
                }
            }
        }
    }

    private static EnumGenValidity getValidity(ExtendedBlockStorage[] data, int x, int y, int z) {
        ExtendedBlockStorage at = data[y / 16];
        int pY = y & 15;
        IBlockState state = at.get(x, pY, z);
        if (isSeeThrough(state)) return EnumGenValidity.AIR;
        if (y > 0) {
            int checkY = y - 1;
            ExtendedBlockStorage checkStorage = data[checkY / 16];
            if (checkStorage == null) return EnumGenValidity.SOLID_ACCESSIBLE;// Empty mini-chunk so its all air
            state = checkStorage.get(x, checkY & 15, z);
            if (isSeeThrough(state)) {
                return EnumGenValidity.SOLID_ACCESSIBLE;
            }
        }
        if (y < 255) {
            int checkY = y + 1;
            ExtendedBlockStorage checkStorage = data[checkY / 16];
            if (checkStorage == null) return EnumGenValidity.SOLID_ACCESSIBLE;// Empty mini-chunk so its all air
            state = checkStorage.get(x, checkY & 15, z);
            if (isSeeThrough(state)) {
                return EnumGenValidity.SOLID_ACCESSIBLE;
            }
        }
        if (x > 0) {
            int checkX = x - 1;
            state = at.get(checkX, pY, z);
            if (isSeeThrough(state)) {
                return EnumGenValidity.SOLID_ACCESSIBLE;
            }
        }
        if (x < 15) {
            int checkX = x + 1;
            state = at.get(checkX, pY, z);
            if (isSeeThrough(state)) {
                return EnumGenValidity.SOLID_ACCESSIBLE;
            }
        }
        if (z > 0) {
            int checkZ = z - 1;
            state = at.get(x, pY, checkZ);
            if (isSeeThrough(state)) {
                return EnumGenValidity.SOLID_ACCESSIBLE;
            }
        }
        if (z < 15) {
            int checkZ = z + 1;
            state = at.get(x, pY, checkZ);
            if (isSeeThrough(state)) {
                return EnumGenValidity.SOLID_ACCESSIBLE;
            }
        }
        return EnumGenValidity.SOILD_SURROUNDED;
    }

    public static boolean isSeeThrough(IBlockState state) {
        Material mat = state.getMaterial();
        return mat == Material.AIR || mat == Material.PLANTS || mat == Material.WATER;
    }

    private static boolean genOre(World world, BlockPos pos, Random rand) {
        double c = rand.nextDouble();
        List<OreEntry> sortedEntries = CaveOreRegistry.INSTANCE.getEntriesForGen()//
                .filter((entry) -> entry.canGen(world, pos))//
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
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
