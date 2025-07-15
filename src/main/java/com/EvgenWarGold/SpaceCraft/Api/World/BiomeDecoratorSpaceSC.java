package com.EvgenWarGold.SpaceCraft.Api.World;

import net.minecraft.world.World;

public class BiomeDecoratorSpaceSC extends BiomeDecoratorSpace {

    private World world;

    @Override
    protected void setCurrentWorld(World world) {
        this.world = world;
    }

    @Override
    protected World getCurrentWorld() {
        return this.world;
    }

    @Override
    protected void decorate() {}
}
