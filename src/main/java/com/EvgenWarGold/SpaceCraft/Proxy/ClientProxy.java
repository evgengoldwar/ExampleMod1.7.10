package com.EvgenWarGold.SpaceCraft.Proxy;

import com.EvgenWarGold.SpaceCraft.Api.Client.IPlayerClient;
import com.EvgenWarGold.SpaceCraft.Core.Client.FootprintRenderer;
import com.EvgenWarGold.SpaceCraft.Core.Entity.Player.PlayerClient;
import com.EvgenWarGold.SpaceCraft.Core.Tick.TickHandlerClient;
import com.EvgenWarGold.SpaceCraft.SpaceCraft;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        ClientProxy.registerHandlers();
    }

    public static IPlayerClient playerClientHandler = new PlayerClient();
    public static Minecraft mc = FMLClientHandler.instance().getClient();
    public static FootprintRenderer footprintRenderer = new FootprintRenderer();

    public static void renderFootprints(float partialTicks) {
        ClientProxy.footprintRenderer.renderFootprints(ClientProxy.mc.thePlayer, partialTicks);
        MinecraftForge.EVENT_BUS.post(new EventSpecialRender(partialTicks));
    }

    public static class EventSpecialRender extends Event {

        public final float partialTicks;

        public EventSpecialRender(float partialTicks) {
            this.partialTicks = partialTicks;
        }
    }

    public static void registerHandlers() {
        final TickHandlerClient tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register(tickHandlerClient);
        MinecraftForge.EVENT_BUS.register(tickHandlerClient);
        MinecraftForge.EVENT_BUS.register(SpaceCraft.proxy);
    }
}
