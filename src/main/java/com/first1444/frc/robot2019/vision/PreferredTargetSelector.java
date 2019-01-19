package com.first1444.frc.robot2019.vision;

import java.util.Collection;

public interface PreferredTargetSelector {
	/**
	 *
	 * @param visiblePackets The visible
	 * @return The preferred vision target.
	 */
	VisionPacket getPreferredTarget(Collection<? extends VisionPacket> visiblePackets);
}
