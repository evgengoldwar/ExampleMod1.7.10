package com.EvgenWarGold.SpaceCraft.Proxy;

import java.io.File;

import com.EvgenWarGold.SpaceCraft.Api.Client.IPlayerServer;
import com.EvgenWarGold.SpaceCraft.Configs.ConfigManager;
import com.EvgenWarGold.SpaceCraft.Configs.ConfigSpaceID;
import com.EvgenWarGold.SpaceCraft.Core.Dimensions.EnumPlanet;
import com.EvgenWarGold.SpaceCraft.Core.Entity.Player.PlayerServer;
import com.EvgenWarGold.SpaceCraft.Core.Items.SCItems;
import com.EvgenWarGold.SpaceCraft.Util.Constants;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class CommonProxy {
    public IPlayerServer player = new PlayerServer();

    public void preInit(FMLPreInitializationEvent event) {
        // Config
        ConfigManager.initialize(new File(event.getModConfigurationDirectory(), Constants.MAIN_CONFIG_FILE));
        ConfigSpaceID.initialize(new File(event.getModConfigurationDirectory(), Constants.SPACE_ID_CONFIG_FILE));

        SCItems.initItems();
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {
        EnumPlanet.registerAllPlanet();
    }

    public void serverStarting(FMLServerStartingEvent event) {
        EnumPlanet.registerAllPlanetForServer();
    }

    @Mod.EventHandler
    public void unregisterDims(FMLServerStoppedEvent var1) {
        EnumPlanet.unregisterPlanets();
    }

    public World getWorldForID(int dimensionID) {
        final MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (theServer == null) {
            return null;
        }
        return theServer.worldServerForDimension(dimensionID);
    }

    public EntityPlayer getPlayerFromNetHandler(INetHandler handler) {
        if (handler instanceof NetHandlerPlayServer) {
            return ((NetHandlerPlayServer) handler).playerEntity;
        }
        return null;
    }
}
