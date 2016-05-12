package alexiil.mc.mod.ores.api;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICaveOreRegistry {
    ICaveOreEntry getEntry(String oreDictionaryName);

    default ICaveOreEntry getOrCreateEntry(String oreDictionaryName, Predicate<BlockPos> canGen, double chance, ICaveOreGeneratorParams gen) {
        return getOrCreateEntry(oreDictionaryName, (world, pos) -> canGen.test(pos), chance, gen);
    }

    ICaveOreEntry getOrCreateEntry(String oreDictionaryName, BiPredicate<World, BlockPos> canGen, double chance, ICaveOreGeneratorParams gen);

    Stream<IBlockState> getReplacables();

    default boolean canRelace(IBlockState state) {
        return getReplacables().anyMatch((s2) -> s2 == state);
    }

    default ICaveOreGeneratorParams createOreGenWithNormalDistribution(double size, double deviation) {
        return (pos, rand) -> size + rand.nextGaussian() * deviation;
    }

    default ICaveOreGeneratorParams createOreGenWithUniformDistribution(int min, int max) {
        return (pos, rand) -> rand.nextInt(max - min) + min;
    }

    public interface ICaveOreEntry {
        /** An identifier for the ore. Should be the ore-dictionary name for the base ore block. */
        String uniqueIdentifier();

        boolean canGen(World world, BlockPos pos);

        /** @return A chance between 0 and 1. 0 means that it will never generate, 1 means it will be pushed up higher
         *         in the queue of what to gen and so might not actually generate at all if too many ores are
         *         registered. A good number (for, say, coal) is 0.0006 */
        double chance();

        /** @return A set of blockstates that will be replaced with the {@link #defaultOres()}. */
        Set<IBlockState> defaultReplacements();

        /** @return A map of replacable states -> custom replacements. If a key is in both this map and the
         *         {@link #defaultReplacements()} set then this one will be prefered. */
        Map<IBlockState, List<ICaveOre>> customReplacements();

        default Set<IBlockState> allReplacables() {
            Set<IBlockState> set = new HashSet<>();
            set.addAll(defaultReplacements());
            set.addAll(customReplacements().keySet());
            return set;
        }

        /** @return The base ore to generate (usually a stone based one) */
        List<ICaveOre> defaultOres();

        /** @param sizeRequired The minimum size of the deposit (left to generate) for this ore to be generated
         * @param sizeCost The size cost to deduct from generation after placing this ore.
         * @param ore */
        void addDefaultOre(double sizeRequired, double sizeCost, IBlockState ore);

        default void addDefaultOre(double sizeCost, IBlockState ore) {
            addDefaultOre(sizeCost, sizeCost, ore);
        }

        ICaveOreGeneratorParams genParams();
    }

    public interface ICaveOre {
        double sizeRequired();

        double sizeCost();

        IBlockState ore();
    }

    @FunctionalInterface
    public interface ICaveOreGeneratorParams {
        double nextSize(BlockPos pos, Random rand);
    }
}
