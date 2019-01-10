package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.util.MathUtil;
import me.retrodaredevil.action.SimpleAction;

import java.util.function.Supplier;

public class TurnToOrientation extends SimpleAction {

	private final double desiredOrientation;
	private final Supplier<SwerveDrive> driveSupplier;
	private final Supplier<Orientation> orientationSupplier;

	protected TurnToOrientation(double desiredOrientation, Supplier<SwerveDrive> driveSupplier, Supplier<Orientation> orientationSupplier) {
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

	}
}
