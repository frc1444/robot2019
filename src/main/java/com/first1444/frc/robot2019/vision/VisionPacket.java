package com.first1444.frc.robot2019.vision;

/**
 * NOTE: Distances are in inches, angles are in degrees
 */
public interface VisionPacket {
	/** @return The x position of the robot relative to the vision target (left and right)*/
	double getX();
	/** @return The y position of the robot relative to the vision target (up and down)*/
	double getY();
	/** @return The z position of the robot relative to the vision target (depth). This should be negative*/
	double getZ();
	
	/** @return the yaw of the vision target relative to the robot*/
	double getYaw();
	/** @return the pitch of the vision target relative to the robot*/
	double getPitch();
	/** @return the roll of the vision target relative to the robot*/
	double getRoll();
	
	/** @return The x position of the target on the camera. In range [-1..1]*/
	double getImageX();
	/** @return The y position of the target on the camera. In range [-1..1]*/
	double getImageY();
}
