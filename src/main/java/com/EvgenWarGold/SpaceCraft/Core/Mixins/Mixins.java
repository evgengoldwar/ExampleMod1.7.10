package com.EvgenWarGold.SpaceCraft.Core.Mixins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

public enum Mixins {

    // spotless:off
    RENDER_FOOTPRINTS(new Builder("Render footprints")
        .setPhase(Phase.EARLY)
        .setSide(Side.CLIENT)
        .addMixinClasses("minecraft.FootPrintsRendererMixin")
        .addTargetedMod(TargetMod.VANILLA)),
    REPLACE_ENTITY_CLIENT_PLAYER_MP(new Builder("Replace EntityClientPlayerMP with GCEntityClientPlayerMP")
        .setPhase(Phase.EARLY).setSide(Side.CLIENT).addMixinClasses("minecraft.PlayerControllerMPMixin")
        .addTargetedMod(TargetMod.VANILLA));
    //spotless:on

    public final String name;
    public final List<String> mixinClasses;
    private final Supplier<Boolean> applyIf;
    public final Phase phase;
    private final Side side;
    public final List<TargetMod> targetedMods;
    public final List<TargetMod> excludedMods;

    private static class Builder {

        private final String name;
        private final List<String> mixinClasses = new ArrayList<>();
        private final Supplier<Boolean> applyIf = () -> true;
        private Side side = Side.BOTH;
        private Phase phase = Phase.LATE;
        private final List<TargetMod> targetedMods = new ArrayList<>();
        private final List<TargetMod> excludedMods = new ArrayList<>();

        public Builder(String name) {
            this.name = name;
        }

        public Builder addMixinClasses(String... mixinClasses) {
            this.mixinClasses.addAll(Arrays.asList(mixinClasses));
            return this;
        }

        public Builder setPhase(Phase phase) {
            this.phase = phase;
            return this;
        }

        public Builder setSide(Side side) {
            this.side = side;
            return this;
        }

        public Builder addTargetedMod(TargetMod mod) {
            this.targetedMods.add(mod);
            return this;
        }

        public Builder addExcludedMod(TargetMod mod) {
            this.excludedMods.add(mod);
            return this;
        }
    }

    Mixins(Builder builder) {
        this.name = builder.name;
        this.mixinClasses = builder.mixinClasses;
        this.applyIf = builder.applyIf;
        this.side = builder.side;
        this.targetedMods = builder.targetedMods;
        this.excludedMods = builder.excludedMods;
        this.phase = builder.phase;
        if (this.targetedMods.isEmpty()) {
            throw new RuntimeException("No targeted mods specified for " + this.name);
        }
        if (this.applyIf == null) {
            throw new RuntimeException("No ApplyIf function specified for " + this.name);
        }
    }

    private boolean shouldLoadSide() {
        return this.side == Side.BOTH || this.side == Side.SERVER && FMLLaunchHandler.side()
            .isServer()
            || this.side == Side.CLIENT && FMLLaunchHandler.side()
                .isClient();
    }

    private boolean allModsLoaded(List<TargetMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) {
            return false;
        }

        for (final TargetMod target : targetedMods) {
            if (target == TargetMod.VANILLA) {
                continue;
            }

            // Check coremod first
            if (!loadedCoreMods.isEmpty() && target.coreModClass != null
                && !loadedCoreMods.contains(target.coreModClass)) {
                return false;
            }
            if (!loadedMods.isEmpty() && target.modId != null && !loadedMods.contains(target.modId)) {
                return false;
            }
        }

        return true;
    }

    private boolean noModsLoaded(List<TargetMod> targetedMods, Set<String> loadedCoreMods, Set<String> loadedMods) {
        if (targetedMods.isEmpty()) {
            return true;
        }

        for (final TargetMod target : targetedMods) {
            if (target == TargetMod.VANILLA) {
                continue;
            }

            // Check coremod first
            if (!loadedCoreMods.isEmpty() && target.coreModClass != null
                && loadedCoreMods.contains(target.coreModClass)) {
                return false;
            }
            if (!loadedMods.isEmpty() && target.modId != null && loadedMods.contains(target.modId)) {
                return false;
            }
        }

        return true;
    }

    public boolean shouldLoad(Set<String> loadedCoreMods, Set<String> loadedMods) {
        return this.shouldLoadSide() && this.applyIf.get()
            && this.allModsLoaded(this.targetedMods, loadedCoreMods, loadedMods)
            && this.noModsLoaded(this.excludedMods, loadedCoreMods, loadedMods);
    }

    enum Side {
        BOTH,
        CLIENT,
        SERVER
    }

    public enum Phase {
        EARLY,
        LATE,
    }
}
