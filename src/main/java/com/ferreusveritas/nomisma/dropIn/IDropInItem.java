package com.ferreusveritas.nomisma.dropIn;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IDropInItem {
	
	boolean canDropItemIn(PlayerEntity player, ItemStack stack, ItemStack incoming, Slot slot);
	
	ItemStack dropItemIn(PlayerEntity player, ItemStack stack, ItemStack incoming, Slot slot);
	
	@OnlyIn(Dist.CLIENT)
	default List<ITextProperties> getDropInTooltip(ItemStack stack) {
		return Collections.singletonList(new TranslationTextComponent("nomisma.right_click_add"));
	}
	
}