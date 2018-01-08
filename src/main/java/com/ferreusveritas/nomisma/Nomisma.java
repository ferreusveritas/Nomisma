package com.ferreusveritas.nomisma;

import java.util.Random;

import com.ferreusveritas.nomisma.proxy.CommonProxy;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid = Nomisma.MODID, version = Nomisma.VERSION, dependencies = "after:thermalfoundation;after:railcraft")
public class Nomisma
{

	@SidedProxy(clientSide = "com.ferreusveritas.nomisma.proxy.ClientProxy", serverSide = "com.ferreusveritas.nomisma.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static final String MODID = "nomisma";
	public static final String VERSION = "0.1.1";

	@Instance(MODID)
	public static Nomisma instance;

	public static Item coinage;
		
	public static final NomismaTab tabNomisma = new NomismaTab("tabNomisma");
	
	public Random rand = new Random();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		coinage = new ItemCoinage();
		MinecraftForge.EVENT_BUS.register(this);
		
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}
	
	@EventHandler
	public void PostInit(FMLPostInitializationEvent e) {
		tabNomisma.setTabIconItemStack(new ItemStack(coinage, 1, 3));
	}
	
	@SubscribeEvent
	public void onEntityDrop(LivingDropsEvent event) {
		
		int loot = event.getLootingLevel();
		
		Entity attacker = event.getSource().getTrueSource();
		
		if(!(event.isRecentlyHit() && (event.getSource().damageType.equals("arrow") || (event.getSource().damageType.equals("player") && attacker.getEyeHeight() > 1.0f)))){
			return;//Stop Mob Grinders and Turtles from producing loot
		}
		
		EntityLivingBase entity = event.getEntityLiving();
		
		if(entity instanceof EntityZombie) {
			EntityZombie zombie = (EntityZombie) entity;
			if(zombie.isChild()){
				return;//Children don't have money
			}
			if (zombie instanceof EntityZombieVillager) {
				if(rand.nextInt(100) < 75 + loot * 5){//Same chances as a Villager
					event.getEntityLiving().entityDropItem(new ItemStack(coinage, rand.nextInt(2) + 1, 0), 1.0f);
				}
			} else {
				if(rand.nextInt(100) < 12 + loot * 3){
					event.getEntityLiving().entityDropItem(new ItemStack(coinage, (rand.nextInt(4) / 3) + 1, 0), 1.0f);
				}
			}
		}

		if(entity instanceof EntitySkeleton){
			if(rand.nextInt(100) < 8 + loot * 2){//Skeletons are less likely to have loot because they have nothing to carry it in(rotten or no clothes)
				entity.entityDropItem(new ItemStack(coinage, (rand.nextInt(4) / 3) + 1, 0), 1.0f);
			}
		}
		
		if(entity instanceof EntityVillager){
			if(entity.isChild()){
				return;//Children don't have money
			}
			if(rand.nextInt(100) < 75 + loot * 5){
				entity.entityDropItem(new ItemStack(coinage, rand.nextInt(2) + 1, 0), 1.0f);
			}
		}
		
		if(entity instanceof EntityWitch){
			if(rand.nextInt(100) < 25 + loot * 5){
				entity.entityDropItem(new ItemStack(coinage, rand.nextInt(3) + 1, 0), 1.0f);
			}
		}
		
	}

	///////////////////////////////////////////
	// REGISTRATION
	///////////////////////////////////////////
	
	@Mod.EventBusSubscriber(modid = Nomisma.MODID)
	public static class RegistrationHandler {

		@SubscribeEvent
		public static void registerBlocks(final RegistryEvent.Register<Block> event) {
			//final IForgeRegistry<Block> registry = event.getRegistry();
		}
		
		@SubscribeEvent
		public static void registerItems(final RegistryEvent.Register<Item> event) {
			final IForgeRegistry<Item> registry = event.getRegistry();

			registry.register(coinage);
		}

		@SubscribeEvent(priority = EventPriority.LOWEST)
		public static void registerRecipes(final RegistryEvent.Register<IRecipe> event) {
			//final IForgeRegistry<IRecipe> registry = event.getRegistry();
			
			String nuggets[] = new String[] {"nuggetIron", "nuggetCopper", "nuggetSilver", "nuggetGold", "nuggetEnderium", "nuggetMithril"};
			
			for(int i = 0; i < ItemCoinage.numTypes - 1; i++){
				
				Ingredient inCoin = Ingredient.fromStacks(new ItemStack(Nomisma.coinage, 1, i));
				
				GameRegistry.addShapelessRecipe(
						new ResourceLocation(Nomisma.MODID, "coinageUp_" + i),
						null,// Group
						new ItemStack(Nomisma.coinage, 1, i + 1),// Output
						new Ingredient[]{ inCoin, inCoin, inCoin, inCoin }// Input
					);
				
				GameRegistry.addShapelessRecipe(
						new ResourceLocation(Nomisma.MODID, "coinageDown_" + i),
						null,// Group
						new ItemStack(Nomisma.coinage, 4, i),// Output
						new Ingredient[]{ Ingredient.fromStacks(new ItemStack(Nomisma.coinage, 1, i + 1)) }// Input
					);
				
				NuggetRecipe(i, (i & 1) == 0 ? 1 : 4, nuggets[i / 2]);
			}
			
		}

		private static void NuggetRecipe(int metadata, int numoutput, String nuggetName){
			if(OreDictionary.doesOreNameExist(nuggetName)){
				NonNullList<ItemStack> ores = OreDictionary.getOres(nuggetName);
				if(!ores.isEmpty()){
					ItemStack nugget = ores.get(0);
					nugget = new ItemStack(nugget.getItem(), numoutput, nugget.getItemDamage());
					ItemStack coin = new ItemStack(coinage, numoutput, metadata);
					GameRegistry.addSmelting(coin, nugget, 0.0f);
				} 
			}
		}		
		
	}
	
}