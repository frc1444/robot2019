package com.first1444.frc.robot2019.vision;

/**
 * NOTE: Distances are in inches, angles are in degrees
 */
public interface VisionPacket {
	/** @return The distance of the x axis (left and right) */
	double getX();
	/** @return The distance of the y axis (up and down) */
	double getY();
	/** @return The distance of the z axis (depth). This should always be positive*/
	double getZ();
	
	/** @return the yaw of the vision target */
	double getYaw();
	double getPitch();
	double getRoll();
	
	double getImageX();
	double getImageY();
}
