package com.first1444.frc.robot2019.autonomous.actions;

import com.first1444.frc.robot2019.actions.TimedAction;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public class WaitAction extends TimedAction {
	private final BooleanSupplier shouldWait;
	private final BooleanSupplier shouldStart;
	/**
	 * @param lastMillis The amount of time in millis for this to last
	 */
	public WaitAction(long lastMillis, BooleanSupplier shouldWait, BooleanSupplier shouldStart) {
		super(true, lastMillis);
		this.shouldWait = Objects.requireNonNull(shouldWait);
		this.shouldStart = Objects.requireNonNull(shouldStart);
	}
	
	@Override
	protected void onIsDoneRequest() {
		super.onIsDoneRequest();
		if(shouldStart.getAsBoolean()){
			setDone(true);
		}
		if(shouldWait.getAsBoolean()){
			setDone(false);
		}
	}
}
