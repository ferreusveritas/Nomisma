package com.ferreusveritas.nomisma.dropIn;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageDropInCreative implements IMessage {
	
	private static final long serialVersionUID = 6654581117899104558L;
	
	public int slot;
	public ItemStack stack;
	
	public MessageDropInCreative() { }
	
	public MessageDropInCreative(int slot, ItemStack stack) {
		this.slot = slot;
		this.stack = stack;
	}
	
	public boolean receive(NetworkEvent.Context context) {
		context.enqueueWork(() -> DropInHandler.executeCreativeDropIn(context.getSender(), slot, stack));
		return true;
	}
	
}