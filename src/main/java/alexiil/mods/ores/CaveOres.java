package alexiil.mods.ores;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

import alexiil.mods.lib.AlexIILMod;

@Mod(modid = Lib.Mod.ID)
public class CaveOres extends AlexIILMod {
    @Instance
    public static CaveOres INSTANCE;

    @Override
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.ORE_GEN_BUS.register(CaveEventListener.INSTANCE);
        MinecraftForge.TERRAIN_GEN_BUS.register(CaveEventListener.INSTANCE);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        for (String s : OreDictionary.getOreNames()) {
            if (s.toLowerCase(Locale.ROOT).startsWith("ore")) {
                for (ItemStack stack : OreDictionary.getOres(s)) {
                    if (!(stack.getItem() instanceof ItemBlock))
                        continue;
                    ItemBlock ib = (ItemBlock) stack.getItem();
                    Block block = ib.block;
                    OreBlockHandler.addOre(block);
                }
            }
        }
    }

    @Override
    public String getCommitHash() {
        return Lib.Mod.COMMIT_HASH;
    }

    @Override
    public int getBuildType() {
        return Lib.Mod.buildType();
    }

    @Override
    public String getUser() {
        return "AlexIIL";
    }

    @Override
    public String getRepo() {
        return "CaveOres";
    }
}
