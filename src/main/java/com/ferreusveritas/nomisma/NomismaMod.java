package com.ferreusveritas.nomisma;

import com.ferreusveritas.nomisma.dropIn.MessageDropIn;
import com.ferreusveritas.nomisma.dropIn.MessageDropInCreative;
import com.ferreusveritas.nomisma.dropIn.MessageSetSelectedItem;
import com.ferreusveritas.nomisma.dropIn.NetworkHandler;
import com.ferreusveritas.nomisma.recipe.CoinPurseExtractRecipe;

import net.minecraft.block.Block;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(NomismaMod.MODID)
public class NomismaMod {
	
	public static final String MODID = "nomisma";
	
	public static NetworkHandler network;
	
	private static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
	public static final RegistryObject<CoinPurseExtractRecipe.Serializer> COIN_PURSE_EXTRACT_RECIPE = RECIPE_SERIALIZERS.register("coin_extract", CoinPurseExtractRecipe.Serializer::new);
	
	public NomismaMod() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OptionsHolder.COMMON_SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
		MinecraftForge.EVENT_BUS.register(this);
		NomismaItems.init();
		MobDropManager.init();
	}
	
	public void setup(final FMLCommonSetupEvent event) {
		network = new NetworkHandler(MODID, 1);
		network.register(MessageDropIn.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(MessageDropInCreative.class, NetworkDirection.PLAY_TO_SERVER);
		network.register(MessageSetSelectedItem.class, NetworkDirection.PLAY_TO_CLIENT);
	}
	
	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
			// register a new block here
		}
	}
	
}