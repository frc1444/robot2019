package com.first1444.frc.robot2019.subsystems.swerve;


public interface SwerveModule {

	/**
	 * @param speed A number in range [-1..1] representing the speed as a percentage
	 */
	void setTargetSpeed(double speed);

	/**
	 * @return Gets the speed as a percentage
	 */
	double getTargetSpeed();

	void setTargetPosition(double positionDegrees);
	double getTargetPosition();

	default void set(double targetSpeed, double positionDegrees){
		setTargetSpeed(targetSpeed);
		setTargetPosition(positionDegrees);
	}

	double getCurrentPosition();

	/**
	 *
	 * @return A string to represent
	 */
	String getName();
}
