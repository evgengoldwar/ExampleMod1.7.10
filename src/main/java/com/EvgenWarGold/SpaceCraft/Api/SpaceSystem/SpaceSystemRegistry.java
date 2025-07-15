package com.EvgenWarGold.SpaceCraft.Api.SpaceSystem;

import java.util.Map;
import java.util.TreeMap;

import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.Event;

public class SpaceSystemRegistry {

    static int maxPlanetID = 0;

    static TreeMap<String, Planet> planets = Maps.newTreeMap();

    static BiMap<String, Integer> planetIDs = HashBiMap.create();

    public static boolean registerPlanet(Planet planet) {
        if (SpaceSystemRegistry.planetIDs.containsKey(planet.getName())) {
            return false;
        }

        SpaceSystemRegistry.planets.put(planet.getName(), planet);
        SpaceSystemRegistry.planetIDs.put(planet.getName(), ++SpaceSystemRegistry.maxPlanetID);

        MinecraftForge.EVENT_BUS.post(new PlanetRegisterEvent(planet.getName(), SpaceSystemRegistry.maxPlanetID));
        return true;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Planet> getRegisteredPlanets() {
        return (Map<String, Planet>) SpaceSystemRegistry.planets.clone();
    }

    public static Map<String, Integer> getRegisteredPlanetIDs() {
        return ImmutableMap.copyOf(SpaceSystemRegistry.planetIDs);
    }

    public static int getPlanetID(String planetName) {
        return SpaceSystemRegistry.planetIDs.get(planetName);
    }

    public static class PlanetRegisterEvent extends Event {

        public final String planetName;
        public final int planetID;

        public PlanetRegisterEvent(String planetName, int planetID) {
            this.planetName = planetName;
            this.planetID = planetID;
        }
    }
}
