package com.EvgenWarGold.SpaceCraft;

import java.util.HashMap;

import com.EvgenWarGold.SpaceCraft.Api.Vector.BlockVec3;
import com.EvgenWarGold.SpaceCraft.Core.Network.SpaceCraftChannelHandler;
import com.EvgenWarGold.SpaceCraft.Core.Tick.TickHandlerServer;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import net.minecraft.item.ItemStack;

import com.EvgenWarGold.SpaceCraft.Core.Command.CommandDimTp;
import com.EvgenWarGold.SpaceCraft.Proxy.CommonProxy;
import com.EvgenWarGold.SpaceCraft.Util.Constants;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = Constants.MOD_ID,
    version = Tags.VERSION,
    name = Constants.MOD_NAME,
    acceptedMinecraftVersions = "[1.7.10]")
public class SpaceCraft {
    public static SpaceCraftChannelHandler packetPipeline;

    public static HashMap<String, ItemStack> itemList = new HashMap<>();
    public static HashMap<String, ItemStack> blocksList = new HashMap<>();
    @SidedProxy(
        clientSide = "com.EvgenWarGold.SpaceCraft.Proxy.ClientProxy",
        serverSide = "com.EvgenWarGold.SpaceCraft.Proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        packetPipeline = SpaceCraftChannelHandler.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDimTp());
    }

    @Mod.EventHandler
    public void serverInit(FMLServerStartedEvent event) {
        TickHandlerServer.restart();
        BlockVec3.chunkCacheDim = Integer.MAX_VALUE;
    }
}
