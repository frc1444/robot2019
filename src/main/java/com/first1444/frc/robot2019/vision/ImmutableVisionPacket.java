package com.first1444.frc.robot2019.vision;

class ImmutableVisionPacket implements VisionPacket{
	private final double x, y, z, yaw, pitch, roll;
	
	/**
	 *
	 * @param x The x (Left to right)
	 * @param y The y (Up and down)
	 * @param z The z (Depth)
	 * @param yaw The yaw (rotation around the y axis) (left and right rotation)
	 * @param pitch The pitch (rotation around the x axis) (Up and down tilt)
	 * @param roll The roll (rotation around the z axis) (How much it is turned)
	 */
	ImmutableVisionPacket(double x, double y, double z, double yaw, double pitch, double roll) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
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
