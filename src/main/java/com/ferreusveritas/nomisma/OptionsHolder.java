package com.ferreusveritas.nomisma;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class OptionsHolder {
	
	public static class Common {
		private static final boolean defaultEnableAntiCheese = true;
		private static final int defaultNumCheeseSquares = 16;
		private static final int defaultCheeseSquareHitPoints = 4;
		private static final int defaultCheeseAreaWidth = 128;
		private static final int defaultCheeseSquareWidth = 16;
		
		private static final boolean defaultEnableVillagerEmeraldReplacement = true;
		private static final int defaultEmeraldCoinValue = 2;
		
		private static final boolean defaultEnableEmeraldPurseConversion = true;
		private static final boolean defaultEnablePurseAutomaticCoinPickup = true;
		
		private static final boolean defaultEnableMobCoinDrops = true;
		
		//Cheese Rect Config
		public final ConfigValue<Boolean> enableAntiCheese;
		public final ConfigValue<Integer> numCheeseSquares;
		public final ConfigValue<Integer> cheeseSquareHitPoints;
		public final ConfigValue<Integer> cheeseAreaWidth;
		public final ConfigValue<Integer> cheeseSquareWidth;
		
		//Villager Trade Config
		public final ConfigValue<Boolean> enableVillagerEmeraldReplacement;
		public final ConfigValue<Integer> emeraldCoinValue;
		
		//Purse Config
		public final ConfigValue<Boolean> enableEmeraldPurseConversion;
		public final ConfigValue<Boolean> enablePurseAutomaticCoinPickup;
		
		//Mod Drop Config
		public final ConfigValue<Boolean> enableMobCoinDrops;
		
		public Common(ForgeConfigSpec.Builder builder) {
			
			builder.push("anti-cheese").comment("The anti-cheese system prevents players from farming mobs in a spawner setup for coin drops.",
				"Square areas are randomly assigned to the world.  When a coin drop occurs the area takes a hit point.  When the area is out of hitpoints ",
				"it moves to a new random location. This forces players to only get coin drops by exploring new areas and adventuring.");
			
			enableAntiCheese = builder
				.comment("If true then the anti-cheese system is enabled for coin drops.  If false then coin drops are made normally.")
				.define("enableAntiCheese", defaultEnableAntiCheese);
			
			numCheeseSquares = builder
				.comment("Total number of anti-cheese squares in the area")
				.defineInRange("numCheeseSquares", defaultNumCheeseSquares, 1, 256);
			
			cheeseSquareHitPoints = builder
				.comment("Number of times a square area can have coin drops before it moves")
				.defineInRange("cheeseSquareHitPoints", defaultCheeseSquareHitPoints, 1, 256);
			
			cheeseAreaWidth = builder
				.comment("Width of the entire system in blocks. Should be much larger than cheeseSquareWidth. System repeats endlessly across the world but only one instance of the system is used.")
				.defineInRange("cheeseAreaWidth", defaultCheeseAreaWidth, 1, 1024);
			
			cheeseSquareWidth = builder
				.comment("Width of a single anti-cheese square in blocks")
				.defineInRange("cheeseSquareWidth", defaultCheeseSquareWidth, 1, 128);
			
			builder.pop();
			
			
			builder.push("villager trade").comment("Villager trades with emeralds can be replaced with coins to make trading more useful.  All villager trades that involve an emerald are replaced with coins");
			
			enableVillagerEmeraldReplacement = builder
				.comment("Enable emerald replacement in villager trades")
				.define("enableVillagerEmeraldReplacement", defaultEnableVillagerEmeraldReplacement);
			
			emeraldCoinValue = builder
				.comment("Power of 4 value for an emerald used for villager trades and emerald to coin purse conversion",
					"0 => 1 (iron coin), ",
					"1 => 4 (iron coin pile), ",
					"2 => 16 (copper coin), ",
					"3 => 64 (copper coin pile), ",
					"4 => 256 (gold coin), ",
					"5 => 1024 (gold coin pile)"
				)
				.defineInRange("emeraldCoinValue", defaultEmeraldCoinValue, 0, 5);
			
			builder.pop();
			
			
			builder.push("coin purse config");
			
			enableEmeraldPurseConversion = builder
				.comment("If enabled then emeralds can be dropped into a purse and converted into their coin value equivalent.")
				.define("enableEmeraldPurseConversion", defaultEnableEmeraldPurseConversion);
			
			enablePurseAutomaticCoinPickup = builder
				.comment("If enabled then coins picked up from the world will go straight into a purse in a player's inventory instead of the player's inventory space.")
				.define("enablePurseAutomaticCoinPickup", defaultEnablePurseAutomaticCoinPickup);
			
			builder.pop();
			
			
			builder.push("mob drops");
			
			enableMobCoinDrops = builder
				.comment("If enabled then mobs such as Zombies, Illagers, and Witches will drop coins when killed by a player.")
				.define("enableMobCoinDrops", defaultEnableMobCoinDrops);
			
			builder.pop();
		}
		
	}
	
	public static int getEmeraldCoinLevel() {
		return COMMON.emeraldCoinValue.get();
	}
	
	public static int getEmeraldValue() {
		return (int)Math.pow(4, getEmeraldCoinLevel());
	}
	
	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	
	static {
		Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON = commonSpecPair.getLeft();
		COMMON_SPEC = commonSpecPair.getRight();
	}
	
}