package com.EvgenWarGold.SpaceCraft.Configs;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.EvgenWarGold.SpaceCraft.Util.SCLog;

public class ConfigSpaceID {

    static Configuration config;

    public static int IDSpaceBiome = 200;
    public static int IDMoon = 500;
    public static int IDMars = 501;
    public static int IDSaturn = 502;
    public static int IDUran = 503;
    public static int IDSun = 504;

    public static void initialize(File file) {
        ConfigSpaceID.config = new Configuration(file);
        ConfigSpaceID.syncConfig(true);
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

            prop = config.get("general", "IDSpaceBiome", IDSpaceBiome);
            prop.comment = "Global ID Biome for space";
            IDSpaceBiome = prop.getInt();
            propOrder.add(prop.getName());

            prop = config.get("general", "IDMoon", IDMoon);
            prop.comment = "Global ID for Moon";
            IDMoon = prop.getInt();
            propOrder.add(prop.getName());

            prop = config.get("general", "IDMars", IDMars);
            prop.comment = "Global ID for Mars";
            IDMars = prop.getInt();
            propOrder.add(prop.getName());

            prop = config.get("general", "IDSaturn", IDSaturn);
            prop.comment = "Global ID for Saturn";
            IDSaturn = prop.getInt();
            propOrder.add(prop.getName());

            prop = config.get("general", "IDUran", IDUran);
            prop.comment = "Global ID for Uran";
            IDUran = prop.getInt();
            propOrder.add(prop.getName());

            prop = config.get("general", "IDSun", IDSun);
            prop.comment = "Global ID for Sun";
            IDSun = prop.getInt();
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
