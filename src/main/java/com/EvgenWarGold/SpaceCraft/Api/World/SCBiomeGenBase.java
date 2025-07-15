package com.EvgenWarGold.SpaceCraft.Api.World;

import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;

import com.EvgenWarGold.SpaceCraft.Configs.ConfigSpaceID;

public class SCBiomeGenBase extends BiomeGenBase {

    public static final BiomeGenBase SPACE = new BiomeGenSpaceSC(ConfigSpaceID.IDSpaceBiome).setBiomeName("Space");
    public Block stoneBlock;
    public byte topMeta;
    public byte fillerMeta;
    public byte stoneMeta;

    public SCBiomeGenBase(int id) {
        super(id);
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        this.rainfall = 0.0f;
        this.setColor(0xFFE67330);
    }

    @Override
    public float getSpawningChance() {
        return 0.1f;
    }
}
