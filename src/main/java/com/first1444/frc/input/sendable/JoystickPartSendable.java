package com.first1444.frc.input.sendable;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import me.retrodaredevil.controller.input.JoystickPart;

import java.text.DecimalFormat;
import java.util.function.Supplier;

public class JoystickPartSendable extends SendableBase {
	private final DecimalFormat format = new DecimalFormat(" #0.00;-#0.00");
	private final Supplier<JoystickPart> joystickPartSupplier;

	public JoystickPartSendable(JoystickPart joystick) {
		this.joystickPartSupplier = () -> joystick;
	}
	public JoystickPartSendable(Supplier<JoystickPart> joystickPartSupplier){
		this.joystickPartSupplier = joystickPartSupplier;
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.addStringProperty("X", () -> format.format(joystickPartSupplier.get().getX()), null);
		builder.addStringProperty("Y", () -> format.format(joystickPartSupplier.get().getY()), null);
		builder.addStringProperty("correct magnitude", () -> format.format(joystickPartSupplier.get().getCorrectMagnitude()), null);
		builder.addStringProperty("angle degrees", () -> format.format(joystickPartSupplier.get().getAngle()), null);
		builder.addBooleanProperty("is deadzone", () -> joystickPartSupplier.get().isDeadzone(), null);

	}
}
