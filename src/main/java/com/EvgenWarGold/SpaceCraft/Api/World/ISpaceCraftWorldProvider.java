package com.EvgenWarGold.SpaceCraft.Api.World;

import com.EvgenWarGold.SpaceCraft.Api.SpaceSystem.SpaceSystem;

public interface ISpaceCraftWorldProvider {

    float getGravity();

    float getFallDamageModifier();

    SpaceSystem getSpaceSystemBody();
}
