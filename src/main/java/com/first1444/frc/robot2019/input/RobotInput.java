package com.first1444.frc.robot2019.input;

import me.retrodaredevil.controller.ControllerInput;
import me.retrodaredevil.controller.input.DigitalChildPositionInputPart;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.controller.output.ControllerRumble;

public interface RobotInput extends ControllerInput {

	/** @return A JoystickPart representing the direction to move*/
	JoystickPart getMovementJoy();

	InputPart getTurnAmount();

	/** @return An InputPart that can have a range of [0..1] or [-1..1] representing the speed multiplier */
	InputPart getMovementSpeed();
	
	InputPart getVisionAlign();
	
	/** The manual speed of the lift. Should be activated when this {@link InputPart}'s is not in a deadzone*/
	InputPart getLiftManualSpeed();
	/** Should be used with {@link #getLiftManualSpeed()}. When pressed you can manually go below the limit to pickup cargo.*/
	InputPart getCargoLiftManualAllowed();
	
	/** @return An InputPart with a range of [-1..1] where a negative value is intaking and positive is spitting*/
	InputPart getCargoIntakeSpeed();
	
	InputPart getHatchManualPivotSpeed();
	InputPart getHatchPivotGroundPreset();
	InputPart getHatchPivotReadyPreset();
	InputPart getHatchPivotStowedPreset();
	
	InputPart getLevel1Preset();
	InputPart getLevel2Preset();
	InputPart getLevel3Preset();
	InputPart getLevelCargoShipCargoPreset();
	InputPart getCargoPickupPreset();
	
	InputPart getDefenseButton();
	
	InputPart getClimbLiftSpeed();
	InputPart getClimbWheelSpeed();
	
	InputPart getAutonomousCancelButton();
	JoystickPart getResetGyroJoy();
	
	ControllerRumble getDriverRumble();
	
	InputPart getAutonomousWaitButton();
	InputPart getAutonomousStartButton();
	
	InputPart getSwerveQuickReverseCancel();
	InputPart getSwerveRecalibrate();
	
	InputPart getCameraToggleButton();
}
