package com.first1444.frc.input;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import me.retrodaredevil.controller.options.ControlOption;
import me.retrodaredevil.controller.options.OptionValue;

public class ControlOptionSendable extends SendableBase {
	private final ControlOption option;

	public ControlOptionSendable(ControlOption option) {
		this.option = option;

		setName(option.getLabel());
		setSubsystem(option.getCategory());

	}

	@Override
	public void initSendable(SendableBuilder builder) {
		final OptionValue value = option.getOptionValue();
		final String key = "Value";
		if(value.isOptionValueBoolean()){
			builder.addBooleanProperty(key, value::getBooleanOptionValue, value::setBooleanOptionValue);
		} else if(value.isOptionValueRadio()){
			throw new UnsupportedOperationException("Cannot add property for a radio option");
		} else {
			builder.addDoubleProperty(key, value::getOptionValue, value::setOptionValue);
		}
	}
}
