package com.EvgenWarGold.SpaceCraft.Core.Dimensions.Mars;

import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

import com.EvgenWarGold.SpaceCraft.Api.SpaceSystem.SpaceSystem;
import com.EvgenWarGold.SpaceCraft.Api.Vector.Vector3;
import com.EvgenWarGold.SpaceCraft.Api.World.ISpaceCraftWorldProvider;
import com.EvgenWarGold.SpaceCraft.Api.World.WorldProviderSpace;
import com.EvgenWarGold.SpaceCraft.Core.Client.CloudRenderer;
import com.EvgenWarGold.SpaceCraft.Core.Dimensions.EnumPlanet;

public class WorldProviderMars extends WorldProviderSpace implements ISpaceCraftWorldProvider {

    @Override
    public float getGravity() {
        return 0.058f;
    }

    @Override
    public float getFallDamageModifier() {
        return 0.16f;
    }

    @Override
    public SpaceSystem getSpaceSystemBody() {
        return EnumPlanet.Mars.getPlanet();
    }

    @Override
    public Vector3 getFogColor() {
        return new Vector3(0.0, 0.0, 0.0);
    }

    @Override
    public Vector3 getSkyColor() {
        return new Vector3(0.0, 0.0, 0.0);
    }

    @Override
    public boolean canRainOrSnow() {
        return false;
    }

    @Override
    public boolean hasSunset() {
        return false;
    }

    @Override
    public long getDayLength() {
        return 48000L;
    }

    @Override
    public Class<? extends IChunkProvider> getChunkProviderClass() {
        return ChunkProviderMars.class;
    }

    @Override
    public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
        return WorldChunkManagerMars.class;
    }

    @Override
    public IRenderHandler getCloudRenderer() {
        return new CloudRenderer();
    }

    @Override
    public boolean isSurfaceWorld() {
        return this.worldObj != null && this.worldObj.isRemote;
    }
}
