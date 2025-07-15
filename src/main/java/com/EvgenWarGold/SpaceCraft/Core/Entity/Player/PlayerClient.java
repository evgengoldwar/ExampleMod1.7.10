package com.EvgenWarGold.SpaceCraft.Core.Entity.Player;

import com.EvgenWarGold.SpaceCraft.Api.Client.IPlayerClient;
import com.EvgenWarGold.SpaceCraft.Api.Vector.BlockVec3;
import com.EvgenWarGold.SpaceCraft.Api.Vector.Vector3;
import com.EvgenWarGold.SpaceCraft.Api.World.ISpaceCraftWorldProvider;
import com.EvgenWarGold.SpaceCraft.Proxy.ClientProxy;
import com.EvgenWarGold.SpaceCraft.Util.WorldUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;

public class PlayerClient implements IPlayerClient {
    @Override
    public void moveEntity(EntityPlayerSP player, double par1, double par3, double par5) {
        this.updateFeet(player, par1, par5);
    }

    @Override
    public void onUpdate(EntityPlayerSP player) {

    }

    @Override
    public void onLivingUpdatePre(EntityPlayerSP player) {

    }

    @Override
    public void onLivingUpdatePost(EntityPlayerSP player) {

    }

    @Override
    public float getBedOrientationInDegrees(EntityPlayerSP player, float vanillaDegrees) {
        return 0;
    }

    @Override
    public boolean isEntityInsideOpaqueBlock(EntityPlayerSP player, boolean vanillaInside) {
        return false;
    }

    @Override
    public boolean wakeUpPlayer(EntityPlayerSP player, boolean par1, boolean par2, boolean par3) {
        return false;
    }

    @Override
    public void onBuild(int i, EntityPlayerSP player) {

    }

    private void updateFeet(EntityPlayerSP player, double motionX, double motionZ) {
        final double motionSqrd = motionX * motionX + motionZ * motionZ;
        double distanceSinceLastStep = 0.36;
        int lastStep = 0;

        // If the player is on the moon, not airbourne and not riding anything
        if (motionSqrd > 0.001 && player.worldObj != null
            && player.worldObj.provider instanceof ISpaceCraftWorldProvider
            && player.ridingEntity == null
            && !player.capabilities.isFlying) {
            final int iPosX = (int) Math.floor(player.posX);
            final int iPosY = (int) Math.floor(player.posY - 2);
            final int iPosZ = (int) Math.floor(player.posZ);

            // If the block below is the moon block
            // And is the correct metadata (moon turf)
                if (distanceSinceLastStep > 0.35) {
                    Vector3 pos = new Vector3(player);
                    // Set the footprint position to the block below and add random number to stop
                    // z-fighting
                    pos.y = MathHelper.floor_double(player.posY - 1) + player.getRNG().nextFloat() / 100.0F;

                    // Adjust footprint to left or right depending on step count
                    switch (lastStep) {
                        case 0:
                            pos.translate(
                                new Vector3(
                                    Math.sin(Math.toRadians(-player.rotationYaw + 90)) * 0.25,
                                    0,
                                    Math.cos(Math.toRadians(-player.rotationYaw + 90)) * 0.25));
                            break;
                        case 1:
                            pos.translate(
                                new Vector3(
                                    Math.sin(Math.toRadians(-player.rotationYaw - 90)) * 0.25,
                                    0,
                                    Math.cos(Math.toRadians(-player.rotationYaw - 90)) * 0.25));
                            break;
                    }

                    pos = WorldUtil.getFootprintPosition(
                        player.worldObj,
                        player.rotationYaw - 180,
                        pos,
                        new BlockVec3(player));

                    final long chunkKey = ChunkCoordIntPair.chunkXZ2Int(pos.intX() >> 4, pos.intZ() >> 4);
                    ClientProxy.footprintRenderer.addFootprint(
                        chunkKey,
                        player.worldObj.provider.dimensionId,
                        pos,
                        player.rotationYaw,
                        player.getCommandSenderName());

                    // Increment and cap step counter at 1
                    lastStep++;
                    lastStep %= 2;
                    distanceSinceLastStep = 0;
                } else {
                    distanceSinceLastStep += motionSqrd;
                }
        }
    }
}
