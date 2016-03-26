package alexiil.mods.ores;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

import alexiil.mods.ores.api.ICaveOreRegistry;

public enum CaveOreRegistry implements ICaveOreRegistry {
    INSTANCE;

    private final Map<String, OreEntry> entries = new HashMap<>();

    @Override
    public ICaveOreEntry getEntry(String oreDictionaryName) {
        return null;
    }

    @Override
    public ICaveOreEntry getOrCreateEntry(String oreDictionaryName, Predicate<BlockPos> canGen, double chance, ICaveOreGenerator gen) {
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

    @Override
    public ICaveOreGenerator createSimpleGen(double size, double sizeDeviation) {
        return new DefaultOreGenerator(size, sizeDeviation);
    }

    public Stream<OreEntry> getEntriesForGen() {
        return entries.values().stream().sorted((a, b) -> (a.chance > b.chance) ? 1 : (a.chance < b.chance ? -1 : 0));
    }

    public static class OreEntry implements ICaveOreRegistry.ICaveOreEntry {
        private final String identifier;
        private final Set<IBlockState> defaultReplacements = new HashSet<>();
        private final Map<IBlockState, List<ICaveOre>> customReplacements = new HashMap<>();
        private final Predicate<BlockPos> canGen;
        private final double chance;
        private final ICaveOreGenerator generator;
        private final List<ICaveOre> defaultOres = new ArrayList<>();

        public OreEntry(String identifier, Predicate<BlockPos> canGen, double chance, ICaveOreGenerator gen) {
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
        public boolean canGen(BlockPos pos) {
            return canGen.test(pos);
        }

        @Override
        public double chance() {
            return chance;
        }

        @Override
        public ICaveOreGenerator generator() {
            return generator;
        }
    }
}
