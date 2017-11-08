package com.ferreusveritas.nomisma;

import java.util.List;
import java.util.Random;

import com.ferreusveritas.nomisma.proxy.CommonProxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = Nomisma.MODID, version = Nomisma.VERSION, dependencies = "after:ThermalFoundation;after:Railcraft")
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
		
		Entity attacker = event.getSource().getEntity();
		
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
		public static void registerBlocks() {
		}
		
		@SubscribeEvent
		public static void registerItems() {
			GameRegistry.register(coinage);
		}

		public static void registerRecipes() {
			
			for(int i = 0; i < ItemCoinage.numTypes - 1; i++){
				
				ItemStack inCoin = new ItemStack(Nomisma.coinage, 1, i);
				
				GameRegistry.addShapelessRecipe(
						new ItemStack(Nomisma.coinage, 1, i + 1),// Output
						new Object[]{ inCoin, inCoin, inCoin, inCoin }// Input
					);
				
				GameRegistry.addShapelessRecipe(
						new ItemStack(Nomisma.coinage, 4, i),// Output
						new Object[] {new ItemStack(Nomisma.coinage, 1, i + 1)}// Input
					);
				
				NuggetRecipe(0, 1, "nuggetIron");
				NuggetRecipe(1, 4, "nuggetIron");
				NuggetRecipe(2, 1, "nuggetCopper");
				NuggetRecipe(3, 4, "nuggetCopper");
				NuggetRecipe(4, 1, "nuggetSilver");
				NuggetRecipe(5, 4, "nuggetSilver");
				NuggetRecipe(6, 1, "nuggetGold");
				NuggetRecipe(7, 4, "nuggetGold");
				NuggetRecipe(8, 1, "nuggetEnderium");
				NuggetRecipe(9, 4, "nuggetEnderium");
				NuggetRecipe(10, 1, "nuggetMithril");
				NuggetRecipe(11, 4, "nuggetMithril");
				
			}
		}

		private static void NuggetRecipe(int metadata, int numoutput, String nuggetName){
			if(OreDictionary.doesOreNameExist(nuggetName)){
				List<ItemStack> ores = OreDictionary.getOres(nuggetName);
				if(!ores.isEmpty()){
					ItemStack nugget = ores.get(0);
					nugget = new ItemStack(nugget.getItem(), numoutput, nugget.getItemDamage());
					ItemStack coin = new ItemStack(coinage, 1, metadata);
					GameRegistry.addSmelting(coin, nugget, 0.0f);
				} 
			}
		}		
		
	}
	
}