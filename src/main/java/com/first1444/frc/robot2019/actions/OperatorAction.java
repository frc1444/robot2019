package com.first1444.frc.robot2019.actions;

import com.first1444.frc.robot2019.Robot;
import com.first1444.frc.robot2019.input.RobotInput;
import me.retrodaredevil.action.SimpleAction;

public class OperatorAction extends SimpleAction {
	private final Robot robot;
	private final RobotInput input;
	public OperatorAction(Robot robot, RobotInput input) {
		super(true);
		this.robot = robot;
		this.input = input;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
	}
}
