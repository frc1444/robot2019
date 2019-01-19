package com.first1444.frc.robot2019.vision;

import java.util.Collection;
import java.util.Collections;

class ImmutableVisionInstant implements VisionInstant {
	private final Collection<VisionPacket> packets;
	private final long timeMillis;
	public ImmutableVisionInstant(Collection<VisionPacket> packets, long timeMillis) {
		this.packets = Collections.unmodifiableCollection(packets);
		this.timeMillis = timeMillis;
	}
	
	@Override
	public Collection<VisionPacket> getVisiblePackets() {
		return packets;
	}
	
	@Override
	public long getTimeMillis() {
		return timeMillis;
	}
}
