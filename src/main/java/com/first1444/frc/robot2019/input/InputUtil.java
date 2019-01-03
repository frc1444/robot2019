package com.first1444.frc.robot2019.input;

import me.retrodaredevil.controller.implementations.BaseExtremeFlightJoystickControllerInput;
import me.retrodaredevil.controller.implementations.BaseStandardControllerInput;
import me.retrodaredevil.controller.implementations.ControllerPartCreator;
import me.retrodaredevil.controller.implementations.StandardControllerInputCreator;
import me.retrodaredevil.controller.implementations.mappings.DefaultExtremeFlightJoystickInputCreator;
import me.retrodaredevil.controller.implementations.mappings.PS4StandardControllerInputCreator;
import me.retrodaredevil.controller.options.OptionValues;
import me.retrodaredevil.controller.types.ExtremeFlightJoystickControllerInput;
import me.retrodaredevil.controller.types.StandardControllerInput;

public final class InputUtil {
	private InputUtil(){ throw new UnsupportedOperationException(); }


	public static StandardControllerInput createPS4Controller(ControllerPartCreator controller){
		StandardControllerInputCreator ps4Mappings = new PS4StandardControllerInputCreator();
		Boolean physicalLocationSwapped = ps4Mappings.getPhysicalLocationsSwapped();
		Boolean buttonNamesSwapped = ps4Mappings.getButtonNamesSwapped();
		return new BaseStandardControllerInput(
				ps4Mappings,
				controller,
				OptionValues.createImmutableBooleanOptionValue(physicalLocationSwapped == null ? false : physicalLocationSwapped),
				OptionValues.createImmutableBooleanOptionValue(buttonNamesSwapped == null ? false : buttonNamesSwapped)
		);
	}
	public static ExtremeFlightJoystickControllerInput createJoystick(ControllerPartCreator controller){
		return new BaseExtremeFlightJoystickControllerInput(
				new DefaultExtremeFlightJoystickInputCreator(),
				controller
		);
	}
}
