package com.first1444.frc.robot2019.vision;

import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Collection;

public class PacketListener extends Thread {
	private final Collection<VisionPacket> packets = new ArrayList<>();
	public PacketListener(){
		setDaemon(true);
	}
	public Collection<VisionPacket> getPackets(){
		synchronized (packets){
			return new ArrayList<>(packets);
		}
	}
	
	@Override
	public void run() {
	
	}
	
	private void updatePackets(String jsonArray){
		updatePackets(VisionConstants.GSON.fromJson(jsonArray, JsonArray.class));
	}
	private void updatePackets(JsonArray jsonArray){
	
	}
}
