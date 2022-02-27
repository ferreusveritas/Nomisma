package com.ferreusveritas.nomisma.item;

import java.util.List;
import java.util.Optional;

import com.ferreusveritas.nomisma.OptionsHolder;
import com.ferreusveritas.nomisma.dropIn.IDropInItem;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class PurseItem extends Item implements IDropInItem {
	
	public PurseItem(Properties properties) {
		super(properties);
	}
	
	public int getCoins(ItemStack purse) {
		if(PurseItem.isCoinPurse(purse)) {
			if(!purse.hasTag()) {
				purse.setTag(new CompoundNBT());
			}
			CompoundNBT tag = purse.getTag();
			int coins = tag.getInt("coins");
			return coins < 0 ? 0 : coins;
		}
		return 0;
	}
	
	public void setCoins(ItemStack purse, int coins) {
		if(PurseItem.isCoinPurse(purse)) {
			if(!purse.hasTag()) {
				purse.setTag(new CompoundNBT());
			}
			CompoundNBT tag = purse.getTag();
			tag.putInt("coins", coins < 0 ? 0 : coins);
		}
	}
	
	public int addCoins(ItemStack purse, int value) {
		int currCoins = getCoins(purse);
		int newCoins = currCoins + value;
		if(value > 0) {
			if(newCoins < 0) {
				newCoins = Integer.MAX_VALUE;//Overflow
			}
		}
		else {
			if(newCoins < 0) {
				newCoins = 0;//Attempt to remove more coins than are available
			}
		}
		
		setCoins(purse, newCoins);
		return newCoins - currCoins;
	}
	
	public Optional<Integer> getValue(ItemStack coins) {
		Optional<Integer> asCoins;
		
		asCoins = CoinItem.asCoin(coins).map( c -> c.getValue() * coins.getCount());
		if(asCoins.isPresent()) {
			return asCoins;
		}
		
		asCoins = asCoinPurse(coins).map(p -> p.getCoins(coins));
		if(asCoins.isPresent()) {
			return asCoins;
		}
		
		asCoins = coins.getItem() == Items.EMERALD ? Optional.of(coins.getCount() * OptionsHolder.getEmeraldValue()) : Optional.empty();
		if(asCoins.isPresent()) {
			return asCoins;
		}
		
		return Optional.empty();
	}
	
	public int addCoins(ItemStack purse, ItemStack coins) {
		return getValue(coins)
			.map(value -> asCoinPurse(purse).map(target -> target.addCoins(purse, value)).orElse(0))
			.orElse(0);
	}
	
	public int addCoinsFromPurse(ItemStack purse, ItemStack droppingPurse) {
		return asCoinPurse(droppingPurse)
			.map(dropPurse -> dropPurse.getCoins(droppingPurse))
			.map(value -> asCoinPurse(purse).map(target -> target.addCoins(purse, value)).orElse(0))
			.orElse(0);
	}
	
	public int remCoins(ItemStack purse, int value) {
		return -addCoins(purse, -value);
	}
	
	public boolean hasCoins(ItemStack purse, int value) {
		return getCoins(purse) >= value;
	}
	
	public static boolean isCoinPurse(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof PurseItem;
	}
	
	public static Optional<PurseItem> asCoinPurse(ItemStack stack) {
		return PurseItem.isCoinPurse(stack) ? Optional.of((PurseItem)stack.getItem()) : Optional.empty();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.appendHoverText(stack, world, list, flag);
		list.add(new TranslationTextComponent("nomisma.coins").append(new StringTextComponent(": " + getCoins(stack))));
	}
	
	private boolean emeraldTest(ItemStack stack) {
		return OptionsHolder.COMMON.enableEmeraldPurseConversion.get() && (stack.getItem() == Items.EMERALD);
	}
	
	@Override
	public boolean canDropItemIn(PlayerEntity player, ItemStack stack, ItemStack incoming, Slot slot) {
		return (CoinItem.isCoin(incoming) || PurseItem.isCoinPurse(incoming) || emeraldTest(incoming)) && PurseItem.isCoinPurse(stack) && slot.mayPickup(player);
	}
	
	@Override
	public ItemStack dropItemIn(PlayerEntity player, ItemStack stack, ItemStack incoming, Slot slot) {
		ItemStack ret = tryAddToCoinPurse(player, stack, incoming, slot);
		return ret == null ? stack : ret;
	}
	
	private ItemStack tryAddToCoinPurse(PlayerEntity player, ItemStack coinPurse, ItemStack coins, Slot slot) {
		Optional<PurseItem> purseItem = PurseItem.asCoinPurse(coinPurse);
		if (purseItem.isPresent() && slot.mayPickup(player)) {
			purseItem.get().addCoins(coinPurse, coins);
			coins.setCount(0);
			return coinPurse;
		}
		
		return null;
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		CompoundNBT tag = itemStack.getTag();
		int remove = tag.getInt("craft_remove");
		tag.remove("craft_remove");
		remCoins(itemStack, remove);
		return itemStack.copy();
	}
	
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
	
}
