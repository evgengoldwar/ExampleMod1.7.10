package com.EvgenWarGold.SpaceCraft.Core.Items;

import java.util.ArrayList;
import java.util.stream.Collectors;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.EvgenWarGold.SpaceCraft.Api.SpaceSystem.SpaceSystem;
import com.EvgenWarGold.SpaceCraft.Api.SpaceSystem.SpaceSystemRegistry;
import com.EvgenWarGold.SpaceCraft.Util.Constants;
import com.EvgenWarGold.SpaceCraft.Util.SCLog;

public class ItemMoonKey extends Item {

    public ItemMoonKey(String assetName) {
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setUnlocalizedName(assetName);
        this.setTextureName(Constants.TEXTURE_PREFIX + assetName);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return CreativeTabs.tabCombat;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName() + "." + stack.getItemDamage();
    }

    @Override
    public EnumRarity getRarity(ItemStack p_77613_1_) {
        return EnumRarity.epic;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            final ArrayList<SpaceSystem> cBodyList = new ArrayList<>();
            cBodyList.addAll(
                SpaceSystemRegistry.getRegisteredPlanets()
                    .values());

            String result = cBodyList.stream()
                .map(spaceSystem -> spaceSystem.getDimensionID() + ": " + spaceSystem.getName())
                .collect(Collectors.joining(", "));

            if (cBodyList != null) {
                SCLog.info(result);
            } else SCLog.info("Solar System is empty");
        }
        return stack;
    }
}
