package com.ferreusveritas.nomisma.dropIn;

import java.io.Serializable;

import net.minecraftforge.fml.network.NetworkEvent;

public interface IMessage extends Serializable {
	
	public boolean receive(NetworkEvent.Context context);
	
}
