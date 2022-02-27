package com.ferreusveritas.nomisma.dropIn;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
	
	public final SimpleChannel channel;
	
	private int index = 0;
	
	public NetworkHandler(String modid, int protocol) {
		this(modid, "main", protocol);
	}
	
	public NetworkHandler(String modid, String channelName, int protocol) {
		String protocolStr = Integer.toString(protocol);
		
		channel = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(modid, channelName))
			.networkProtocolVersion(() -> protocolStr)
			.clientAcceptedVersions(protocolStr::equals)
			.serverAcceptedVersions(protocolStr::equals)
			.simpleChannel();
	}
	
	public <T extends IMessage> void register(Class<T> clazz, NetworkDirection dir) {
		BiConsumer<T, PacketBuffer> encoder = MessageSerializer::writeObject;
		
		Function<PacketBuffer, T> decoder = (buf) -> {
			try {
				@SuppressWarnings("deprecation")
				T msg = clazz.newInstance();
				MessageSerializer.readObject(msg, buf);
				return msg;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			} 
		};
		
		BiConsumer<T, Supplier<NetworkEvent.Context>> consumer = (msg, supp) -> {
			NetworkEvent.Context context = supp.get();
			if(context.getDirection() == dir) {
				context.setPacketHandled(msg.receive(context));
			}
		};
		
		channel.registerMessage(index, clazz, encoder, decoder, consumer);
		index++;
	}
	
	public void sendToPlayer(IMessage msg, ServerPlayerEntity player) {
		channel.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}
	
	public void sendToServer(IMessage msg) {
		channel.sendToServer(msg);
	}
	
}