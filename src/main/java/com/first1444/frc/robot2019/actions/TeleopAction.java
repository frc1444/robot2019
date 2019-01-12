package com.first1444.frc.robot2019.actions;

import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.Robot;
import com.first1444.frc.robot2019.input.RobotInput;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveModule;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;

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
		final SwerveDrive drive = robot.getDrive();

		final JoystickPart joystick = input.getMovementJoy();
		final double x, y;
		if(joystick.isDeadzone()){
			x = 0;
			y = 0;
		} else {
			x = joystick.getX();
			y = joystick.getY();
		}

		final InputPart turnInputPart = input.getTurnAmount();
		final double turnAmount;
		if(turnInputPart.isDeadzone()){
			turnAmount = 0;
		} else {
			turnAmount = turnInputPart.getPosition();
		}

		final InputPart speedInputPart = input.getMovementSpeed();
		final double speed;
		if(speedInputPart.isDeadzone()){
			speed = 0;
		} else {
			speed = speedInputPart.getPosition();
		}

		drive.setControl(x, y, turnAmount, speed, Perspective.DRIVER_STATION);
	}

	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
	}
}
