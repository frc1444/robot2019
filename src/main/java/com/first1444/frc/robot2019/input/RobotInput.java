package com.first1444.frc.robot2019.input;

import me.retrodaredevil.controller.ControllerInput;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.controller.output.ControllerRumble;

public interface RobotInput extends ControllerInput {

	/** @return A JoystickPart representing the direction to move*/
	JoystickPart getMovementJoy();

	InputPart getTurnAmount();

	/** @return An InputPart that can have a range of [0..1] or [-1..1] representing the speed multiplier */
	InputPart getMovementSpeed();
	
	InputPart getLiftManualSpeed();
	
	/** @return An InputPart with a range of [-1..1] where a negative value is intaking and positive is spitting*/
	InputPart getCargoIntakeSpeed();
	
	InputPart getHatchManualPivotSpeed();
	
	InputPart getAutonomousCancelButton();
	JoystickPart getResetGyroJoy();
	
	ControllerRumble getDriverRumble();
}
