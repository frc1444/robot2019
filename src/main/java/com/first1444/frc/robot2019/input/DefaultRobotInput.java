package com.first1444.frc.robot2019.input;

import me.retrodaredevil.controller.SimpleControllerInput;
import me.retrodaredevil.controller.input.*;
import me.retrodaredevil.controller.output.ControllerRumble;
import me.retrodaredevil.controller.output.DisconnectedRumble;
import me.retrodaredevil.controller.types.ExtremeFlightJoystickControllerInput;
import me.retrodaredevil.controller.types.RumbleCapableController;
import me.retrodaredevil.controller.types.StandardControllerInput;

import java.util.function.Supplier;

/**
 * A class that takes care of all the controllers connected to the driver station and
 * exposes some of their inputs to be used in other parts of the program
 * <p>
 * This must be updated each call to period, and is usually done by a {@link me.retrodaredevil.controller.ControllerManager}
 */
public class DefaultRobotInput extends SimpleControllerInput implements RobotInput {
	private final StandardControllerInput controller;
	private final Supplier<ControllerRumble> rumbleSupplier;
	private final ExtremeFlightJoystickControllerInput joystick;

	private final InputPart movementSpeed;
	private final InputPart liftManualSpeed;
	private final InputPart hatchManualPivotSpeed;

	public DefaultRobotInput(StandardControllerInput controller, ExtremeFlightJoystickControllerInput joystick, ControllerRumble rumble){
		this.controller = controller;
		this.joystick = joystick;
		if(rumble != null){
			addChildren(false, true, rumble);
			this.rumbleSupplier = () -> rumble;
		} else {
			if (controller instanceof RumbleCapableController) {
				this.rumbleSupplier = ((RumbleCapableController) controller)::getRumble;
			} else {
				final ControllerRumble disconnectedRumble = new DisconnectedRumble();
				this.rumbleSupplier = () -> disconnectedRumble;
				addChildren(false, false, disconnectedRumble);
			}
		}
		addChildren(false, false, controller, joystick);
		movementSpeed = new TwoWayInput(
				References.create(controller::getRightTrigger), // forward
				References.create(controller::getLeftTrigger) // backward
		);
		addChildren(false, false, movementSpeed);
		final References.InputPartGetter joystickYGetter = () -> joystick.getMainJoystick().getYAxis();
		liftManualSpeed = new LowestPositionInputPart(
				References.create(joystickYGetter), // NOTE: This relies on this being in this order
				References.create(joystick::getTrigger)
		);
		addChildren(false, false, liftManualSpeed);
		hatchManualPivotSpeed = new LowestPositionInputPart(
				References.create(joystickYGetter),
				References.create(joystick::getThumbButton)
		);
		addChildren(false, false, hatchManualPivotSpeed);
	}

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
	public InputPart getLiftManualSpeed() {
        return liftManualSpeed;
	}
	
	@Override
	public InputPart getCargoIntakeSpeed() {
        return joystick.getDPad().getYAxis();
	}
	
	@Override
	public InputPart getHatchManualPivotSpeed() {
		return null;
	}
	
	@Override
	public InputPart getAutonomousCancelButton() {
		return controller.getLeftStick();
	}
	
	@Override
	public JoystickPart getResetGyroJoy() {
		return controller.getDPad();
	}
	
	@Override
	public ControllerRumble getDriverRumble() {
		return rumbleSupplier.get();
	}
	
	@Override
	public boolean isConnected() {
		return controller.isConnected() && joystick.isConnected();
	}
}
