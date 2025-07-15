package com.EvgenWarGold.SpaceCraft.Util;

import static com.EvgenWarGold.SpaceCraft.Util.Constants.MOD_NAME;

import org.apache.logging.log4j.Level;

import com.EvgenWarGold.SpaceCraft.Configs.ConfigManager;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

public class SCLog {

    public static void info(String message) {
        FMLRelaunchLog.log(MOD_NAME, Level.INFO, message);
    }

    public static void severe(String message) {
        FMLRelaunchLog.log(MOD_NAME, Level.ERROR, message);
    }

    public static void debug(String message) {
        if (ConfigManager.enableDebug) {
            FMLRelaunchLog.log(MOD_NAME, Level.INFO, "Debug: " + message);
        }
    }

    public static void exception(Exception e) {
        FMLRelaunchLog.log(MOD_NAME, Level.ERROR, e.getMessage());
    }
}
