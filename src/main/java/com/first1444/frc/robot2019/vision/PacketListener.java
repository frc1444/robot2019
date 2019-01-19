package com.first1444.frc.robot2019.vision;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.Collection;

public class PacketListener extends Thread {
	private final int port;
	private VisionInstant instant;
	public PacketListener(int port){
		this.port = port;
		setDaemon(true);
	}
	public VisionInstant getPackets(){
		synchronized (this){
			return instant;
		}
	}
	
	@Override
	public void run() {
		try(ZContext context = new ZContext()){
			ZMQ.Socket socket = context.createSocket(ZMQ.SUB);
			socket.connect("tcp://10.14.44.5:" + port);
			socket.setLinger(0);
			socket.subscribe("".getBytes());
			
			while(!Thread.currentThread().isInterrupted()){
				String reply = socket.recvStr(0);
				updatePackets(reply);
			}
		}
	}
	
	private void updatePackets(String jsonObjectString){
		updatePackets(VisionConstants.GSON.fromJson(jsonObjectString, JsonObject.class));
	}
	private void updatePackets(JsonObject jsonObject){
		JsonArray packetArray = jsonObject.get("packets").getAsJsonArray();
		final Collection<VisionPacket> packets = new ArrayList<>();
		for (final JsonElement packetElement : packetArray) {
			final JsonObject packetObject = packetElement.getAsJsonObject();
			final VisionPacket packet = new ImmutableVisionPacket(
					packetObject.get("x").getAsDouble(),
					packetObject.get("y").getAsDouble(),
					packetObject.get("z").getAsDouble(),
					packetObject.get("yaw").getAsDouble(),
					packetObject.get("pitch").getAsDouble(),
					packetObject.get("roll").getAsDouble()
			);
			packets.add(packet);
			System.out.println(packet);
		}
		synchronized (this) {
			instant = new ImmutableVisionInstant(packets, System.currentTimeMillis());
		}
	}
}
