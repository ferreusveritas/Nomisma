package com.ferreusveritas.nomisma.recipe;

import java.util.Optional;

import com.ferreusveritas.nomisma.NomismaItems;
import com.ferreusveritas.nomisma.NomismaMod;
import com.ferreusveritas.nomisma.item.CoinItem;
import com.ferreusveritas.nomisma.item.PurseItem;
import com.google.gson.JsonObject;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class CoinPurseExtractRecipe implements ICraftingRecipe {
	
	private final ResourceLocation id;
	
	public CoinPurseExtractRecipe(ResourceLocation idIn) {
		id = idIn;
	}
	
	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public ItemStack getResultItem() {
		return new ItemStack(NomismaItems.getCoin());
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		ItemStack coinPurse = ItemStack.EMPTY;
		int slot = -1;
		
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.isEmpty()) continue;
			Item item = stack.getItem();
			if (item instanceof PurseItem) {
				if(coinPurse.isEmpty()) {
					coinPurse = stack;
					slot = i;
				} else {
					return false;
				}
			}
		}
		
		Optional<PurseItem> purseItem =  PurseItem.asCoinPurse(coinPurse);
		
		if(purseItem.isPresent()) {
			int coinLevel = 0;
			switch(slot) {
				case 0: coinLevel = 0; break;
				case 1: coinLevel = 2; break;
				case 2: coinLevel = 4; break;
				default: return false;
			}
			CoinItem coinItem = NomismaItems.getCoin(coinLevel);
			int coinValue = coinItem.getValue();
			if(purseItem.get().hasCoins(coinPurse, coinValue)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public ItemStack assemble(CraftingInventory inv) {
		ItemStack coinPurse = ItemStack.EMPTY;
		
		int slot = -1;
		
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();
			if (item instanceof PurseItem) {
				slot = i;
				coinPurse = stack;
			}
		}
		
		Optional<PurseItem> purseItem = PurseItem.asCoinPurse(coinPurse);
		
		if (!purseItem.isPresent()) { // Should only happen if the result from matches() gets ignored
			return ItemStack.EMPTY;
		}
		
		int coinLevel = 0;
		switch(slot) {
			case 0: coinLevel = 0; break;
			case 1: coinLevel = 2; break;
			case 2: coinLevel = 4; break;
		}
		CoinItem coinItem = NomismaItems.getCoin(coinLevel);
		int coinValue = coinItem.getValue();
		purseItem.get().hasCoins(coinPurse, coinValue);
		coinPurse.getTag().putInt("craft_remove", coinValue);
		return new ItemStack(coinItem);
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}
	
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return NomismaMod.COIN_PURSE_EXTRACT_RECIPE.get();
	}
	
	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CoinPurseExtractRecipe> {
		@Override
		public CoinPurseExtractRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			return new CoinPurseExtractRecipe(recipeId);
		}
		
		@Override
		public CoinPurseExtractRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
			return new CoinPurseExtractRecipe(recipeId);
		}
		
		@Override
		public void toNetwork(PacketBuffer buffer, CoinPurseExtractRecipe recipe) {
			// TODO Auto-generated method stub
		}
		
	}
	
}