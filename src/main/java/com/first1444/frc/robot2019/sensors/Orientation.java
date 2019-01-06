package com.first1444.frc.robot2019.sensors;

import com.first1444.frc.robot2019.Perspective;

import static com.first1444.frc.util.MathUtil.mod;

public interface Orientation {
	/**
	 * @return The orientation in degrees. 0=facing right, 90=facing forward, 180=facing left, 270=facing backwards
	 */
	double getOrientation();

	/**
	 * @param perspective The perspective of the driver
	 * @return The amount to add to the desired direction to account for the given perspective
	 */
	default double getOffset(Perspective perspective){
		if(!perspective.isUseGyro()){
			return mod(perspective.getForwardDirection() - 90, 360);
		}
		return mod(perspective.getForwardDirection() - getOrientation(), 360);
	}
}
