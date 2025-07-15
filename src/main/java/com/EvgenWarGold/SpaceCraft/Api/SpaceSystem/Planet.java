package com.EvgenWarGold.SpaceCraft.Api.SpaceSystem;

public class Planet extends SpaceSystem {

    public Planet(String planetName) {
        super(planetName);
    }

    @Override
    public int getID() {
        return SpaceSystemRegistry.getPlanetID(this.bodyName);
    }

    @Override
    public String getUnlocalizedNamePrefix() {
        return "planet";
    }
}
