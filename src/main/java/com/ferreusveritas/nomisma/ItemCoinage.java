package com.ferreusveritas.nomisma;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ItemCoinage extends Item {

	//private String[] name = { "mono", "di", "tri", "tetra", "penta", "hexa", "hepta", "octa", "nona", "deca", "undeca", "dodeca" };
	//private String suffix = "nomis";
	
	static public int numTypes = 12;
	
	public static final String name = "coinage";
	
	public ItemCoinage(){
		setCreativeTab(Nomisma.tabNomisma);
		setUnlocalizedName(name);
		setRegistryName(name);
		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack){
		int metadata = MathHelper.clamp_int(itemStack.getItemDamage(), 0, numTypes - 1);
		return super.getUnlocalizedName() + "." + metadata;
	}
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> items) {
        for(int i = 0; i < numTypes; i++) {
        	items.add(new ItemStack(this, 1, i));
        }
	}
	
}
