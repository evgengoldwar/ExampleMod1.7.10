package com.EvgenWarGold.SpaceCraft.Core.Entity.Player;

import com.EvgenWarGold.SpaceCraft.Proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.Session;
import net.minecraft.world.World;

public class SCEntityClientPlayerMP extends EntityClientPlayerMP {
    public SCEntityClientPlayerMP(Minecraft minecraft, World world, Session session, NetHandlerPlayClient netHandler,
                                  StatFileWriter statFileWriter) {
        super(minecraft, world, session, netHandler, statFileWriter);
    }

    @Override
    public void onLivingUpdate() {
        ClientProxy.playerClientHandler.onLivingUpdatePre(this);
        super.onLivingUpdate();
        ClientProxy.playerClientHandler.onLivingUpdatePost(this);
    }

    @Override
    public void moveEntity(double par1, double par3, double par5) {
        super.moveEntity(par1, par3, par5);
        ClientProxy.playerClientHandler.moveEntity(this, par1, par3, par5);
    }

    @Override
    public void onUpdate() {
        ClientProxy.playerClientHandler.onUpdate(this);
        super.onUpdate();
    }
}
