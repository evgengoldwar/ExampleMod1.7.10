package com.EvgenWarGold.SpaceCraft.Api.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.EvgenWarGold.SpaceCraft.Api.Vector.Vector3;
import com.EvgenWarGold.SpaceCraft.Util.SCCoreUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class WorldProviderSpace extends WorldProvider implements ISpaceCraftWorldProvider {

    long timeCurrentOffset = 0L;
    long preTickTime = Long.MIN_VALUE;
    private long saveTCO = 0L;
    static Field tickCounter;

    static {
        try {
            tickCounter = VillageCollection.class
                .getDeclaredField(SCCoreUtil.isDeobfuscated() ? "tickCounter" : "field_75553_e");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The fog color in this dimension
     */
    public abstract Vector3 getFogColor();

    /**
     * The sky color in this dimension
     */
    public abstract Vector3 getSkyColor();

    /**
     * Whether there will be rain or snow in this dimension
     */
    public abstract boolean canRainOrSnow();

    /**
     * Whether or not to render vanilla sunset (can be overridden with custom sky provider)
     */
    public abstract boolean hasSunset();

    /**
     * The length of day in this dimension
     * <p/>
     * Default: 24000
     */
    public abstract long getDayLength();

    public abstract Class<? extends IChunkProvider> getChunkProviderClass();

    public abstract Class<? extends WorldChunkManager> getWorldChunkManagerClass();

    @Override
    public void setDimension(int var1) {
        this.dimensionId = var1;
        super.setDimension(var1);
    }

    @Override
    public String getDimensionName() {
        return this.getSpaceSystemBody()
            .getLocalizedName();
    }

    @Override
    public void updateWeather() {
        if (!this.worldObj.isRemote) {
            final long newTime = this.worldObj.getWorldInfo()
                .getWorldTime();
            if (this.preTickTime == Long.MIN_VALUE) {
                // First tick: get the timeCurrentOffset from saved ticks in villages.dat :)
                int savedTick = 0;
                try {
                    tickCounter.setAccessible(true);
                    savedTick = tickCounter.getInt(this.worldObj.villageCollectionObj);
                    if (savedTick < 0) {
                        savedTick = 0;
                    }
                } catch (final Exception ignore) {}
                this.timeCurrentOffset = savedTick - newTime;
            } else {
                // Detect jumps in world time (e.g. because of bed use on Overworld) and reverse
                // them for this world
                final long diff = newTime - this.preTickTime;
                if (diff > 1L) {
                    this.timeCurrentOffset -= diff - 1L;
                    this.saveTime();
                }
            }
            this.preTickTime = newTime;
            this.saveTCO = 0L;
        }

        if (this.canRainOrSnow()) {
            super.updateWeather();
        } else {
            this.worldObj.getWorldInfo()
                .setRainTime(0);
            this.worldObj.getWorldInfo()
                .setRaining(false);
            this.worldObj.getWorldInfo()
                .setThunderTime(0);
            this.worldObj.getWorldInfo()
                .setThundering(false);
            this.worldObj.rainingStrength = 0.0F;
            this.worldObj.thunderingStrength = 0.0F;
        }
    }

    @Override
    public String getSaveFolder() {
        return "DIM" + this.getSpaceSystemBody()
            .getDimensionID();
    }

    @Override
    public String getWelcomeMessage() {
        return "Entering " + this.getSpaceSystemBody()
            .getLocalizedName();
    }

    @Override
    public String getDepartMessage() {
        return "Leaving " + this.getSpaceSystemBody()
            .getLocalizedName();
    }

    @Override
    public boolean canBlockFreeze(int x, int y, int z, boolean byWater) {
        return this.canRainOrSnow();
    }

    @Override
    public boolean canDoLightning(Chunk chunk) {
        return this.canRainOrSnow();
    }

    @Override
    public boolean canDoRainSnowIce(Chunk chunk) {
        return this.canRainOrSnow();
    }

    @Override
    public float[] calcSunriseSunsetColors(float var1, float var2) {
        return this.hasSunset() ? super.calcSunriseSunsetColors(var1, var2) : null;
    }

    @Override
    public float calculateCelestialAngle(long par1, float par3) {
        par1 = this.worldObj.getWorldInfo()
            .getWorldTime() + this.timeCurrentOffset;
        final int j = (int) (par1 % this.getDayLength());
        float f1 = (j + par3) / this.getDayLength() - 0.25F;

        if (f1 < 0.0F) {
            ++f1;
        }

        if (f1 > 1.0F) {
            --f1;
        }

        final float f2 = f1;
        f1 = 0.5F - MathHelper.cos(f1 * (float) Math.PI) / 2.0F;
        return f2 + (f1 - f2) / 3.0F;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3 getFogColor(float var1, float var2) {
        final Vector3 fogColor = this.getFogColor();
        return Vec3.createVectorHelper(fogColor.floatX(), fogColor.floatY(), fogColor.floatZ());
    }

    @Override
    public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {
        final Vector3 skyColor = this.getSkyColor();
        return Vec3.createVectorHelper(skyColor.floatX(), skyColor.floatY(), skyColor.floatZ());
    }

    @Override
    public boolean isSkyColored() {
        return true;
    }

    /**
     * Do not override this.
     * <p>
     * Returns true on clients (to allow rendering of sky etc, maybe even clouds). Returns false on servers (to disable
     * Nether Portal mob spawning and sleeping in beds).
     */
    @Override
    public boolean isSurfaceWorld() {
        return this.worldObj != null && this.worldObj.isRemote;
    }

    /**
     * This must normally return false, so that if the dimension is set for 'static' loading it will not keep chunks
     * around the dimension spawn position permanently loaded. It is also needed to be false so that the 'Force
     * Overworld Respawn' setting in core.conf will work correctly - see also WorldProviderS[ace.getRespawnDimension().
     * <p>
     * But: returning 'false' will cause beds to explode in this dimension. If you want beds NOT to explode, you can
     * override this, like in WorldProviderMoon.
     */
    @Override
    public boolean canRespawnHere() {
        return false;
    }

    /**
     * Do NOT override this in your add-ons.
     * <p>
     * This controls whether the player will respawn in the space dimension or the Overworld in accordance with the
     * 'Force Overworld Respawn' setting on core.conf.
     */
    @Override
    public int getRespawnDimension(EntityPlayerMP player) {
        return this.shouldForceRespawn() ? this.dimensionId : 0;
    }

    /**
     * If true, the player should respawn in this dimension upon death.
     * <p>
     * Obeying the 'Force Overworld Respawn' setting from core.conf is an important protection for players are endlessly
     * dying in a space dimension: for example respawning in an airless environment with no oxygen tanks and no oxygen
     * machinery.
     */
    public boolean shouldForceRespawn() {
        return true;
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        try {
            final Class<? extends IChunkProvider> chunkProviderClass = this.getChunkProviderClass();

            final Constructor<?>[] constructors = chunkProviderClass.getConstructors();
            for (final Constructor<?> constr : constructors) {
                if (Arrays
                    .equals(constr.getParameterTypes(), new Object[] { World.class, long.class, boolean.class })) {
                    return (IChunkProvider) constr.newInstance(
                        this.worldObj,
                        this.worldObj.getSeed(),
                        this.worldObj.getWorldInfo()
                            .isMapFeaturesEnabled());
                }
                if (constr.getParameterTypes().length == 0) {
                    return (IChunkProvider) constr.newInstance();
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void registerWorldChunkManager() {
        if (this.getWorldChunkManagerClass() == null) {
            super.registerWorldChunkManager();
        } else {
            try {
                final Class<? extends WorldChunkManager> chunkManagerClass = this.getWorldChunkManagerClass();

                final Constructor<?>[] constructors = chunkManagerClass.getConstructors();
                for (final Constructor<?> constr : constructors) {
                    if (Arrays.equals(constr.getParameterTypes(), new Object[] { World.class })) {
                        this.worldChunkMgr = (WorldChunkManager) constr.newInstance(this.worldObj);
                    } else if (constr.getParameterTypes().length == 0) {
                        this.worldChunkMgr = (WorldChunkManager) constr.newInstance();
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean shouldMapSpin(String entity, double x, double y, double z) {
        return false;
    }

    @Override
    public void setWorldTime(long time) {
        this.worldObj.getWorldInfo()
            .setWorldTime(time);
        long diff = -this.timeCurrentOffset;
        this.timeCurrentOffset = time - this.worldObj.getWorldInfo()
            .getWorldTime();
        diff += this.timeCurrentOffset;
        if (diff != 0L) {
            this.saveTime();
            this.preTickTime = time;
        }
        this.saveTCO = 0L;
    }

    @Override
    public long getWorldTime() {
        return this.worldObj.getWorldInfo()
            .getWorldTime() + this.timeCurrentOffset;
    }

    public void setWorldTimeCommand(long time) {
        this.worldObj.getWorldInfo()
            .setWorldTime(time);
        this.timeCurrentOffset = this.saveTCO;
        this.saveTime();
        this.preTickTime = time;
        this.saveTCO = 0L;
    }

    public long getWorldTimeCommand() {
        this.saveTCO = this.timeCurrentOffset;
        return this.worldObj.getWorldInfo()
            .getWorldTime() + this.timeCurrentOffset;
    }

    /**
     * Adjust time offset on Galacticraft worlds when the Overworld time jumps and you don't want the time on all the
     * other Galacticraft worlds to jump also - see WorldUtil.setNextMorning() for example
     */
    public void adjustTimeOffset(long diff) {
        this.timeCurrentOffset -= diff;
        this.preTickTime += diff;
        if (diff != 0L) {
            this.saveTime();
        }
    }

    /**
     * Save this world's custom time (from timeCurrentOffset) into this world's villages.dat :)
     */
    private void saveTime() {
        try {
            final VillageCollection vc = this.worldObj.villageCollectionObj;
            tickCounter.setAccessible(true);
            tickCounter.setInt(vc, (int) this.getWorldTime());
            vc.markDirty();
        } catch (final Exception ignore) {}
    }
}
