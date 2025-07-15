package com.EvgenWarGold.SpaceCraft.Api.Event;

import java.util.Random;

import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.Event;

public class SCCoreEventPopulate extends Event {

    public final World worldObj;
    public final Random rand;
    public final int chunkX;
    public final int chunkZ;

    public SCCoreEventPopulate(World worldObj, Random rand, int chunkX, int chunkZ) {
        this.worldObj = worldObj;
        this.rand = rand;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public static class Pre extends SCCoreEventPopulate {

        public Pre(World world, Random rand, int worldX, int worldZ) {
            super(world, rand, worldX, worldZ);
        }
    }

    public static class Post extends SCCoreEventPopulate {

        public Post(World world, Random rand, int worldX, int worldZ) {
            super(world, rand, worldX, worldZ);
        }
    }
}
