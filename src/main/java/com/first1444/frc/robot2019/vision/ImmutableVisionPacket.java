package com.first1444.frc.robot2019.vision;

class ImmutableVisionPacket implements VisionPacket{
	private final double x, y, z, yaw, pitch, roll;
	
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
}
