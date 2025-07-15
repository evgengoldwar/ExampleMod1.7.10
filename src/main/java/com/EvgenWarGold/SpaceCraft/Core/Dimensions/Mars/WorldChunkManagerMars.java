package com.EvgenWarGold.SpaceCraft.Core.Dimensions.Mars;

import net.minecraft.world.biome.BiomeGenBase;

import com.EvgenWarGold.SpaceCraft.Api.World.SCBiomeGenBase;
import com.EvgenWarGold.SpaceCraft.Api.World.WorldChunkManagerSpace;

public class WorldChunkManagerMars extends WorldChunkManagerSpace {

    @Override
    public BiomeGenBase getBiome() {
        return SCBiomeGenBase.SPACE;
    }
}
