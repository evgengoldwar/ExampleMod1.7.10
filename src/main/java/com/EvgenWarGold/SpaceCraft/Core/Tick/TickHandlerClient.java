package com.EvgenWarGold.SpaceCraft.Core.Tick;

import com.EvgenWarGold.SpaceCraft.Api.Client.Footprint;
import com.EvgenWarGold.SpaceCraft.Proxy.ClientProxy;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldProviderSurface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class TickHandlerClient {
    private static long tickCount;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        final Minecraft minecraft = FMLClientHandler.instance().getClient();
        final WorldClient world = minecraft.theWorld;
        final EntityClientPlayerMP player = minecraft.thePlayer;

        if (event.phase == TickEvent.Phase.START) {
            if (TickHandlerClient.tickCount >= Long.MAX_VALUE) {
                TickHandlerClient.tickCount = 0;
            }

            TickHandlerClient.tickCount++;

            if (TickHandlerClient.tickCount % 20 == 0) {
                for (final List<Footprint> fpList : ClientProxy.footprintRenderer.footprints.values()) {
                    final Iterator<Footprint> fpIt = fpList.iterator();
                    while (fpIt.hasNext()) {
                        final Footprint fp = fpIt.next();
                        fp.age += 20;

                        if (fp.age >= Footprint.MAX_AGE) {
                            fpIt.remove();
                        }
                    }
                }
            }
        }
    }
}
