package com.ferreusveritas.nomisma.proxy;

import com.ferreusveritas.nomisma.ItemCoinage;
import com.ferreusveritas.nomisma.Nomisma;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit() {
		super.preInit();
		
		for(int i = 0; i < ItemCoinage.numTypes; i++) {
			ModelBakery.registerItemVariants(Nomisma.coinage, new ResourceLocation(Nomisma.MODID, "coinage_" + i));
		}
	}
	
	@Override
	public void init() {
		super.init();
		
		for(int i = 0; i < ItemCoinage.numTypes; i++) {
			regMesher(Nomisma.coinage, i);
		}

	}
	
	public static void regMesher(Item item, int meta) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, new ModelResourceLocation(item.getRegistryName() + "_" + meta, "inventory"));
	}
}
