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
	private final ExtremeFlightJoystickControllerInput operatorJoy;
	private final ExtremeFlightJoystickControllerInput climbJoy;

	private final InputPart movementSpeed;
	private final InputPart liftManualSpeed;
	private final InputPart cargoIntakeSpeed;
	private final InputPart hatchManualPivotSpeed;

	public DefaultRobotInput(StandardControllerInput controller,
							 ExtremeFlightJoystickControllerInput operatorJoy,
							 ExtremeFlightJoystickControllerInput climbJoy,
							 ControllerRumble rumble){
		this.controller = controller;
		this.operatorJoy = operatorJoy;
		this.climbJoy = climbJoy;
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
		addChildren(false, false, controller, operatorJoy);
		movementSpeed = new TwoWayInput(
				References.create(controller::getRightTrigger), // forward
				References.create(controller::getLeftTrigger) // backward
		);
		addChildren(false, false, movementSpeed);
		final References.InputPartGetter joystickYGetter = () -> operatorJoy.getMainJoystick().getYAxis();
		liftManualSpeed = new MultiplierInputPart(
				References.create(joystickYGetter), // analog full
				References.create(operatorJoy::getTrigger) // digital
		);
		addChildren(false, false, liftManualSpeed);
		cargoIntakeSpeed = new MultiplierInputPart(
				true,
				new ScaledInputPart(AxisType.ANALOG, References.create(operatorJoy::getSlider)), // analog
				References.create(() -> operatorJoy.getDPad().getYAxis()) // digital full
		);
		addChildren(false, false, cargoIntakeSpeed);
		hatchManualPivotSpeed = new MultiplierInputPart(
				References.create(joystickYGetter), // analog full
				References.create(operatorJoy::getThumbButton) // digital
		);
		addChildren(false, false, hatchManualPivotSpeed);
	}

	// region Drive Controls
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
	// endregion
	
	@Override
	public InputPart getLiftManualSpeed() {
		return liftManualSpeed;
	}
	
	@Override
	public InputPart getManualCargoAllowed() {
		return operatorJoy.getThumbLeftLower();
	}
	
	@Override
	public InputPart getCargoIntakeSpeed() {
		return cargoIntakeSpeed;
	}
	
	@Override
	public InputPart getHatchManualPivotSpeed() {
		return hatchManualPivotSpeed;
	}
	
	// region Lift Presets
	@Override
	public InputPart getLevel1Preset() { // final
		return operatorJoy.getGridLowerLeft();
	}
	@Override
	public InputPart getLevel2Preset() { // final
		return operatorJoy.getGridMiddleLeft();
	}
	@Override
	public InputPart getLevel3Preset() { // final
		return operatorJoy.getGridUpperLeft();
	}
	@Override
	public InputPart getLevelCargoShipCargoPreset() { // final
		return operatorJoy.getGridMiddleRight();
	}
	@Override
	public InputPart getCargoPickupPreset() { // final
		return operatorJoy.getGridLowerRight();
	}
	// endregion
	
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
	public InputPart getAutonomousWaitButton() {
		return climbJoy.getGridLowerLeft();
	}
	
	@Override
	public InputPart getAutonomousStartButton() {
		return climbJoy.getGridLowerRight();
	}
	
	@Override
	public boolean isConnected() {
		return controller.isConnected() && operatorJoy.isConnected();
	}
}
