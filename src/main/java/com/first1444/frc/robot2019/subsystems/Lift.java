package com.first1444.frc.robot2019.subsystems;


public interface Lift {
	void setDesiredPosition(double desiredPosition);
	void setDesiredPosition(Position desiredPosition);
	
	/**
	 *
	 * @return true if the position set with {@link #setDesiredPosition(double)} or {@link #setDesiredPosition(Position)} is reached, false otherwise
	 */
	boolean isDesiredPositionReached();
	/** Sets the manual speed of the lift.*/
	void setManualSpeed(double speed, boolean canPickupCargo);
	/** Same as {@link #setManualSpeed(double, boolean) setManualSpeed(speed, true), except this won't try to slow the lift down if it gets near the bottom}*/
	void setManualSpeedOverride(double speed);
	void lockCurrentPosition();
	LiftMode getLiftMode();
	
	enum LiftMode {
		SPEED, POSITION
	}
	
	
	enum Position {
		CARGO_PICKUP,
		LEVEL1,
		LEVEL2,
		LEVEL3,
		CARGO_CARGO_SHIP
	}
	
}
