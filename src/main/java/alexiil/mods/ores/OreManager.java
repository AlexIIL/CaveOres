package alexiil.mods.ores;

import java.util.Map;
import java.util.Random;

import net.minecraft.util.BlockPos;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;

import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

import alexiil.version.api.VersionedApi;

public class OreManager {
    private static final Map<GenerateMinable.EventType, OreInfo> ores = Maps.newHashMap();

    static {
        ores.put(GenerateMinable.EventType.COAL, new OreInfo() {
            @Override
            public int getCount(ChunkProviderSettings cps) {
                return cps.coalCount;
            }

            @Override
            public int getSize(ChunkProviderSettings cps) {
                return cps.coalSize;
            }

            @Override
            public IBlockChooser createChooser(ChunkProviderSettings cps) {
                return new DefaultBlockChooser(cps.coalMinHeight, cps.coalMaxHeight);
            }
        });
        ores.put(GenerateMinable.EventType.DIAMOND, new OreInfo() {

            @Override
            public int getCount(ChunkProviderSettings cps) {
                return cps.diamondCount;
            }

            @Override
            public int getSize(ChunkProviderSettings cps) {
                return cps.diamondSize;
            }

            @Override
            public IBlockChooser createChooser(ChunkProviderSettings cps) {
                return new DefaultBlockChooser(cps.diamondMinHeight, cps.diamondMaxHeight);
            }
        });
        ores.put(GenerateMinable.EventType.GOLD, new OreInfo() {
            @Override
            public int getCount(ChunkProviderSettings cps) {
                return cps.goldCount;
            }

            @Override
            public int getSize(ChunkProviderSettings cps) {
                return cps.goldSize;
            }

            @Override
            public IBlockChooser createChooser(ChunkProviderSettings cps) {
                return new DefaultBlockChooser(cps.goldMinHeight, cps.goldMaxHeight);
            }
        });
        ores.put(GenerateMinable.EventType.IRON, new OreInfo() {
            @Override
            public int getCount(ChunkProviderSettings cps) {
                return cps.ironCount;
            }

            @Override
            public int getSize(ChunkProviderSettings cps) {
                return cps.ironSize;
            }

            @Override
            public IBlockChooser createChooser(ChunkProviderSettings cps) {
                return new DefaultBlockChooser(cps.ironMinHeight, cps.ironMaxHeight);
            }
        });
        ores.put(GenerateMinable.EventType.LAPIS, new OreInfo() {
            @Override
            public int getCount(ChunkProviderSettings cps) {
                return cps.lapisCount;
            }

            @Override
            public int getSize(ChunkProviderSettings cps) {
                return cps.lapisSize;
            }

            @Override
            public IBlockChooser createChooser(final ChunkProviderSettings cps) {
                return new IBlockChooser() {
                    @Override
                    public BlockPos getPos(BlockPos origin, Random rand) {
                        int y = rand.nextInt(cps.lapisSpread) + rand.nextInt(cps.lapisSpread) + cps.lapisCenterHeight - cps.lapisSpread;
                        if (y <= 0)
                            y = 1;
                        return origin.add(rand.nextInt(16), y, rand.nextInt(16));
                    }
                };
            }
        });
        ores.put(GenerateMinable.EventType.QUARTZ, new OreInfo() {
            @Override
            public int getCount(ChunkProviderSettings cps) {
                return 16;
            }

            @Override
            public int getSize(ChunkProviderSettings cps) {
                return 14;
            }

            @Override
            public IBlockChooser createChooser(ChunkProviderSettings cps) {
                return new DefaultBlockChooser(10, 118);
            }
        });
        ores.put(GenerateMinable.EventType.REDSTONE, new OreInfo() {

            @Override
            public int getCount(ChunkProviderSettings cps) {
                return cps.redstoneCount;
            }

            @Override
            public int getSize(ChunkProviderSettings cps) {
                return cps.redstoneSize;
            }

            @Override
            public IBlockChooser createChooser(ChunkProviderSettings cps) {
                return new DefaultBlockChooser(cps.redstoneMinHeight, cps.redstoneMaxHeight);
            }
        });
    }

    @VersionedApi.Final
    public static abstract class OreInfo {
        private final Map<ChunkProviderSettings, IBlockChooser> choosers;

        public OreInfo() {
            choosers = new MapMaker().weakKeys().makeMap();
        }

        public abstract int getCount(ChunkProviderSettings cps);

        public abstract int getSize(ChunkProviderSettings cps);

        public final IBlockChooser getChooser(ChunkProviderSettings cps) {
            if (choosers.containsKey(cps))
                return choosers.get(cps);
            IBlockChooser chooser = createChooser(cps);
            choosers.put(cps, chooser);
            return chooser;
        }

        protected abstract IBlockChooser createChooser(ChunkProviderSettings cps);
    }

    @VersionedApi.Final
    public interface IBlockChooser {
        BlockPos getPos(BlockPos origin, Random rand);
    }

    @VersionedApi.Final
    public static class DefaultBlockChooser implements IBlockChooser {
        private final int min, max;

        @VersionedApi.Final
        public DefaultBlockChooser(int min, int max) {
            this.min = Math.min(min, 1);
            this.max = max;
        }

        @Override
        @VersionedApi.Final
        public BlockPos getPos(BlockPos origin, Random rand) {
            int y = rand.nextInt(max - min) + min;
            return origin.add(rand.nextInt(16), y, rand.nextInt(16));
        }
    }

    @VersionedApi.Final
    public static OreInfo getOre(GenerateMinable.EventType type) {
        if (ores.containsKey(type))
            return ores.get(type);
        return null;
    }
}
