package alexiil.mods.ores;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import com.google.common.collect.Lists;

public class OreBlockHandler {
    private static final List<IBlockState> ores = Lists.newArrayList();

    /** Call this with an ore that you would like to only generate higher next to caves. */
    public static void addOre(IBlockState state) {
        ores.add(state);
    }

    /** Call this with an ore that you would like to only generate higher next to caves. */
    public static void addOre(Block block) {
        for (Object obj : block.getBlockState().getValidStates()) {
            IBlockState state = (IBlockState) obj;
            addOre(state);
        }
    }

    public static boolean isOre(IBlockState state) {
        return ores.contains(state);
    }
}
