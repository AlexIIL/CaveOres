package alexiil.mods.ores.api;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public interface ICaveOreRegistry {
    ICaveOre construct(double chance, Predicate<BlockPos> canGenPredicate, IOreGenerator oreGen);

    default ICaveOre construct(double chance, int minY, int maxY, IBlockState toGen, int maxPerSeam) {
        return construct(chance, getDefaultCanGenPredicate(minY, maxY), getDefaultGenaratorFor(toGen, maxPerSeam));
    }

    void registerOre(IBlockState from, ICaveOre ore);

    void unregisterOre(IBlockState from, ICaveOre ore);

    default void registerOre(Block from, ICaveOre ore) {
        for (IBlockState state : from.getBlockState().getValidStates()) {
            registerOre(state, ore);
        }
    }

    default ICaveOre registerOre(IBlockState from, double chance, Predicate<BlockPos> canGenPredicate, IOreGenerator oreGen) {
        ICaveOre ore = construct(chance, canGenPredicate, oreGen);
        registerOre(from, ore);
        return ore;
    }

    default Predicate<BlockPos> getDefaultCanGenPredicate(int yMin, int yMax) {
        return (pos) -> (pos.getY() >= yMin && pos.getY() <= yMax);
    }

    IOreGenerator getDefaultGenaratorFor(IBlockState toGen, int max);

    void unregisterAll(IBlockState from);

    public interface ICaveOre {
        void unregister();

        boolean canGen(BlockPos pos);

        double chance();

        IOreGenerator oreGenerator();
    }
}
