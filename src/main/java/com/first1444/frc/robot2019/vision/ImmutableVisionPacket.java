package com.first1444.frc.robot2019.vision;

import static java.lang.Math.abs;

class ImmutableVisionPacket implements VisionPacket{
	private final double x, y, z, yaw, pitch, roll, imageX, imageY;
	
	/**
	 *  @param x The x (Left to right)
	 * @param y The y (Up and down)
	 * @param z The z (Depth)
	 * @param yaw The yaw (rotation around the y axis) (left and right rotation)
	 * @param pitch The pitch (rotation around the x axis) (Up and down tilt)
	 * @param roll The roll (rotation around the z axis) (How much it is turned)
	 * @param imageX The x position of the vision from the perspective of the camera in range [-1..1]. A positive value is right
	 * @param imageY The y position of the vision from the perspective of the camera in range [-1..1]. A positive value is up
	 */
	ImmutableVisionPacket(double x, double y, double z, double yaw, double pitch, double roll, double imageX, double imageY) {
		if(abs(imageX) > 1)
			throw new IllegalArgumentException();
		if(abs(imageY) > 1)
			throw new IllegalArgumentException();
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		this.imageX = imageX;
		this.imageY = imageY;
	}
	
	@Override
	public double getX() {
		return x;
	}
	
	@Override
	public double getY() {
		return y;
	}
	
	@Override
	public double getZ() {
		return z;
	}
	
	@Override
	public double getYaw() {
		return yaw;
	}
	
	@Override
	public double getPitch() {
		return pitch;
	}
	
	@Override
	public double getRoll() {
		return roll;
	}
	
	@Override
	public double getImageX() {
		return imageX;
	}
	
	@Override
	public double getImageY() {
		return imageY;
	}
	
	@Override
	public String toString() {
		return "ImmutableVisionPacket{" +
				"x=" + x +
				", y=" + y +
				", z=" + z +
				", yaw=" + yaw +
				", pitch=" + pitch +
				", roll=" + roll +
				'}';
	}
	
}
