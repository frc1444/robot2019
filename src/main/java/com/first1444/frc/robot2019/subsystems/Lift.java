package com.first1444.frc.robot2019.subsystems;


public interface Lift {
	void setDesiredPosition(double desiredPosition);
	void setDesiredPosition(Position desiredPosition);
	
	/**
	 *
	 * @return true if the position set with {@link #setDesiredPosition(double)} or {@link #setPositionCargoIntake()} is reached
	 */
	boolean isDesiredPositionReached();
	void setPositionCargoIntake();
	void setManualSpeed(double speed, boolean canPickupCargo);
	void lockCurrentPosition();
	LiftMode getLiftMode();
	
	enum LiftMode {
		SPEED, POSITION
	}
	
	
	enum Position {
		LEVEL1,
		LEVEL2,
		LEVEL3,
		CARGO_CARGO_SHIP
	}
	
}
