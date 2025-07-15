package com.EvgenWarGold.SpaceCraft.Util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.StatCollector;

import com.EvgenWarGold.SpaceCraft.SpaceCraft;

public class SCCoreUtil {

    public static int nextID = 0;
    public static boolean deobfuscated;

    static {
        try {
            deobfuscated = Launch.classLoader.getClassBytes("net.minecraft.world.World") != null;
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isDeobfuscated() {
        return deobfuscated;
    }

    public static int nextInternalID() {
        SCCoreUtil.nextID++;
        return SCCoreUtil.nextID - 1;
    }

    public static void registerSpaceCraftItem(String key, Item item) {
        SpaceCraft.itemList.put(key, new ItemStack(item));
    }

    public static void registerSpaceCraftItem(String key, Item item, int metadata) {
        SpaceCraft.itemList.put(key, new ItemStack(item, 1, metadata));
    }

    public static void registerSpaceCraftItem(String key, ItemStack stack) {
        SpaceCraft.itemList.put(key, stack);
    }

    public static void registerSpaceCraftBlock(String key, Block block) {
        SpaceCraft.blocksList.put(key, new ItemStack(block));
    }

    public static void registerSpaceCraftBlock(String key, Block block, int metadata) {
        SpaceCraft.blocksList.put(key, new ItemStack(block, 1, metadata));
    }

    public static void registerSpaceCraftBlock(String key, ItemStack stack) {
        SpaceCraft.blocksList.put(key, stack);
    }

    public static String translate(String key) {
        final String result = StatCollector.translateToLocal(key);
        final int comment = result.indexOf('#');
        return comment > 0 ? result.substring(0, comment)
            .trim() : result;
    }
}
