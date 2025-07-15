package com.EvgenWarGold.SpaceCraft.Core.Dimensions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

import com.EvgenWarGold.SpaceCraft.Api.SpaceSystem.Planet;
import com.EvgenWarGold.SpaceCraft.Api.SpaceSystem.SpaceSystem;
import com.EvgenWarGold.SpaceCraft.Api.SpaceSystem.SpaceSystemRegistry;
import com.EvgenWarGold.SpaceCraft.Api.World.ISpaceCraftWorldProvider;
import com.EvgenWarGold.SpaceCraft.Configs.ConfigManager;
import com.EvgenWarGold.SpaceCraft.Configs.ConfigSpaceID;
import com.EvgenWarGold.SpaceCraft.Core.Dimensions.Mars.WorldProviderMars;
import com.EvgenWarGold.SpaceCraft.Core.Dimensions.Moon.WorldProviderMoon;
import com.EvgenWarGold.SpaceCraft.Util.Constants;
import com.EvgenWarGold.SpaceCraft.Util.SCLog;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;

public enum EnumPlanet {

    Moon("Moon", new Planet("Moon"), ConfigSpaceID.IDMoon, WorldProviderMoon.class),
    Mars("Mars", new Planet("Mars"), ConfigSpaceID.IDMars, WorldProviderMars.class);

    private final String planetName;
    private final Planet planet;
    private final int planetId;
    private final Class<? extends WorldProvider> providerClass;
    private static final List<Integer> worldProviderIDs = new ArrayList<>();
    public static List<Integer> registeredPlanets;
    public static Map<Integer, String> dimNames = new TreeMap<>();

    EnumPlanet(String planetName, Planet planet, int planetId, Class<? extends WorldProvider> providerClass) {
        this.planetName = planetName;
        this.planet = planet;
        this.planetId = planetId;
        this.providerClass = providerClass;
    }

    public String getPlanetName() {
        return planetName;
    }

    public Planet getPlanet() {
        return planet;
    }

    public int getPlanetId() {
        return planetId;
    }

    public Class<? extends WorldProvider> getProviderClass() {
        return providerClass;
    }

    public static void registerAllPlanet() {
        for (EnumPlanet enumPlanet : EnumPlanet.values()) {
            registerPlanet(enumPlanet);
        }
        final ArrayList<SpaceSystem> spaceSystems = new ArrayList<>();

        spaceSystems.addAll(
            SpaceSystemRegistry.getRegisteredPlanets()
                .values());

        for (final SpaceSystem body : spaceSystems) {
            final int id = Arrays.binarySearch(ConfigManager.staticLoadDimensions, body.getDimensionID());
            if (!registerProvider(
                body.getDimensionID(),
                body.getWorldProvider(),
                body.getForceStaticLoad() || id < 0,
                0)) {
                body.setUnreachable();
            }
        }
    }

    public static void registerAllPlanetForServer() {
        initialiseDimensionNames();

        final ArrayList<SpaceSystem> cBodyList = new ArrayList<>();
        cBodyList.addAll(
            SpaceSystemRegistry.getRegisteredPlanets()
                .values());

        for (final SpaceSystem body : cBodyList) {
            if (body.shouldAutoRegister() && !registerPlanet(body.getDimensionID(), body.getReachable(), 0)) {
                body.setUnreachable();
            }
        }
    }

    public static void registerPlanet(EnumPlanet enumPlanet) {
        Planet newPlanet;
        newPlanet = enumPlanet.getPlanet();
        newPlanet.setDimensionInfo(enumPlanet.getPlanetId(), enumPlanet.getProviderClass());
        SpaceSystemRegistry.registerPlanet(enumPlanet.getPlanet());
    }

    public static boolean registerProvider(int id, Class<? extends WorldProvider> provider, boolean keepLoaded,
        int defaultID) {
        final boolean flag = DimensionManager.registerProviderType(id, provider, keepLoaded);
        if (flag) {
            worldProviderIDs.add(id);
            return true;
        }
        worldProviderIDs.add(defaultID);
        FMLLog.severe(
            "Could not register dimension " + id + " - does it clash with another mod?  Change the ID in config.");
        return false;
    }

    public static boolean registerPlanet(int planetID, boolean initialiseDimensionAtServerInit, int defaultID) {
        if (registeredPlanets == null) {
            registeredPlanets = new ArrayList<>();
        }

        if (initialiseDimensionAtServerInit) {
            if (DimensionManager.isDimensionRegistered(planetID)) {
                SCLog.severe(
                    "Dimension already registered to another mod: unable to register planet dimension " + planetID);
                registeredPlanets.add(defaultID);
                return false;
            }
            DimensionManager.registerDimension(planetID, planetID);
            SCLog.info("Registered Dimension: " + planetID);
            registeredPlanets.add(planetID);
            final World w = FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .worldServerForDimension(planetID);
            dimNames.put(planetID, getDimensionName(w.provider));
            return true;
        }
        registeredPlanets.add(planetID);
        return true;
    }

    public static String getDimensionName(WorldProvider wp) {
        if (wp instanceof ISpaceCraftWorldProvider) {
            final SpaceSystem cb = ((ISpaceCraftWorldProvider) wp).getSpaceSystemBody();
            if (cb != null) {
                return cb.getUnlocalizedName();
            }
        }

        if (wp.dimensionId == Constants.OVERWORLD_ID) {
            return "Overworld";
        }

        return wp.getDimensionName();
    }

    public static void initialiseDimensionNames() {
        final WorldProvider provider = getProviderForDimensionServer(Constants.OVERWORLD_ID);
        dimNames.put(Integer.valueOf(Constants.OVERWORLD_ID), provider.getDimensionName());
    }

    public static WorldProvider getProviderForDimensionServer(int id) {
        final MinecraftServer theServer = FMLCommonHandler.instance()
            .getMinecraftServerInstance();
        if (theServer == null) {
            SCLog.debug("Called WorldUtil server side method but FML returned no server - is this a bug?");
            return null;
        }
        final World ws = theServer.worldServerForDimension(id);
        if (ws != null) {
            return ws.provider;
        }
        return null;
    }

    public static void unregisterPlanets() {
        if (registeredPlanets != null) {
            for (final Integer var1 : registeredPlanets) {
                if (DimensionManager.isDimensionRegistered(var1)) {
                    DimensionManager.unregisterDimension(var1);
                    SCLog.info("Unregistered Dimension: " + var1);
                } else {
                    SCLog.info("Unregistered Dimension: " + var1 + " - already unregistered");
                }
            }

            registeredPlanets = null;
        }
        dimNames.clear();
    }
}
