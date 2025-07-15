package com.EvgenWarGold.SpaceCraft.Core.Dimensions.Mars;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import com.EvgenWarGold.SpaceCraft.Api.World.BiomeDecoratorSpaceSC;

public class BiomeDecoratorMars extends BiomeDecoratorSpaceSC {

    @Override
    protected void decorate() {
        World world = this.getCurrentWorld();
        for (int i = 0; i < 5; ++i) {
            int randPosX = this.chunkX + 8 + this.rand.nextInt(16);
            int randPosY = this.rand.nextInt(80);
            int randPosZ = this.chunkZ + 8 + this.rand.nextInt(16);
            if (world.getBlock(randPosX, randPosY - 1, randPosZ) == Blocks.netherrack
                && world.getBlockMetadata(randPosX, randPosY - 1, randPosZ) == 0
                && world.isAirBlock(randPosX, randPosY, randPosZ)) {
                world.setBlock(randPosX, randPosY, randPosZ, Blocks.flowing_lava);
            }
        }
    }
}
