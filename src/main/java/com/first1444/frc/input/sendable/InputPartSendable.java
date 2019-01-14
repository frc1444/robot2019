package com.first1444.frc.input.sendable;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import me.retrodaredevil.controller.input.InputPart;

import java.text.DecimalFormat;
import java.util.function.Supplier;

public class InputPartSendable extends SendableBase {
	private final DecimalFormat format = new DecimalFormat(" #0.00;-#0.00");
	private final Supplier<InputPart> inputPartSupplier;

	public InputPartSendable(InputPart inputPart) {
		this.inputPartSupplier = () -> inputPart;
	}
	public InputPartSendable(Supplier<InputPart> inputPartSupplier){
		this.inputPartSupplier = inputPartSupplier;
	}


	@Override
	public void initSendable(SendableBuilder builder) {
		builder.addStringProperty("position", () -> format.format(inputPartSupplier.get().getPosition()), null);
		builder.addStringProperty("digital position", () -> format.format(inputPartSupplier.get().getDigitalPosition()), null);
		builder.addBooleanProperty("is deadzone", () -> inputPartSupplier.get().isDeadzone(), null);
		builder.addBooleanProperty("is down", () -> inputPartSupplier.get().isDown(), null);
	}
}
