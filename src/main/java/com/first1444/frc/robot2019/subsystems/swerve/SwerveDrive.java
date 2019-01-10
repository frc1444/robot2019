package com.first1444.frc.robot2019.subsystems.swerve;

import com.first1444.frc.robot2019.Perspective;

import java.util.List;

public interface SwerveDrive {
	List<? extends SwerveModule> getModules();

	/**
	 * Sets the values that will be applied to the swerve drive. These values will be reset each time this updates
	 * so you must call this method constantly to keep the robot going in the desired direction
	 * <p>
	 * NOTE: If both x and y are 0, the robot should not move no matter what speed is, but the wheels should still
	 * rotate to the previous position.
	 *
	 * @param x The x axis of the joystick AKA the strafe vector. In range [-1..1]
	 * @param y The y axis of the joystick AKA the forward vector. In range [-1..1]
	 * @param turnAmount A value in range [-1..1] representing the amount to turn
	 * @param speed The speed multiplier. In range [0..1]
	 * @param controlPerspective The perspective to drive the robot from
	 */
	void setControl(double x, double y, double turnAmount, double speed, Perspective controlPerspective);
}
