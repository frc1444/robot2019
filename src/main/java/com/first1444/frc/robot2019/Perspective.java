package com.first1444.frc.robot2019;

public enum Perspective {
	ROBOT_FORWARD_CAM(90, false),
	ROBOT_RIGHT_CAM(0, false),
	ROBOT_LEFT_CAM(180, false),
	ROBOT_BACK_CAM(-90, false),
	DRIVER_STATION(90, true);

	private final double forwardDirection;
	private final boolean useGyro;

	Perspective(double forwardDirection, boolean useGyro) {
		this.forwardDirection = forwardDirection;
		this.useGyro = useGyro;
	}

	/**
	 * @return The the direction relative to the field that "up" on the joystick corresponds to
	 */
	public double getForwardDirection(){
		return forwardDirection;
	}
	public boolean isUseGyro(){
		return useGyro;
	}
}
