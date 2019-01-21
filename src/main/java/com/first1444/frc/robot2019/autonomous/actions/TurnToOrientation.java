package com.first1444.frc.robot2019.autonomous.actions;

import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.util.MathUtil;
import me.retrodaredevil.action.SimpleAction;

import java.util.function.Supplier;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class TurnToOrientation extends SimpleAction {

	private final double desiredOrientation;
	private final Supplier<SwerveDrive> driveSupplier;
	private final Supplier<Orientation> orientationSupplier;

	public TurnToOrientation(double desiredOrientation, Supplier<SwerveDrive> driveSupplier, Supplier<Orientation> orientationSupplier) {
		super(true);
		this.desiredOrientation = desiredOrientation;
		this.driveSupplier = driveSupplier;
		this.orientationSupplier = orientationSupplier;
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		final SwerveDrive drive = driveSupplier.get();
		final Orientation orientation = orientationSupplier.get();
		final double currentOrientation = orientation.getOrientation();

		final double minChange = MathUtil.minChange(desiredOrientation, currentOrientation, 360);
		final double turnAmount = max(-1, min(1, minChange / -20)); // when positive turn right, when negative turn left
		drive.setControl(0, 0, 1, turnAmount, Perspective.DRIVER_STATION);

	}
}
