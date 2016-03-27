package alexiil.mc.mod.ores.compat;

import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import alexiil.mc.mod.ores.CaveLog;

public interface ICaveOreCompat {
    public enum AttemptType {
        /** Indicates that a block existing is NOT implied by a mod existing, as the block might not be registered due
         * to a config option etc. */
        MIGHT_EXIST,
        /** Indicates that if this mod is loaded then the block MUST also be loaded */
        SHOULD_EXIST;
    }

    void preInit();

    default void init() {}

    default void postInit() {}

    default void doThingIfBlockExists(String modId, String blockName, AttemptType type, Consumer<Block> consumeIfExists) {
        Block block = Block.blockRegistry.getObject(new ResourceLocation(modId, blockName));
        if (block != null) {
            consumeIfExists.accept(block);
        } else if (type == AttemptType.SHOULD_EXIST) {
            CaveLog.error("[compat][" + modId + "] Attempted to find a block called " + blockName + " but failed!");
        } else if (type == AttemptType.MIGHT_EXIST) {
            CaveLog.info("[compat][" + modId + "] Attempted to find a block called " + blockName + " but failed!");
        }
    }
}
