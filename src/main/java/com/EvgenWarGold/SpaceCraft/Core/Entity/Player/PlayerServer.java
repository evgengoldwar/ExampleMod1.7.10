package com.EvgenWarGold.SpaceCraft.Core.Entity.Player;

import com.EvgenWarGold.SpaceCraft.Api.Client.IPlayerServer;
import com.EvgenWarGold.SpaceCraft.Api.World.ISpaceCraftWorldProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;

public class PlayerServer implements IPlayerServer {
    @Override
    public void clonePlayer(EntityPlayerMP player, EntityPlayer oldPlayer, boolean keepInv) {

    }

    @Override
    public void updateRiddenPre(EntityPlayerMP player) {

    }

    @Override
    public void updateRiddenPost(EntityPlayerMP player) {

    }

    @Override
    public boolean mountEntity(EntityPlayerMP player, Entity par1Entity) {
        return false;
    }

    @Override
    public void moveEntity(EntityPlayerMP player, double par1, double par3, double par5) {
        if (player.worldObj.provider instanceof ISpaceCraftWorldProvider && !player.worldObj.isRemote
            && player.ridingEntity == null) {
            SCPlayerHandler.updateFeet(player, par1, par5);
        }
    }

    @Override
    public boolean wakeUpPlayer(EntityPlayerMP player, boolean par1, boolean par2, boolean par3) {
        return false;
    }

    @Override
    public float attackEntityFrom(EntityPlayerMP player, DamageSource par1DamageSource, float par2) {
        return 0;
    }

    @Override
    public void knockBack(EntityPlayerMP player, Entity p_70653_1_, float p_70653_2_, double impulseX, double impulseZ) {

    }
}
