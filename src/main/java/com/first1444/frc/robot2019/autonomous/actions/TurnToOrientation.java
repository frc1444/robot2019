package com.first1444.frc.robot2019.autonomous.actions;

import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.util.MathUtil;
import me.retrodaredevil.action.SimpleAction;

import java.util.function.Supplier;

import static java.lang.Math.*;

public class TurnToOrientation extends SimpleAction {
	private final double MAX_SPEED = .6;

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
		final double turnAmount = max(-MAX_SPEED, min(MAX_SPEED, minChange / -70)); // when positive turn right, when negative turn left
		drive.setControl(0, 0, 1, turnAmount, Perspective.DRIVER_STATION);
		if(abs(minChange) < 7.5){
			setDone(true);
		}
	}
}
