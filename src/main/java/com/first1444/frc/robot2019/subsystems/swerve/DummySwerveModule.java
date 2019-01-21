package com.first1444.frc.robot2019.subsystems.swerve;

import me.retrodaredevil.action.SimpleAction;

public class DummySwerveModule extends SimpleAction implements SwerveModule {
	public DummySwerveModule() {
		super(true);
	}
	
	@Override
	public void setTargetSpeed(double speed) {
	
	}
	
	@Override
	public double getTargetSpeed() {
		return 0;
	}
	
	@Override
	public void setTargetAngle(double positionDegrees) {
	
	}
	
	@Override
	public double getTotalDistanceTraveledInches() {
		return 0;
	}
	
	@Override
	public double getTargetAngle() {
		return 0;
	}
	
	@Override
	public double getCurrentAngle() {
		return 0;
	}
	
	@Override
	public String getName() {
		return "Dummy Swerve Module";
	}
}
