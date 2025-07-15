package com.EvgenWarGold.SpaceCraft.Core.Mixins;

public enum TargetMod {

    VANILLA("Minecraft", null),
    FORGE("Minecraft Forge", "net.minecraftforge.classloading.FMLForgePlugin", "Forge"),
    PLAYERAPI("Player API", "api.player.forge.PlayerAPIPlugin", "PlayerAPI"),
    OPTIFINE("Optifine", "optifine.OptiFineForgeTweaker", "Optifine");

    public final String modName;
    public final String coreModClass;
    public final String modId;

    TargetMod(String modName, String coreModClass) {
        this(modName, coreModClass, null);
    }

    TargetMod(String modName, String coreModClass, String modId) {
        this.modName = modName;
        this.coreModClass = coreModClass;
        this.modId = modId;
    }

    @Override
    public String toString() {
        return "TargetedMod{modName='" + this.modName
            + "', coreModClass='"
            + this.coreModClass
            + "', modId='"
            + this.modId
            + "'}";
    }
}
