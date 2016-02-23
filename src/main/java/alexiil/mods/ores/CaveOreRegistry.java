package alexiil.mods.ores;

import java.util.*;
import java.util.function.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import alexiil.mods.ores.api.ICaveOreRegistry;
import alexiil.mods.ores.api.IOreGenerator;

public enum CaveOreRegistry implements ICaveOreRegistry {
    INSTANCE;

    private Map<IBlockState, OreList> oreMap = new IdentityHashMap<>();

    @Override
    public ICaveOre construct(double chance, Predicate<BlockPos> canGenPredicate, IOreGenerator oreGen) {
        return new OreGenEntry(chance, canGenPredicate, oreGen);
    }

    @Override
    public IOreGenerator getDefaultGenaratorFor(IBlockState toGen, int maxPerSeam) {
        return new DefaultOreGenerator(toGen, maxPerSeam);
    }

    @Override
    public void registerOre(IBlockState from, ICaveOre ore) {
        if (!oreMap.containsKey(from)) {
            oreMap.put(from, new OreList());
        }
        oreMap.get(from).ores.add(ore);
    }

    @Override
    public void unregisterOre(IBlockState from, ICaveOre ore) {
        if (!oreMap.containsKey(from)) return;
        OreList list = oreMap.get(from);
        list.ores.remove(ore);
    }

    @Override
    public void unregisterAll(IBlockState from) {
        oreMap.remove(from);
    }

    public boolean genOre(World world, BlockPos pos, Random rand) {
        IBlockState current = world.getBlockState(pos);
        if (!hasReplacementFor(current)) return false;

        OreList ore = oreMap.get(current);
        return ore.genOre(world, pos, rand);
    }

    public boolean hasReplacementFor(IBlockState state) {
        return oreMap.containsKey(state);
    }

    private static class OreList {
        private final List<ICaveOre> ores = new ArrayList<>();

        public boolean genOre(World world, BlockPos pos, Random rand) {
            int index = rand.nextInt(ores.size());
            if (index < 0) throw new IllegalStateException("Too few ores! " + ores.size());
            // Perhaps this should be additive instead of multiplicative?
            // (currently it has to be the right index and lower than the chance, but maybe just run through the list
            // until you get one with the right chance? So a lower-than map of double->ore?)
            ICaveOre ore = ores.get(index);
            if (!ore.canGen(pos)) return false;
            if (rand.nextDouble() > ore.chance()) return false;
            ore.oreGenerator().genOre(world, pos, rand);
            return true;
        }
    }

    public static class OreGenEntry implements ICaveOre {
        public final double chance;
        public final Predicate<BlockPos> canGenPredicate;
        public final IOreGenerator oreGen;

        private OreGenEntry(double chance, Predicate<BlockPos> canGenPredicate, IOreGenerator oreGen) {
            this.chance = chance;
            this.canGenPredicate = canGenPredicate;
            this.oreGen = oreGen;
        }

        @Override
        public void unregister() {
            // TODO Auto-generated method stub
            throw new AbstractMethodError("Implement this!");
        }

        @Override
        public boolean canGen(BlockPos pos) {
            return canGenPredicate.test(pos);
        }

        @Override
        public double chance() {
            return chance;
        }

        @Override
        public IOreGenerator oreGenerator() {
            return oreGen;
        }
    }
}