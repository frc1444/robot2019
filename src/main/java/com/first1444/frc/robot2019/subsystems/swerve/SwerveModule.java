package com.first1444.frc.robot2019.subsystems.swerve;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import me.retrodaredevil.action.Action;

public interface SwerveModule {
	
	default Action getCalibrateAction(){
		return null;
	}

	/**
	 * @param speed A number in range [-1..1] representing the speed as a percentage
	 */
	void setTargetSpeed(double speed);
	/**
	 * @return Gets the speed as a percentage
	 */
	double getTargetSpeed();
	
	double getTotalDistanceTraveledInches();

	
	void setTargetAngle(double positionDegrees);
	double getTargetAngle();
	
	double getCurrentAngle();

	default void set(double targetSpeed, double positionDegrees){
		SmartDashboard.putString(getName(), "speed: " + targetSpeed + " angle: " + positionDegrees);
		setTargetSpeed(targetSpeed);
		setTargetAngle(positionDegrees);
	}


	/**
	 *
	 * @return A string to represent
	 */
	String getName();
}
