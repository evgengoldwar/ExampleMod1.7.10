package com.EvgenWarGold.SpaceCraft.Core.Items;

import net.minecraft.item.Item;

import com.EvgenWarGold.SpaceCraft.Util.Constants;
import com.EvgenWarGold.SpaceCraft.Util.SCCoreUtil;

import cpw.mods.fml.common.registry.GameRegistry;

public class SCItems {

    public static Item moonKey;
    public static String moonKeyString = "moon_key";

    public static void initItems() {
        SCItems.moonKey = new ItemMoonKey(moonKeyString);

        registerSpaceCraftItems();
        registerItems();
    }

    public static void registerSpaceCraftItems() {
        SCCoreUtil.registerSpaceCraftItem(moonKeyString, SCItems.moonKey);
    }

    public static void registerItems() {
        SCItems.registerItem(SCItems.moonKey);
    }

    public static void registerItem(Item item) {
        GameRegistry.registerItem(item, item.getUnlocalizedName(), Constants.MOD_ID);
    }
}
