package com.ferreusveritas.nomisma.dropIn;

import net.minecraftforge.fml.network.NetworkEvent;

public class MessageDropIn implements IMessage {
	
	private static final long serialVersionUID = 4879090175821123361L;
	
	public int slot;
	
	public MessageDropIn() { }
	
	public MessageDropIn(int slot) {
		this.slot = slot;
	}
	
	public boolean receive(NetworkEvent.Context context) {
		context.enqueueWork(() -> DropInHandler.executeDropIn(context.getSender(), slot));
		return true;
	}
	
}
