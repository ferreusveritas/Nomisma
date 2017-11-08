package com.ferreusveritas.nomisma;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class NomismaTab extends CreativeTabs {

	private ItemStack stack;
	
	public NomismaTab(String lable) {
		super(lable);
	}

	public void setTabIconItemStack(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public ItemStack getIconItemStack() {
		return stack;
	}
	
	@Override
	public ItemStack getTabIconItem() {
		return stack;
	}

}
