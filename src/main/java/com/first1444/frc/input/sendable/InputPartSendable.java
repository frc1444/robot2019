package com.first1444.frc.input.sendable;

import com.first1444.frc.robot2019.Constants;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import me.retrodaredevil.controller.input.InputPart;

import java.util.function.Supplier;

public class InputPartSendable extends ControllerPartSendable {
	private final Supplier<? extends InputPart> inputPartSupplier;

	public InputPartSendable(InputPart inputPart) {
		super(inputPart);
		this.inputPartSupplier = () -> inputPart;
	}
	public InputPartSendable(Supplier<? extends InputPart> inputPartSupplier){
		super(inputPartSupplier);
		this.inputPartSupplier = inputPartSupplier;
	}


	@Override
	public void initSendable(SendableBuilder builder) {
		super.initSendable(builder);
		builder.addStringProperty("position", () -> Constants.DECIMAL_FORMAT.format(inputPartSupplier.get().getPosition()), null);
		builder.addStringProperty("digital position", () -> Constants.DECIMAL_FORMAT.format(inputPartSupplier.get().getDigitalPosition()), null);
		builder.addBooleanProperty("is deadzone", () -> inputPartSupplier.get().isDeadzone(), null);
		builder.addBooleanProperty("is down", () -> inputPartSupplier.get().isDown(), null);
	}
}
