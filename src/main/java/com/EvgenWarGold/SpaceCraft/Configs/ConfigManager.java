package com.EvgenWarGold.SpaceCraft.Configs;

import static com.EvgenWarGold.SpaceCraft.Util.Constants.CONFIG_CATEGORY_GENERAL;
import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.EvgenWarGold.SpaceCraft.Util.SCLog;

public class ConfigManager {

    public static int[] staticLoadDimensions = {};
    public static boolean enableDebug;

    static Configuration config;

    public static void initialize(File file) {
        ConfigManager.config = new Configuration(file);
        ConfigManager.syncConfig(true);
    }

    public static void forceSave() {
        ConfigManager.config.save();
    }

    public static void syncConfig(boolean load) {
        final List<String> propOrder = new ArrayList<>();

        try {
            Property prop;
            if (!config.isChild && load) {
                config.load();
            }

            // Configs region

            prop = config.get(CONFIG_CATEGORY_GENERAL, "Enable Debug Message", false);
            prop.comment = "If this is enabled, debug messages will appear in the console. This is useful for finding bugs in the mod.";
            enableDebug = prop.getBoolean(false);
            propOrder.add(prop.getName());

            // End region

            config.setCategoryPropertyOrder(CATEGORY_GENERAL, propOrder);
            if (config.hasChanged()) {
                config.save();
            }
        } catch (final Exception e) {
            SCLog.severe("Problem loading config");
        }
    }
}
