package com.first1444.frc.robot2019.autonomous.actions;

import com.first1444.frc.robot2019.actions.TimedAction;
import com.first1444.frc.robot2019.subsystems.CargoIntake;

public class TimedCargoIntake extends TimedAction {
	
	private final CargoIntake cargoIntake;
	private final double speed;
	
	/**
	 * @param lastMillis The amount of time in millis for this to last
	 */
	public TimedCargoIntake(long lastMillis, CargoIntake cargoIntake, double speed) {
		super(true, lastMillis);
		this.cargoIntake = cargoIntake;
		this.speed = speed;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		cargoIntake.setSpeed(speed);
	}
	
	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		cargoIntake.setSpeed(0);
	}
}
