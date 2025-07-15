package com.EvgenWarGold.SpaceCraft.Core.Entity.Player;

import com.EvgenWarGold.SpaceCraft.Api.Client.Footprint;
import com.EvgenWarGold.SpaceCraft.Api.Vector.BlockVec3;
import com.EvgenWarGold.SpaceCraft.Api.Vector.Vector3;
import com.EvgenWarGold.SpaceCraft.Core.Network.PacketSimple;
import com.EvgenWarGold.SpaceCraft.Core.Tick.TickHandlerServer;
import com.EvgenWarGold.SpaceCraft.SpaceCraft;
import com.EvgenWarGold.SpaceCraft.Util.WorldUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;

import java.lang.ref.WeakReference;

public class SCPlayerHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.onPlayerLogin((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.onPlayerLogout();
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.onPlayerRespawn((EntityPlayerMP) event.player);
        }
    }

    private void onPlayerLogin(EntityPlayerMP player) {
        SpaceCraft.packetPipeline
            .sendTo(new PacketSimple(PacketSimple.EnumSimplePacket.C_GET_SPACE_SYSTEM_BODY_LIST, new Object[] {}), player);
    }

    private void onPlayerLogout() {}

    private void onPlayerRespawn(EntityPlayerMP player) {}

    protected static void updateFeet(EntityPlayerMP player, double motionX, double motionZ) {
        final double motionSqrd = motionX * motionX + motionZ * motionZ;
        int lastStep = 0;
        double distanceSinceLastStep = 0;
        if (motionSqrd > 0.001D && !player.capabilities.isFlying) {
            final int iPosX = MathHelper.floor_double(player.posX);
            final int iPosY = MathHelper.floor_double(player.posY) - 1;
            final int iPosZ = MathHelper.floor_double(player.posZ);


            // If the block below is the moon block
            // And is the correct metadata (moon turf)
                // If it has been long enough since the last step
                if (distanceSinceLastStep > 0.35D) {
                    Vector3 pos = new Vector3(player);
                    // Set the footprint position to the block below and add random number to stop
                    // z-fighting
                    pos.y = MathHelper.floor_double(player.posY - 1D) + player.worldObj.rand.nextFloat() / 100.0F;

                    // Adjust footprint to left or right depending on step count
                    switch (lastStep) {
                        case 0:
                            float a = (-player.rotationYaw + 90F) / (180F / (float) Math.PI);
                            pos.translate(new Vector3(MathHelper.sin(a) * 0.25F, 0, MathHelper.cos(a) * 0.25F));
                            break;
                        case 1:
                            a = (-player.rotationYaw - 90F) / (180F / (float) Math.PI);
                            pos.translate(new Vector3(MathHelper.sin(a) * 0.25, 0, MathHelper.cos(a) * 0.25));
                            break;
                    }

                    final float rotation = player.rotationYaw - 180;
                    pos = WorldUtil.getFootprintPosition(player.worldObj, rotation, pos, new BlockVec3(player));

                    final long chunkKey = ChunkCoordIntPair.chunkXZ2Int(pos.intX() >> 4, pos.intZ() >> 4);
                    TickHandlerServer.addFootprint(
                        chunkKey,
                        new Footprint(
                            player.worldObj.provider.dimensionId,
                            pos,
                            rotation,
                            player.getCommandSenderName()),
                        player.worldObj.provider.dimensionId);

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
