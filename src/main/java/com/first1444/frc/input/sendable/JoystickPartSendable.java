package com.first1444.frc.input.sendable;

import com.first1444.frc.robot2019.Constants;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import me.retrodaredevil.controller.input.JoystickPart;

import java.text.DecimalFormat;
import java.util.function.Supplier;

public class JoystickPartSendable extends ControllerPartSendable {
	private final Supplier<? extends JoystickPart> joystickPartSupplier;

	public JoystickPartSendable(JoystickPart joystick) {
		super(joystick);
		this.joystickPartSupplier = () -> joystick;
	}
	public JoystickPartSendable(Supplier<? extends JoystickPart> joystickPartSupplier){
		super(joystickPartSupplier);
		this.joystickPartSupplier = joystickPartSupplier;
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.addStringProperty("X", () -> Constants.format.format(joystickPartSupplier.get().getX()), null);
		builder.addStringProperty("Y", () -> Constants.format.format(joystickPartSupplier.get().getY()), null);
		builder.addStringProperty("correct magnitude", () -> Constants.format.format(joystickPartSupplier.get().getCorrectMagnitude()), null);
		builder.addStringProperty("angle degrees", () -> Constants.format.format(joystickPartSupplier.get().getAngle()), null);
		builder.addBooleanProperty("is deadzone", () -> joystickPartSupplier.get().isDeadzone(), null);

	}
}
