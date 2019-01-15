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

	void setTargetAngle(double positionDegrees);
	double getTargetAngle();
	
	double getCurrentAngle();

	default void set(double targetSpeed, double positionDegrees){
		setTargetSpeed(targetSpeed);
		setTargetAngle(positionDegrees);
	}


	/**
	 *
	 * @return A string to represent
	 */
	String getName();
}
