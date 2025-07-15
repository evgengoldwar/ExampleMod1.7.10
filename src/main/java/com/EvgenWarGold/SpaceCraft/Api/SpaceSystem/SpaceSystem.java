package com.EvgenWarGold.SpaceCraft.Api.SpaceSystem;

import java.util.Locale;

import net.minecraft.util.StatCollector;
import net.minecraft.world.WorldProvider;

public abstract class SpaceSystem {

    protected final String bodyName;
    protected String unlocalizedName;
    protected int dimensionID = 0;
    protected boolean autoRegisterDimension = false;
    protected boolean isReachable = false;
    protected boolean forceStaticLoad = true;

    protected Class<? extends WorldProvider> providerClass;

    public SpaceSystem(String unlocalizedPlanetName) {
        this.bodyName = unlocalizedPlanetName.toLowerCase(Locale.ENGLISH);
        this.unlocalizedName = unlocalizedPlanetName;
    }

    public abstract int getID();

    public abstract String getUnlocalizedNamePrefix();

    public String getUnlocalizedName() {
        return this.getUnlocalizedNamePrefix() + "." + this.unlocalizedName;
    }

    public String getName() {
        return this.bodyName;
    }

    public String getLocalizedName() {
        String s = this.getUnlocalizedName();
        s = s == null ? "" : StatCollector.translateToLocal(s);
        final int comment = s.indexOf('#');
        return comment > 0 ? s.substring(0, comment)
            .trim() : s;
    }

    public SpaceSystem setDimensionInfo(int dimId, Class<? extends WorldProvider> providerClass) {
        return this.setDimensionInfo(dimId, providerClass, true);
    }

    public SpaceSystem setDimensionInfo(int providerId, Class<? extends WorldProvider> providerClass,
        boolean autoRegister) {
        this.dimensionID = providerId;
        this.providerClass = providerClass;
        this.autoRegisterDimension = autoRegister;
        this.isReachable = true;
        return this;
    }

    public boolean getReachable() {
        return this.isReachable;
    }

    public boolean getForceStaticLoad() {
        return this.forceStaticLoad;
    }

    public SpaceSystem setForceStaticLoad(boolean force) {
        this.forceStaticLoad = force;
        return this;
    }

    public boolean shouldAutoRegister() {
        return this.autoRegisterDimension;
    }

    public int getDimensionID() {
        return this.dimensionID;
    }

    public Class<? extends WorldProvider> getWorldProvider() {
        return this.providerClass;
    }

    public void setUnreachable() {
        this.isReachable = false;
    }
}
