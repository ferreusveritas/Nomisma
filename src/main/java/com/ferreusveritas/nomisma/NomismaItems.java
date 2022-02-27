package com.ferreusveritas.nomisma;

import com.ferreusveritas.nomisma.item.CoinItem;
import com.ferreusveritas.nomisma.item.PurseItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class NomismaItems {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NomismaMod.MODID);
	
	public static void init() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	private static LazyOptional<Item> coinGetter = LazyOptional.of(() -> getCoin(0));
	private static LazyOptional<Item> purseGetter = LazyOptional.of(() -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(NomismaMod.MODID, "purse")));
	
	public static Item getCoin() {
		return coinGetter.resolve().get();
	}
	
	public static Item getPurse() {
		return purseGetter.resolve().get();
	}
	
	public static CoinItem getCoin(int level) {
		return (CoinItem) ForgeRegistries.ITEMS.getValue(new ResourceLocation(NomismaMod.MODID, "coin_" + level));
	}
	
	static {
		//iron			4 ^ 0 = 1
		//iron pile		4 ^ 1 = 4
		//copper		4 ^ 2 = 16
		//copper pile	4 ^ 3 = 64
		//gold			4 ^ 4 = 256
		//gold pile		4 ^ 5 = 1024
		//gold pike stk 4 ^ 5 * 64 = 65536
		
		for(int i = 0; i < 6; i++) {
			final int value = (int) Math.pow(4, i);
			ITEMS.register("coin_" + i, () -> new CoinItem(new Item.Properties().tab(ItemGroup.TAB_MATERIALS), value));
		}
		
		ITEMS.register("purse", () -> new PurseItem(new Item.Properties().tab(ItemGroup.TAB_MATERIALS).stacksTo(1)));
	}
	
}
