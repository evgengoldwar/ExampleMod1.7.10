package com.EvgenWarGold.SpaceCraft.Core.Dimensions.Moon;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

import com.EvgenWarGold.SpaceCraft.Api.SpaceSystem.SpaceSystem;
import com.EvgenWarGold.SpaceCraft.Api.Vector.Vector3;
import com.EvgenWarGold.SpaceCraft.Api.World.ISpaceCraftWorldProvider;
import com.EvgenWarGold.SpaceCraft.Api.World.WorldProviderSpace;
import com.EvgenWarGold.SpaceCraft.Core.Client.CloudRenderer;
import com.EvgenWarGold.SpaceCraft.Core.Dimensions.EnumPlanet;
import com.EvgenWarGold.SpaceCraft.Util.FastMath;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WorldProviderMoon extends WorldProviderSpace implements ISpaceCraftWorldProvider {

    @Override
    public Vector3 getFogColor() {
        return new Vector3(0, 0, 0);
    }

    @Override
    public Vector3 getSkyColor() {
        return new Vector3(0, 0, 0);
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
        return 192000L;
    }

    @Override
    public boolean shouldForceRespawn() {
        return false;
    }

    @Override
    public Class<? extends IChunkProvider> getChunkProviderClass() {
        return ChunkProviderMoon.class;
    }

    @Override
    public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
        return WorldChunkManagerMoon.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(float partialTicks) {
        float celestialAngle = this.worldObj.getCelestialAngle(partialTicks);
        float f = 1.0f - (MathHelper.cos(celestialAngle * FastMath.TWO_PI) * 2.0f + 0.25f);
        if (f < 0.0f) {
            f = 0.0f;
        }
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f * f * 0.5f + 0.3f;
    }

    @Override
    public boolean isSkyColored() {
        return false;
    }

    @Override
    public double getHorizon() {
        return 44.0D;
    }

    @Override
    public int getAverageGroundLevel() {
        return 68;
    }

    @Override
    public boolean canCoordinateBeSpawn(int var1, int var2) {
        return true;
    }

    @Override
    public boolean isSurfaceWorld() {
        return this.worldObj != null && this.worldObj.isRemote;
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player) {
        return this.shouldForceRespawn() ? this.dimensionId : 0;
    }

    @Override
    public float getGravity() {
        return 0.062F;
    }

    @Override
    public float getFallDamageModifier() {
        return 0.18F;
    }

    @Override
    public SpaceSystem getSpaceSystemBody() {
        return EnumPlanet.Moon.getPlanet();
    }

    @Override
    public IRenderHandler getCloudRenderer() {
        return new CloudRenderer();
    }
}
