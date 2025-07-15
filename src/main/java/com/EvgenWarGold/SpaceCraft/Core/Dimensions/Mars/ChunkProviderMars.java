package com.EvgenWarGold.SpaceCraft.Core.Dimensions.Mars;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;

import com.EvgenWarGold.SpaceCraft.Api.Block.BlockMetaPair;
import com.EvgenWarGold.SpaceCraft.Api.World.BiomeDecoratorSpace;
import com.EvgenWarGold.SpaceCraft.Api.World.ChunkProviderSpaceCraters;
import com.EvgenWarGold.SpaceCraft.Api.World.MapGenBaseMeta;
import com.EvgenWarGold.SpaceCraft.Api.World.SCBiomeGenBase;

public class ChunkProviderMars extends ChunkProviderSpaceCraters {

    private MapGenRavineMars ravineGenerator;

    @Override
    protected List<MapGenBaseMeta> getWorldGenerators() {
        return Collections.emptyList();
    }

    public ChunkProviderMars(World world, long seed, boolean mapFeaturesEnabled) {
        super(world, seed, mapFeaturesEnabled);
        this.ravineGenerator = new MapGenRavineMars();
    }

    @Override
    protected BiomeDecoratorSpace getBiomeGenerator() {
        return new BiomeDecoratorMars();
    }

    @Override
    protected BiomeGenBase[] getBiomesForGeneration() {
        return new BiomeGenBase[] { SCBiomeGenBase.SPACE };
    }

    @Override
    public int getCraterProbability() {
        return 0;
    }

    @Override
    protected BiomeGenBase.SpawnListEntry[] getCreatures() {
        return new BiomeGenBase.SpawnListEntry[0];
    }

    @Override
    public double getHeightModifier() {
        return 1.0;
    }

    @Override
    protected BiomeGenBase.SpawnListEntry[] getMonsters() {
        BiomeGenBase.SpawnListEntry skele = new BiomeGenBase.SpawnListEntry(EntityBat.class, 100, 4, 4);
        BiomeGenBase.SpawnListEntry creeper = new BiomeGenBase.SpawnListEntry(EntityPig.class, 100, 4, 4);
        BiomeGenBase.SpawnListEntry spider = new BiomeGenBase.SpawnListEntry(EntityChicken.class, 100, 4, 4);
        BiomeGenBase.SpawnListEntry enderman = new BiomeGenBase.SpawnListEntry(EntityZombie.class, 100, 4, 4);
        return new BiomeGenBase.SpawnListEntry[] { skele, creeper, spider, enderman };
    }

    @Override
    public double getMountainHeightModifier() {
        return 10.0;
    }

    @Override
    protected int getSeaLevel() {
        return 70;
    }

    @Override
    public double getSmallFeatureHeightModifier() {
        return 20.0;
    }

    @Override
    public double getValleyHeightModifier() {
        return 50.0;
    }

    @Override
    public void onChunkProvide(int cX, int cZ, Block[] blocks, byte[] metadata) {
        this.ravineGenerator.func_151539_a(this, this.worldObj, cX, cZ, blocks); // generate
    }

    @Override
    public void onPopulate(IChunkProvider chunkProvider, int chunkX, int chunkZ) {}

    @Override
    public boolean chunkExists(int x, int y) {
        return false;
    }

    @Override
    protected BiomeGenBase.SpawnListEntry[] getWaterCreatures() {
        return new BiomeGenBase.SpawnListEntry[0];
    }

    @Override
    protected BlockMetaPair getGrassBlock() {
        return new BlockMetaPair(Blocks.netherrack, (byte) 0);
    }

    @Override
    protected BlockMetaPair getDirtBlock() {
        return new BlockMetaPair(Blocks.nether_brick, (byte) 0);
    }

    @Override
    protected BlockMetaPair getStoneBlock() {
        return new BlockMetaPair(Blocks.obsidian, (byte) 0);
    }

    @Override
    protected boolean enableBiomeGenBaseBlock() {
        return false;
    }

    @Override
    public String makeString() {
        return "EnceladusLevelSource";
    }
}
