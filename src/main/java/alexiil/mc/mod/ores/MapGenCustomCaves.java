package alexiil.mc.mod.ores;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.base.Objects;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

/** Overridden cave generator that will also add ore generation places to the sides of caves. Much cheaper than finding
 * random places in the whole chunk.
 * 
 * This has the disadvantage that we have no compat with any custom cave generator. */
/* Impl notes: this was taken first as a copy of "MapGenCaves" and deobfuscated to allow proper editing. If this is
 * actually Illegal please let me know and I will remove it from the github repository immediatly. */
public class MapGenCustomCaves extends MapGenCaves {
    private List<BlockPos> viableCavePositions = new ArrayList<>();

    @Override
    public void generate(World world, int x, int z, ChunkPrimer primer) {
        int i = this.range;
        this.worldObj = world;
        this.rand.setSeed(world.getSeed());
        long j = this.rand.nextLong();
        long k = this.rand.nextLong();

        for (int l = x - i; l <= x + i; ++l) {
            for (int i1 = z - i; i1 <= z + i; ++i1) {
                long j1 = l * j;
                long k1 = i1 * k;
                this.rand.setSeed(j1 ^ k1 ^ world.getSeed());
                this.recursiveGenerate(world, l, i1, x, z, primer);
            }
        }

        CaveTerrainGenListener.INSTANCE.addCaveData(world, x, z, viableCavePositions);
        viableCavePositions.clear();
    }

