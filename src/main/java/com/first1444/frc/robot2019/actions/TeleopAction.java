package com.first1444.frc.robot2019.actions;

import com.first1444.frc.robot2019.Robot;
import com.first1444.frc.robot2019.input.RobotInput;
import com.first1444.frc.robot2019.subsystems.Drive;
import me.retrodaredevil.action.SimpleAction;

/**
 * This handles everything needed for teleop and should be ended when teleop is over. This can be recycled
 */
public class TeleopAction extends SimpleAction {
	private final Robot robot;
	private final RobotInput input;
	public TeleopAction(Robot robot, RobotInput input) {
		super(true);
		this.robot = robot;
		this.input = input;
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		final Drive drive = robot.getDrive();
	}

	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
	}
}
