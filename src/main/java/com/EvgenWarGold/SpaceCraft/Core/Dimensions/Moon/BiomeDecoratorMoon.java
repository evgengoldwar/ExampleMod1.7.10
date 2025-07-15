package com.EvgenWarGold.SpaceCraft.Core.Dimensions.Moon;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;

import com.EvgenWarGold.SpaceCraft.Api.Event.SCCoreEventPopulate;

public class BiomeDecoratorMoon {

    private World worldObj;
    private Random randomGenerator;

    private int chunkX;
    private int chunkZ;

    private final WorldGenerator dirtGen;

    public BiomeDecoratorMoon(BiomeGenBase par1BiomeGenBase) {

        this.dirtGen = new WorldGenMinableMeta(Blocks.obsidian, 32, 3, false, Blocks.obsidian, 0);
    }

    public void decorate(World worldObj, Random rand, int chunkX, int chunkZ) {
        if (this.worldObj != null) {
            throw new RuntimeException("Already decorating!!");
        }
        this.worldObj = worldObj;
        this.randomGenerator = rand;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.generateMoon();
        this.worldObj = null;
        this.randomGenerator = null;
    }

    void genStandardOre1(int amountPerChunk, WorldGenerator worldGenerator, int minY, int maxY) {
        for (int var5 = 0; var5 < amountPerChunk; ++var5) {
            final int var6 = this.chunkX + this.randomGenerator.nextInt(16);
            final int var7 = this.randomGenerator.nextInt(maxY - minY) + minY;
            final int var8 = this.chunkZ + this.randomGenerator.nextInt(16);
            worldGenerator.generate(this.worldObj, this.randomGenerator, var6, var7, var8);
        }
    }

    void generateMoon() {
        MinecraftForge.EVENT_BUS
            .post(new SCCoreEventPopulate.Pre(this.worldObj, this.randomGenerator, this.chunkX, this.chunkZ));
        this.genStandardOre1(20, this.dirtGen, 0, 200);

        MinecraftForge.EVENT_BUS
            .post(new SCCoreEventPopulate.Post(this.worldObj, this.randomGenerator, this.chunkX, this.chunkZ));
    }
}
