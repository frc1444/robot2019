package com.first1444.frc.robot2019.subsystems.swerve;

//import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

public interface SwerveModule {

	/**
	 * @param speed A number in range [-1..1] representing the speed as a percentage
	 */
	void setSpeed(double speed);

	/**
	 * @return Gets the speed as a percentage
	 */
	double getSpeed();

	void setTargetPosition(double positionDegrees);
	double getTargetPosition();

	default void set(double speed, double positionDegrees){
		setSpeed(speed);
		setTargetPosition(positionDegrees);
	}

	double getCurrentPosition();

	/**
	 *
	 * @return A string to represent
	 */
	String getName();
}
