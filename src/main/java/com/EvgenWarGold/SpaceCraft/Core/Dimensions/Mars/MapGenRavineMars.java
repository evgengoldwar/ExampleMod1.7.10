package com.EvgenWarGold.SpaceCraft.Core.Dimensions.Mars;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.MapGenRavine;

import com.EvgenWarGold.SpaceCraft.Util.FastMath;

public class MapGenRavineMars extends MapGenRavine {

    @Override
    protected void func_151540_a(long seed, int chunkX, int chunkZ, Block[] block, double x, double y, double z,
        float p_151540_12_, float p_151540_13_, float p_151540_14_, int p_151540_15_, int p_151540_16_,
        double p_151540_17_) {
        Random rand_ = new Random(seed);
        double d4 = chunkX * 16 + 8;
        double d5 = chunkZ * 16 + 8;
        float f3 = 0.0f;
        float f4 = 0.0f;
        if (p_151540_16_ <= 0) {
            int j1 = this.range * 16 - 16;
            p_151540_16_ = j1 - rand_.nextInt(j1 / 4);
        }
        boolean flag1 = false;
        if (p_151540_15_ == -1) {
            p_151540_15_ = p_151540_16_ / 2;
            flag1 = true;
        }
        float f5 = 1.0f;
        for (int k1 = 0; k1 < 256; ++k1) {
            if (k1 == 0 || rand_.nextInt(3) == 0) {
                f5 = 1.0f + rand_.nextFloat() * rand_.nextFloat() * 1.0f;
            }
            this.field_75046_d[k1] = f5 * f5;
        }
        while (p_151540_15_ < p_151540_16_) {
            double d6 = 1.5 + MathHelper.sin(p_151540_15_ * FastMath.PI / p_151540_16_) * p_151540_12_;
            double d7 = d6 * p_151540_17_;
            d6 *= rand_.nextFloat() * 0.25 + 0.75;
            d7 *= rand_.nextFloat() * 0.25 + 0.75;
            float f6 = MathHelper.cos(p_151540_14_);
            float f7 = MathHelper.sin(p_151540_14_);
            x += MathHelper.cos(p_151540_13_) * f6;
            y += f7;
            z += MathHelper.sin(p_151540_13_) * f6;
            p_151540_14_ *= 0.7f;
            p_151540_14_ += f4 * 0.05f;
            p_151540_13_ += f3 * 0.05f;
            f4 *= 0.8f;
            f3 *= 0.5f;
            f4 += (rand_.nextFloat() - rand_.nextFloat()) * rand_.nextFloat() * 2.0f;
            f3 += (rand_.nextFloat() - rand_.nextFloat()) * rand_.nextFloat() * 4.0f;
            if (flag1 || rand_.nextInt(4) != 0) {
                double d8 = x - d4;
                double d9 = z - d5;
                double d10 = p_151540_16_ - p_151540_15_;
                double d11 = p_151540_12_ + 2.0f + 16.0f;
                if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11) {
                    return;
                }
                if (x >= d4 - 16.0 - d6 * 2.0 && z >= d5 - 16.0 - d6 * 2.0
                    && x <= d4 + 16.0 + d6 * 2.0
                    && z <= d5 + 16.0 + d6 * 2.0) {
                    int i4 = MathHelper.floor_double(x - d6) - chunkX * 16 - 1;
                    int l1 = MathHelper.floor_double(x + d6) - chunkX * 16 + 1;
                    int j2 = MathHelper.floor_double(y - d7) - 1;
                    int i5 = MathHelper.floor_double(y + d7) + 1;
                    int k2 = MathHelper.floor_double(z - d6) - chunkZ * 16 - 1;
                    int j3 = MathHelper.floor_double(z + d6) - chunkZ * 16 + 1;
                    if (i4 < 0) {
                        i4 = 0;
                    }
                    if (l1 > 16) {
                        l1 = 16;
                    }
                    if (j2 < 1) {
                        j2 = 1;
                    }
                    if (i5 > 248) {
                        i5 = 248;
                    }
                    if (k2 < 0) {
                        k2 = 0;
                    }
                    if (j3 > 16) {
                        j3 = 16;
                    }
                    boolean flag2 = false;
                    for (int k3 = i4; !flag2 && k3 < l1; ++k3) {
                        for (int l2 = k2; !flag2 && l2 < j3; ++l2) {
                            for (int i6 = i5 + 1; !flag2 && i6 >= j2 - 1; --i6) {
                                int j4 = (k3 * 16 + l2) * 256 + i6;
                                if (i6 >= 0 && i6 < 256) {
                                    if (this.isOceanBlock(block, j4, k3, i6, l2, chunkX, chunkZ)) {
                                        flag2 = true;
                                    }
                                    if (i6 != j2 - 1 && k3 != i4 && k3 != l1 - 1 && l2 != k2 && l2 != j3 - 1) {
                                        i6 = j2;
                                    }
                                }
                            }
                        }
                    }
                    if (!flag2) {
                        for (int k3 = i4; k3 < l1; ++k3) {
                            double d12 = (k3 + chunkX * 16 + 0.5 - x) / d6;
                            for (int j4 = k2; j4 < j3; ++j4) {
                                double d13 = (j4 + chunkZ * 16 + 0.5 - z) / d6;
                                int k4 = (k3 * 16 + j4) * 256 + i5;
                                boolean flag3 = false;
                                if (d12 * d12 + d13 * d13 < 1.0) {
                                    for (int l3 = i5 - 1; l3 >= j2; --l3) {
                                        double d14 = (l3 + 0.5 - y) / d7;
                                        if ((d12 * d12 + d13 * d13) * this.field_75046_d[l3] + d14 * d14 / 6.0 < 1.0) {
                                            if (this.isTopBlock(block, k4, k3, j4, chunkX, chunkZ)) {
                                                flag3 = true;
                                            }
                                            this.digBlock(block, k4, k3, l3, j4, chunkX, chunkZ, flag3);
                                        }
                                        --k4;
                                    }
                                }
                            }
                        }
                        if (flag1) {
                            break;
                        }
                    }
                }
            }
            ++p_151540_15_;
        }
    }

    private boolean isTopBlock(Block[] data, int index, int x, int z, int chunkX, int chunkZ) {
        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
        return data[index] == biome.topBlock;
    }

    @Override
    protected void digBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
        Block top = biome.topBlock;
        Block filler = biome.fillerBlock;
        Block block = data[index];
        if (block == Blocks.netherrack || block == Blocks.nether_brick || block == filler || block == top) {
            data[index] = null;
            if (foundTop && data[index - 1] == filler) {
                data[index - 1] = top;
            }
            if (data[index - 1] == Blocks.netherrack) {
                data[index - 1] = Blocks.nether_brick;
            }
        }
    }
}
