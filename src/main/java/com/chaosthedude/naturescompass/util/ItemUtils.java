package com.chaosthedude.naturescompass.util;

import com.chaosthedude.naturescompass.NaturesCompass;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemUtils {

	public static boolean verifyNBT(ItemStack stack) {
		if (stack.isEmpty() || stack.getItem() != NaturesCompass.naturesCompass) {
			return false;
		} else if (!stack.hasTag()) {
			stack.setTag(new NBTTagCompound());
		}

		return true;
	}

	public static ItemStack getHeldNatureCompass(EntityPlayer player) {
		return getHeldItem(player, NaturesCompass.naturesCompass);
	}

	public static ItemStack getHeldItem(EntityPlayer player, Item item) {
		if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == item) {
			return player.getHeldItemMainhand();
		} else if (!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() == item) {
			return player.getHeldItemOffhand();
		}

		return ItemStack.EMPTY;
	}

}
