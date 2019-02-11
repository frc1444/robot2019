package com.first1444.frc.robot2019.subsystems;

public interface HatchIntake {
	void hold();
	void drop();

	void groundPosition();
	void readyPosition();
	void stowedPosition();
	
	/** Sets the speed of the pivot. A positive value brings the pivot forward and down, a negative value brings it back and stowed.*/
	void setManualPivotSpeed(double speed);
	
	boolean isDesiredPositionReached();
	void lockCurrentPosition();
	PivotMode getPivotMode();
	
	enum PivotMode {
		SPEED, POSITION
	}
	
}
