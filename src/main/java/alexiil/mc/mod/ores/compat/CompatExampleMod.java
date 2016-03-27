package alexiil.mc.mod.ores.compat;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import alexiil.mc.mod.ores.CaveOres;
import alexiil.mc.mod.ores.api.CaveOresAPI;
import alexiil.mc.mod.ores.api.ICaveOreRegistry.ICaveOreEntry;
import alexiil.mc.mod.ores.api.ICaveOreRegistry.ICaveOreGeneratorParams;

public enum CompatExampleMod implements ICaveOreCompat {
    INSTANCE;

    public Block blockExampleOre;

    @Override
    public void preInit() {
        doThingIfBlockExists("examplemod", "example_ore", AttemptType.SHOULD_EXIST, block -> {
            // Assign this block so that other compats (or ourselves later) can use this reference
            blockExampleOre = block;

            // Say that this "block_ore" has a single state (it makes it easier)
            IBlockState oreState = block.getDefaultState();

            // Lets use the same generation numbers/type as iron ore
            // (middle of range is 8 blocks, standard deviation is 3)
            ICaveOreGeneratorParams oreGen = CaveOresAPI.registry.createOreGenWithNormalDistribution(8, 3);

            // Lets generate (the center) between y = 40 and y = 60
            Predicate<BlockPos> oreGenRange = pos -> 40 <= pos.getY() && pos.getY() <= 60;

            // Lets generate about as often as iron ore
            double genChance = CaveOres.DEFAULT_FACTOR * 6;

            // Create our "ore entry data" about
            ICaveOreEntry oreEntry = CaveOresAPI.registry.getOrCreateEntry("oreExample", oreGenRange, genChance, oreGen);
            // Add ourselves as the default ore type.
            oreEntry.addDefaultOre(1, oreState);

            // We only replace stone
            oreEntry.defaultReplacements().add(Blocks.stone.getDefaultState());
            oreEntry.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE));
            oreEntry.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
            oreEntry.defaultReplacements().add(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));
        });
    }
}