    /** Should probably be "genCaveCaller" */
    @Override
    protected void func_180703_a(long randLong, int x, int z, ChunkPrimer primer, double randXCoord, double randYCoord, double randZCoord) {
        this.func_180702_a(randLong, x, z, primer, randXCoord, randYCoord, randZCoord, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
    }

    /** Should probably be "genCaves" */
    @Override
    protected void func_180702_a(long seed, int chunkX, int chunkZ, ChunkPrimer primer, double randXCoord, double randYCoord, double randZCoord, float randA, float randXZDir, float randYDir, int randD, int randE, double randF) {
        double x = chunkX * 16 + 8;
        double z = chunkZ * 16 + 8;
        float xzDirAlter = 0.0F;
        float yDirAlter = 0.0F;
        Random random = new Random(seed);

        if (randE <= 0) {
            int i = this.range * 16 - 16;
            randE = i - random.nextInt(i / 4);
        }

        boolean flag2 = false;

        if (randD == -1) {
            randD = randE / 2;
            flag2 = true;
        }

        int randG = random.nextInt(randE / 2) + randE / 4;

        for (boolean rand_1_in_6 = random.nextInt(6) == 0; randD < randE; ++randD) {
            double somethingXZ = 1.5D + MathHelper.sin(randD * (float) Math.PI / randE) * randA * 1.0F;
            double somethingY = somethingXZ * randF;
            float cos_randYDir = MathHelper.cos(randYDir);
            float sin_randYDir = MathHelper.sin(randYDir);
            randXCoord += MathHelper.cos(randXZDir) * cos_randYDir;
            randYCoord += sin_randYDir;
            randZCoord += MathHelper.sin(randXZDir) * cos_randYDir;

            if (rand_1_in_6) {
                randYDir = randYDir * 0.92F;
            } else {
                randYDir = randYDir * 0.7F;
            }

            randYDir = randYDir + yDirAlter * 0.1F;
            randXZDir += xzDirAlter * 0.1F;
            yDirAlter = yDirAlter * 0.9F;
            xzDirAlter = xzDirAlter * 0.75F;
            yDirAlter = yDirAlter + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            xzDirAlter = xzDirAlter + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (!flag2 && randD == randG && randA > 1.0F && randE > 0) {
                this.func_180702_a(random.nextLong(), chunkX, chunkZ, primer, randXCoord, randYCoord, randZCoord, random.nextFloat() * 0.5F + 0.5F, randXZDir - ((float) Math.PI / 2F), randYDir / 3.0F, randD, randE, 1.0D);
                this.func_180702_a(random.nextLong(), chunkX, chunkZ, primer, randXCoord, randYCoord, randZCoord, random.nextFloat() * 0.5F + 0.5F, randXZDir + ((float) Math.PI / 2F), randYDir / 3.0F, randD, randE, 1.0D);
                return;
            }

            if (flag2 || random.nextInt(4) != 0) {
                double xRandDiff = randXCoord - x;
                double zRandDiff = randZCoord - z;
                double d6 = randE - randD;
                double d7 = randA + 2.0F + 16.0F;

                if (xRandDiff * xRandDiff + zRandDiff * zRandDiff - d6 * d6 > d7 * d7) {
                    return;
                }

                if (randXCoord >= x - 16.0D - somethingXZ * 2.0D && randZCoord >= z - 16.0D - somethingXZ * 2.0D && randXCoord <= x + 16.0D + somethingXZ * 2.0D && randZCoord <= z + 16.0D + somethingXZ * 2.0D) {
                    int rand_x_low = MathHelper.floor_double(randXCoord - somethingXZ) - chunkX * 16 - 1;
                    int rand_x_high = MathHelper.floor_double(randXCoord + somethingXZ) - chunkX * 16 + 1;
                    int rand_y_low = MathHelper.floor_double(randYCoord - somethingY) - 1;
                    int rand_y_high = MathHelper.floor_double(randYCoord + somethingY) + 1;
                    int rand_z_low = MathHelper.floor_double(randZCoord - somethingXZ) - chunkZ * 16 - 1;
                    int rand_z_high = MathHelper.floor_double(randZCoord + somethingXZ) - chunkZ * 16 + 1;

                    // Cap the values
                    if (rand_x_low < 0) rand_x_low = 0;

                    if (rand_x_high > 16) rand_x_high = 16;

                    if (rand_y_low < 1) rand_y_low = 1;

                    if (rand_y_high > 248) rand_y_high = 248;

                    if (rand_z_low < 0) rand_z_low = 0;

                    if (rand_z_high > 16) rand_z_high = 16;

                    boolean hitWater = false;

                    // Check for water
                    for (int j1 = rand_x_low; !hitWater && j1 < rand_x_high; ++j1) {
                        for (int k1 = rand_z_low; !hitWater && k1 < rand_z_high; ++k1) {
                            for (int l1 = rand_y_high + 1; !hitWater && l1 >= rand_y_low - 1; --l1) {
                                if (l1 >= 0 && l1 < 256) {
                                    primer.getBlockState(j1, l1, k1);

                                    if (isOceanBlock(primer, j1, l1, k1, chunkX, chunkZ)) {
                                        hitWater = true;
                                    }

                                    if (l1 != rand_y_low - 1 && j1 != rand_x_low && j1 != rand_x_high - 1 && k1 != rand_z_low && k1 != rand_z_high - 1) {
                                        l1 = rand_y_low;
                                    }
                                }
                            }
                        }
                    }

                    if (!hitWater) {
                        for (int in_x = Math.max(0, rand_x_low - 1); in_x <= Math.min(15, rand_x_high); in_x++) {
                            for (int in_z = Math.max(0, rand_z_low - 1); in_z <= Math.min(15, rand_z_high); in_z++) {
                                for (int in_y = rand_y_low - 1; in_y <= rand_y_high; in_y++) {
                                    // TODO: Optimizable (memory) point
                                    // Maybe this could be a pool of mutable block pos? Might help GC, not sure.
                                    viableCavePositions.add(new BlockPos(in_x, in_y, in_z));
                                }
                            }
                        }
                        for (int in_x = rand_x_low; in_x < rand_x_high; ++in_x) {
                            double d10 = (in_x + chunkX * 16 + 0.5D - randXCoord) / somethingXZ;

                            for (int in_z = rand_z_low; in_z < rand_z_high; ++in_z) {
                                double d8 = (in_z + chunkZ * 16 + 0.5D - randZCoord) / somethingXZ;
                                boolean foundTopBlock = false;

                                if (d10 * d10 + d8 * d8 < 1.0D) {
                                    for (int in_y = rand_y_high; in_y > rand_y_low; --in_y) {
                                        double d9 = (in_y - 1 + 0.5D - randYCoord) / somethingY;

                                        if (d9 > -0.7D && d10 * d10 + d9 * d9 + d8 * d8 < 1.0D) {
                                            IBlockState stateAt = primer.getBlockState(in_x, in_y, in_z);
                                            IBlockState stateAbove = Objects.firstNonNull(primer.getBlockState(in_x, in_y + 1, in_z), Blocks.AIR.getDefaultState());

                                            if (isTopBlock(primer, in_x, in_y, in_z, chunkX, chunkZ)) {
                                                foundTopBlock = true;
                                            }

                                            digBlock(primer, in_x, in_y, in_z, chunkX, chunkZ, foundTopBlock, stateAt, stateAbove);
                                        }
                                    }
                                }
                            }
                        }

                        if (flag2) {
                            break;
                        }
                    }
                }
            }
        }
    }

    /** Should be called "checkIsCaveReplacable" */
    @Override
    protected boolean func_175793_a(IBlockState stateA, IBlockState stateAbove) {
        Block[] retTrueBlocks = { //
            Blocks.STONE, Blocks.DIRT, Blocks.GRASS, Blocks.HARDENED_CLAY,//
            Blocks.STAINED_HARDENED_CLAY, Blocks.SANDSTONE, Blocks.RED_SANDSTONE,//
            Blocks.MYCELIUM, Blocks.SNOW_LAYER };
        final Block blockA = stateA.getBlock();
        for (Block ret : retTrueBlocks) {
            if (blockA == ret) return true;
        }
        boolean falling = blockA instanceof BlockFalling;
        return falling && stateAbove.getMaterial() != Material.WATER;
    }

    /** Recursively called by generate() */
    @Override
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int x, int z, ChunkPrimer chunkPrimerIn) {
        int i = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(15) + 1) + 1);

        if (this.rand.nextInt(7) != 0) {
            i = 0;
        }

        for (int j = 0; j < i; ++j) {
            double randXCoord = chunkX * 16 + this.rand.nextInt(16);
            double randYCoord = this.rand.nextInt(this.rand.nextInt(120) + 8);
            double randZCoord = chunkZ * 16 + this.rand.nextInt(16);
            int k = 1;

            if (this.rand.nextInt(4) == 0) {
                this.func_180703_a(this.rand.nextLong(), x, z, chunkPrimerIn, randXCoord, randYCoord, randZCoord);
                k += this.rand.nextInt(4);
            }

            for (int l = 0; l < k; ++l) {
                float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f2 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();

                if (this.rand.nextInt(10) == 0) {
                    f2 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                }

                this.func_180702_a(this.rand.nextLong(), x, z, chunkPrimerIn, randXCoord, randYCoord, randZCoord, f2, f, f1, 0, 0, 1.0D);
            }
        }
    }

    @Override
    protected boolean isOceanBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
        net.minecraft.block.Block block = data.getBlockState(x, y, z).getBlock();
        return block == Blocks.FLOWING_WATER || block == Blocks.WATER;
    }

    // Exception biomes to make sure we generate like vanilla
    @SuppressWarnings("static-method")
    private boolean isExceptionBiome(net.minecraft.world.biome.BiomeGenBase biome) {
        if (biome == Biomes.BEACH) return true;
        if (biome == Biomes.DESERT) return true;
        return false;
    }

    // Determine if the block at the specified location is the top block for the biome, we take into account
    // Vanilla bugs to make sure that we generate the map the same way vanilla does.
    private boolean isTopBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
        net.minecraft.world.biome.BiomeGenBase biome = worldObj.getBiomeGenForCoords(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        IBlockState state = data.getBlockState(x, y, z);
        return (isExceptionBiome(biome) ? state.getBlock() == Blocks.GRASS : state.getBlock() == biome.topBlock);
    }

    /** Digs out the current block, default implementation removes stone, filler, and top block Sets the block to lava
     * if y is less then 10, and air other wise. If setting to air, it also checks to see if we've broken the surface
     * and if so tries to make the floor the biome's top block
     *
     * @param data Block data array
     * @param index Pre-calculated index into block data
     * @param x local X position
     * @param y local Y position
     * @param z local Z position
     * @param chunkX Chunk X position
     * @param chunkZ Chunk Y position
     * @param foundTop True if we've encountered the biome's top block. Ideally if we've broken the surface. */
    @Override
    protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop, IBlockState state, IBlockState up) {
        net.minecraft.world.biome.BiomeGenBase biome = worldObj.getBiomeGenForCoords(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
        IBlockState top = biome.topBlock;
        IBlockState filler = biome.fillerBlock;

        if (this.func_175793_a(state, up) || state.getBlock() == top.getBlock() || state.getBlock() == filler.getBlock()) {
            if (y < 10) {
                data.setBlockState(x, y, z, Blocks.LAVA.getDefaultState());
            } else {
                data.setBlockState(x, y, z, Blocks.AIR.getDefaultState());

                if (up.getBlock() == Blocks.SAND) {
                    BlockSand.EnumType type = up.getValue(BlockSand.VARIANT);
                    boolean isRedSand = type == BlockSand.EnumType.RED_SAND;
                    data.setBlockState(x, y + 1, z, isRedSand ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState());
                }

                if (foundTop && data.getBlockState(x, y - 1, z).getBlock() == filler.getBlock()) {
                    data.setBlockState(x, y - 1, z, top.getBlock().getDefaultState());
                }
            }
        }
    }

}
