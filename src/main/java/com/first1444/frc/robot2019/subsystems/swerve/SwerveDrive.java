package com.first1444.frc.robot2019.subsystems.swerve;

import com.first1444.frc.robot2019.Perspective;

import java.util.List;

public interface SwerveDrive {
	List<? extends SwerveModule> getModules();

	/**
	 * Sets the values that will be applied to the swerve drive. These values will be reset each time this updates
	 * so you must call this method constantly to keep the robot going in the desired direction
	 *
	 * @param speed
	 * @param directionDegrees
	 * @param turnAmount A value in range [-1..1] representing the amount to turn
	 * @param controlPerspective The perspective to drive the robot from
	 */
	void setControl(double speed, Double directionDegrees, double turnAmount, Perspective controlPerspective);
}
