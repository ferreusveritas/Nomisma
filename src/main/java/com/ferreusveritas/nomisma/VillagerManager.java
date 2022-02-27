package com.ferreusveritas.nomisma;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class VillagerManager {
	
	private static LazyOptional<Item> coinGetter = LazyOptional.of(() -> NomismaItems.getCoin(OptionsHolder.getEmeraldCoinLevel()));
	
	private static ItemStack getCoins(int num) {
		Item coin = coinGetter.resolve().get();
		return new ItemStack(coin, num);
	}
	
	private static ItemStack emeraldFilter(ItemStack input) {
		if(input.getItem() == Items.EMERALD) {
			int count = input.getCount();
			return getCoins(count);
		}
		return input;
	}
	
	public static class MerchantOfferWrapper extends MerchantOffer {
		private MerchantOffer offer;
		
		public MerchantOfferWrapper(MerchantOffer offer) {
			super(new CompoundNBT());
			this.offer = offer;
		}
		@Override public ItemStack getBaseCostA() { return emeraldFilter(offer.getBaseCostA()); }
		@Override public ItemStack getCostA() { return emeraldFilter(offer.getCostA()); }
		@Override public ItemStack getCostB() { return emeraldFilter(offer.getCostB()); }
		@Override public ItemStack getResult() { return emeraldFilter(offer.getResult()); }
		@Override public void updateDemand() { offer.updateDemand(); }
		@Override public ItemStack assemble() { return emeraldFilter(offer.assemble()); }
		@Override public int getUses() { return offer.getUses(); }
		@Override public void resetUses() { offer.resetUses(); }
		@Override public int getMaxUses() { return offer.getMaxUses(); }
		@Override public void increaseUses() { offer.increaseUses(); }
		@Override public int getDemand() { return offer.getDemand(); }
		@Override public void addToSpecialPriceDiff(int price) { offer.addToSpecialPriceDiff(price); }
		@Override public void resetSpecialPriceDiff() { offer.resetSpecialPriceDiff(); }
		@Override public int getSpecialPriceDiff() { return offer.getSpecialPriceDiff(); }
		@Override public void setSpecialPriceDiff(int price) { offer.setSpecialPriceDiff(price); }
		@Override public float getPriceMultiplier() { return offer.getPriceMultiplier(); }
		@Override public int getXp() { return offer.getXp(); }
		@Override public boolean isOutOfStock() { return offer.isOutOfStock(); }
		@Override public void setToOutOfStock() { offer.setToOutOfStock(); }
		@Override public boolean needsRestock() { return offer.needsRestock(); }
		@Override public boolean shouldRewardExp() { return offer.shouldRewardExp(); }
		
		@Override
		public CompoundNBT createTag() {
			CompoundNBT tag = offer.createTag();
			tag.put("buy", getBaseCostA().save(new CompoundNBT()));
			tag.put("sell", getResult().save(new CompoundNBT()));
			tag.put("buyB", getCostB().save(new CompoundNBT()));
			return tag;
		}
		
		@Override
		public boolean satisfiedBy(ItemStack item1, ItemStack item2) {
			return isRequiredItem(item1, getCostA()) && item1.getCount() >= getCostA().getCount() && isRequiredItem(item2, getCostB()) && item2.getCount() >= getCostB().getCount();
		}
		
		protected static boolean isRequiredItem(ItemStack item1, ItemStack item2) {
			if (item2.isEmpty() && item1.isEmpty()) {
				return true;
			} else {
				ItemStack itemstack = item1.copy();
				if (itemstack.getItem().isDamageable(itemstack)) {
					itemstack.setDamageValue(itemstack.getDamageValue());
				}
				return ItemStack.isSame(itemstack, item2) && (!item2.hasTag() || itemstack.hasTag() && NBTUtil.compareNbt(item2.getTag(), itemstack.getTag(), false));
			}
		}
		
		@Override public boolean take(ItemStack item1, ItemStack item2) {
			if (!satisfiedBy(item1, item2)) {
				return false;
			} else {
				item1.shrink(getCostA().getCount());
				if (!getCostB().isEmpty()) {
					item2.shrink(getCostB().getCount());
				}
				return true;
			}
		}
	}
	
	public static class TradeWrapper implements VillagerTrades.ITrade {
		private ITrade trade;
		
		public TradeWrapper(ITrade trade) {
			this.trade = trade;
		}
		
		@Override
		public MerchantOffer getOffer(Entity entity, Random random) {
			return new MerchantOfferWrapper(trade.getOffer(entity, random));
		}
	}
	
	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void onVillagerTradesEvent(final VillagerTradesEvent villagerTradesEvent) {
		if(!OptionsHolder.COMMON.enableVillagerEmeraldReplacement.get()) {
			return;
		}
		
		Int2ObjectMap<List<ITrade>> trades = villagerTradesEvent.getTrades();
		
		for( Entry<Integer, List<ITrade>> tradeEntry : trades.entrySet()) {
			List<ITrade> tradeList = tradeEntry.getValue();
			List<ITrade> newTradeList = new ArrayList<>();
			
			for( ITrade trade : tradeList ) {
				if(trade instanceof TradeWrapper) {
					newTradeList.add(trade);
				} else {
					newTradeList.add(new TradeWrapper(trade));
				}
			}
			
			tradeList.clear();
			tradeList.addAll(newTradeList);
		}
	}
	
}
