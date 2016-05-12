package alexiil.mc.mod.ores;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import alexiil.mc.mod.ores.api.ICaveOreRegistry;

public enum CaveOreRegistry implements ICaveOreRegistry {
    INSTANCE;

    private final Map<String, OreEntry> entries = new HashMap<>();

    @Override
    public ICaveOreEntry getEntry(String oreDictionaryName) {
        return null;
    }

    @Override
    public ICaveOreEntry getOrCreateEntry(String oreDictionaryName, BiPredicate<World, BlockPos> canGen, double chance, ICaveOreGeneratorParams gen) {
        ICaveOreEntry existing = getEntry(oreDictionaryName);
        if (existing != null) return existing;
        OreEntry entry = new OreEntry(oreDictionaryName, canGen, chance, gen);
        entries.put(oreDictionaryName, entry);
        return entry;
    }

    @Override
    public Stream<IBlockState> getReplacables() {
        return entries.values().stream().flatMap(entry -> entry.allReplacables().stream());
    }

    public Stream<OreEntry> getEntriesForGen() {
        return entries.values().stream().sorted((a, b) -> (a.chance > b.chance) ? 1 : (a.chance < b.chance ? -1 : 0));
    }

    public static class OreEntry implements ICaveOreRegistry.ICaveOreEntry {
        private final String identifier;
        private final Set<IBlockState> defaultReplacements = new HashSet<>();
        private final Map<IBlockState, List<ICaveOre>> customReplacements = new HashMap<>();
        private final BiPredicate<World, BlockPos> canGen;
        private final double chance;
        private final ICaveOreGeneratorParams generator;
        private final List<ICaveOre> defaultOres = new ArrayList<>();

        public OreEntry(String identifier, BiPredicate<World, BlockPos> canGen, double chance, ICaveOreGeneratorParams gen) {
            this.identifier = identifier;
            this.canGen = canGen;
            this.chance = chance;
            this.generator = gen;
        }

        @Override
        public String uniqueIdentifier() {
            return identifier;
        }

        @Override
        public Set<IBlockState> defaultReplacements() {
            return defaultReplacements;
        }

        @Override
        public Map<IBlockState, List<ICaveOre>> customReplacements() {
            return customReplacements;
        }

        @Override
        public List<ICaveOre> defaultOres() {
            return defaultOres;
        }

        @Override
        public void addDefaultOre(double sizeRequired, double sizeCost, IBlockState ore) {
            defaultOres.add(new ICaveOre() {
                @Override
                public double sizeRequired() {
                    return sizeRequired;
                }

                @Override
                public double sizeCost() {
                    return sizeCost;
                }

                @Override
                public IBlockState ore() {
                    return ore;
                }
            });
        }

        @Override
        public boolean canGen(World world, BlockPos pos) {
            return canGen.test(world, pos);
        }

        @Override
        public double chance() {
            return chance;
        }

        @Override
        public ICaveOreGeneratorParams genParams() {
            return generator;
        }
    }
}
