package com.EvgenWarGold.SpaceCraft.Core.Mixins.minecraft;

import com.EvgenWarGold.SpaceCraft.Core.Entity.Player.SCEntityClientPlayerMP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerControllerMP.class)
public class PlayerControllerMPMixin {

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    @Final
    private NetHandlerPlayClient netClientHandler;

    /**
     * @author micdoodle8
     * @author SinTh0r4s
     * @author glowredman
     * @reason enable custom Galacticraft dimension behaviour
     */
    @Overwrite
    public EntityClientPlayerMP func_147493_a(World world, StatFileWriter stats) {
        return new SCEntityClientPlayerMP(this.mc, world, this.mc.getSession(), this.netClientHandler, stats);
    }
}
