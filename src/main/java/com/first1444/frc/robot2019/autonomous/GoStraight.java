package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.util.MathUtil;
import me.retrodaredevil.action.SimpleAction;

import java.util.function.Supplier;

public class GoStraight extends SimpleAction {
	private final double distance;
	private final double directionDegrees;
	private final Double faceDirection;
	private final Supplier<SwerveDrive> driveSupplier;
	private final Supplier<Orientation> orientationSupplier;


	public GoStraight(double distance, double directionDegrees, Double faceDirectionDegrees, Supplier<SwerveDrive> driveSupplier, Supplier<Orientation> orientationSupplier) {
		super(true);
		this.distance = distance;
		this.directionDegrees = directionDegrees;
		this.faceDirection = faceDirectionDegrees;
		this.driveSupplier = driveSupplier;
		this.orientationSupplier = orientationSupplier;
	}


	@Override
	protected void onUpdate() {
		super.onUpdate();

		final SwerveDrive drive = driveSupplier.get();
		final double minChange;
		if(faceDirection != null) {
			final Orientation orientation = orientationSupplier.get();
			final double currentOrientation = orientation.getOrientation();

			minChange = MathUtil.minChange(faceDirection, currentOrientation, 360);
		} else {
			minChange = 0;
		}

	}
}
