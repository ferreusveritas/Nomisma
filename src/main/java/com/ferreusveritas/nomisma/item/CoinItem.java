package com.ferreusveritas.nomisma.item;

import java.util.Optional;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CoinItem extends Item {
	
	private final int value;
	
	public CoinItem(Properties properties, int value) {
		super(properties);
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static boolean isCoin(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof CoinItem;
	}
	
	public int getValue(ItemStack stack) {
		return isCoin(stack) ? ((CoinItem)stack.getItem()).getValue() * stack.getCount() : 0;
	}
	
	public static Optional<CoinItem> asCoin(ItemStack stack) {
		return CoinItem.isCoin(stack) ? Optional.of((CoinItem)stack.getItem()) : Optional.empty();
	}
	
}
