package com.first1444.frc.robot2019.vision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.hypot;

public class BestVisionPacketSelector implements PreferredTargetSelector {
	@Override
	public VisionPacket getPreferredTarget(Collection<? extends VisionPacket> visiblePackets) {
		if(visiblePackets.isEmpty())
			throw new IllegalArgumentException();
		
		List<? extends VisionPacket> packets = filterAngles(visiblePackets);
		if(packets.size() == 1){
			return packets.get(0);
		}
		if(packets.isEmpty()){
			return filterDistance(visiblePackets);
		}
        return filterDistance(packets);
	}
	private List<? extends VisionPacket> filterAngles(Collection<? extends VisionPacket> visionPackets){
		final List<VisionPacket> packets = new ArrayList<>(visionPackets);
		packets.removeIf(packet -> abs(packet.getYaw()) > 50 || abs(packet.getPitch()) > 45 || abs(packet.getRoll()) > 45);
		if(packets.isEmpty()){
			return new ArrayList<>(visionPackets);
		}
        return packets;
	}
	private VisionPacket filterDistance(Collection<? extends VisionPacket> visionPackets){
        VisionPacket closest = null;
        double closestDistance = 0;
        for(VisionPacket packet : visionPackets){
        	final double distance = hypot(packet.getX(), packet.getY());
        	if(closest == null || closestDistance > distance){
        		closest = packet;
        		closestDistance = distance;
			}
		}
        return closest;
	}
	
}
