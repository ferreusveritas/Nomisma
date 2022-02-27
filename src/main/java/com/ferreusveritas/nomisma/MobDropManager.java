package com.ferreusveritas.nomisma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import com.ferreusveritas.nomisma.item.CoinItem;
import com.ferreusveritas.nomisma.item.PurseItem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class MobDropManager {
	
	private static class CheeseRect {
		private int x1;
		private int z1;
		private int x2;
		private int z2;
		private int hitPoints = cheeseSquareHitPoints;
		
		public CheeseRect(int x1, int z1, int x2, int z2) {
			this.x1 = x1;
			this.z1 = z1;
			this.x2 = x2;
			this.z2 = z2;
		}
		
		@SuppressWarnings("unused")
		public boolean isInside(int x, int z) {
			return isInside(x, z, 0);
		}
		
		public boolean isInside(int x, int z, int grow) {
			return x >= (x1 - grow) && x < (x2 + grow) && z >= (z1 - grow) && z < (z2 + grow);
		}
		
		public boolean hit() {
			return --hitPoints <= 0;
		}
		
		@Override
		public String toString() {
			return new StringBuilder("CheeseRect: {")
				.append("x1:").append(x1).append(",")
				.append("z1:").append(z1).append(",")
				.append("x2:").append(x2).append(",")
				.append("z2:").append(z2).append("}")
				.toString();
		}
		
	}
	
	private static boolean enableAntiCheese;
	private static int numCheeseSquares;// Total number of anti-cheese rectangles in the area
	private static ArrayList<CheeseRect> cheeseRects;
	private static int cheeseSquareHitPoints;// Number of times a rect can have coin drops before it moves
	private static int cheeseAreaWidth;// Width of the entire system in blocks.
	private static int cheeseSquareWidth;// Width of a single anti-cheese hotspot in blocks
	private static int cheeseRange;
	
	private static boolean enablePurseAutomaticCoinPickup;
	private static boolean enableMobCoinDrops;
	
	@SubscribeEvent
	public static void onServerStartedEvent(final FMLServerStartedEvent serverStartedEvent) {
		OptionsHolder.Common options = OptionsHolder.COMMON;
		enableAntiCheese = options.enableAntiCheese.get();
		if(enableAntiCheese) {
			numCheeseSquares = options.numCheeseSquares.get();// Total number of anti-cheese rectangles in the area
			cheeseRects = new ArrayList<>(numCheeseSquares);
			cheeseSquareHitPoints = options.cheeseSquareHitPoints.get();// Number of times a rect can have coin drops before it moves
			cheeseAreaWidth = options.cheeseAreaWidth.get();;// Width of the entire system in blocks.
			cheeseSquareWidth = options.cheeseSquareWidth.get();;// Width of a single anti-cheese hotspot in blocks
			cheeseRange = cheeseAreaWidth - cheeseSquareWidth;
		}
		
		enablePurseAutomaticCoinPickup = options.enablePurseAutomaticCoinPickup.get();
		enableMobCoinDrops = options.enableMobCoinDrops.get();
	}
	
	
	public static boolean isUncheesed(Random rand, BlockPos pos, int lootLevel) {
		if(!enableAntiCheese) {
			return true;
		}
		
		int x = pos.getX() % cheeseAreaWidth;
		int z = pos.getZ() % cheeseAreaWidth;
		
		for(int i = 0; i < numCheeseSquares; i++) {
			CheeseRect cheeseRect = cheeseRects.get(i);
			if( cheeseRect.isInside(x, z, lootLevel * 2) ) {
				if(cheeseRect.hit()) {
					//Move the Cheese!
					int newX = rand.nextInt(cheeseRange);
					int newZ = rand.nextInt(cheeseRange);
					cheeseRects.set(i, new CheeseRect(newX, newZ, newX + cheeseSquareWidth, newZ + cheeseSquareWidth));
				}
				return true;
			}
		}
		
		return false;
	}
	
	private static HashMap<Class<? extends LivingEntity>, Function<Integer, Integer>> dropMap;
	
	public static void init() {
		//Setup drop handlers
		dropMap = new HashMap<>();
		dropMap.put(ZombieEntity.class, i -> i < 96 ? ((i < 24) ? 2 : 1) : 0);
		dropMap.put(ZombieVillagerEntity.class, i -> i < 64 ? 3 : 2);
		dropMap.put(HuskEntity.class, i -> i < 96 ? ((i < 24) ? 2 : 1) : 0);
		dropMap.put(SkeletonEntity.class, i -> i < 8 ? 1 : 0);//Logic here is that skeletons don't have any pockets for gold. So it's a rare drop
		dropMap.put(WitchEntity.class, i -> i < 64 ? 3 : 2);
		dropMap.put(PillagerEntity.class, i -> i < 64 ? 3 : 2);
		dropMap.put(EvokerEntity.class, i -> i < 64 ? 3 : 2);
		dropMap.put(VindicatorEntity.class, i -> i < 64 ? 3 : 2);
		dropMap.put(IllusionerEntity.class, i -> (i / 64) + 3);
		
		Random rnd = new Random();
		
		for(int i = 0; i < numCheeseSquares; i++) {
			int x = rnd.nextInt(cheeseRange);
			int z = rnd.nextInt(cheeseRange);
			cheeseRects.add(new CheeseRect(x, z, x + cheeseSquareWidth, z + cheeseSquareWidth));
		}
	}
	
	@SubscribeEvent
	public static void onLivingDropsEvent(final LivingDropsEvent livingDropsEvent) {
		
		if(!enableMobCoinDrops) {
			return;
		}
		
		DamageSource damageSource = livingDropsEvent.getSource();
		Entity killer = damageSource.getDirectEntity();
		
		if(killer != null && killer.getClass().equals(ServerPlayerEntity.class)) { //Only an official player, not a fake player
			//ServerPlayerEntity killerPlayer = (ServerPlayerEntity) killer;
			
			LivingEntity entity = livingDropsEvent.getEntityLiving();
			
			if(livingDropsEvent.isRecentlyHit() && dropMap.containsKey(entity.getClass())) {
				World world = entity.level;
				Collection<ItemEntity> drops = livingDropsEvent.getDrops();
				int lootLevel = livingDropsEvent.getLootingLevel();
				int lootChance = world.random.nextInt() & 0xFF;//0 to 255
				lootChance = MathHelper.clamp(lootChance - (lootLevel * 24), 0, 255);//-24 for every loot level
				int addDrop = dropMap.get(entity.getClass()).apply(lootChance);
				if(addDrop > 0 && isUncheesed(world.random, entity.blockPosition(), lootLevel)) {
					Item coin = NomismaItems.getCoin();
					ItemStack newDrop = new ItemStack(coin, addDrop);
					drops.add(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), newDrop));
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onItemPickupEvent(final EntityItemPickupEvent itemPickupEvent) {
		
		if(!enablePurseAutomaticCoinPickup) {
			return;
		}
		
		ItemEntity itemEntity = itemPickupEvent.getItem();
		ItemStack pickedUp = itemEntity.getItem();
		
		if(CoinItem.isCoin(pickedUp)) {
			PlayerEntity player = itemPickupEvent.getPlayer();
			for(ItemStack invStack : player.inventory.items) {
				Optional<PurseItem> coinPurseOpt = PurseItem.asCoinPurse(invStack);
				if(coinPurseOpt.isPresent()) {
					coinPurseOpt.get().addCoins(invStack, pickedUp);
					pickedUp.setCount(0);
					itemPickupEvent.setResult(Result.ALLOW);
					return;
				}
			}
		}
		
	}
	
}