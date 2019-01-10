package com.first1444.frc.robot2019.input;

import me.retrodaredevil.controller.SimpleControllerInput;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.controller.input.References;
import me.retrodaredevil.controller.input.TwoWayInput;
import me.retrodaredevil.controller.types.ExtremeFlightJoystickControllerInput;
import me.retrodaredevil.controller.types.StandardControllerInput;

/**
 * A class that takes care of all the controllers connected to the driver station and
 * exposes some of their inputs to be used in other parts of the program
 * <p>
 * This must be updated each call to period, and is usually done by a {@link me.retrodaredevil.controller.ControllerManager}
 */
public class DefaultRobotInput extends SimpleControllerInput implements RobotInput {
	private final StandardControllerInput controller;
	private final ExtremeFlightJoystickControllerInput joystick;

	private final InputPart movementSpeed;

	public DefaultRobotInput(StandardControllerInput controller, ExtremeFlightJoystickControllerInput joystick){
		this.controller = controller;
		this.joystick = joystick;
		addChildren(false, false, controller, joystick);
		movementSpeed = new TwoWayInput(
				References.create(controller::getRightTrigger), // forward
				References.create(controller::getLeftTrigger) // backward
		);
		addChildren(false, false, movementSpeed);
	}

	// implement inputs defined in RobotInput here


	@Override
	public JoystickPart getMovementJoy() {
		return controller.getLeftJoy();
	}

	@Override
	public InputPart getTurnAmount() {
        return controller.getRightJoy().getXAxis();
	}

	@Override
	public InputPart getMovementSpeed() {
		return movementSpeed;
	}

	@Override
	public boolean isConnected() {
		return controller.isConnected() && joystick.isConnected();
	}
}
