package com.first1444.frc.robot2019.subsystems;

import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.SimpleAction;

public class Drive extends SimpleAction {
	private final Action moduleUpdater;

	public Drive() {
		super(true);
		moduleUpdater = new Actions.ActionMultiplexerBuilder(
				// initialize swerve modules here
		)
				.canBeDone(false)
				.canRecycle(true)
				.build();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		moduleUpdater.update();
	}

	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		moduleUpdater.end();
	}
}
