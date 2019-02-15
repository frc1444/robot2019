package com.first1444.frc.robot2019.autonomous.actions.vision;

import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.util.MathUtil;
import me.retrodaredevil.action.SimpleAction;

import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.Math.*;

@Deprecated
public class VisionEstimateAction extends SimpleAction {
	private final Perspective perspective;
	private final Supplier<SwerveDrive> driveSupplier;
	private final Supplier<Orientation> orientationSupplier;
	private final VisionView view;
	public VisionEstimateAction(Perspective perspective, Supplier<SwerveDrive> driveSupplier, Supplier<Orientation> orientationSupplier, VisionView view) {
		super(true);
		this.perspective = Objects.requireNonNull(perspective);
		this.driveSupplier = Objects.requireNonNull(driveSupplier);
		this.orientationSupplier = Objects.requireNonNull(orientationSupplier);
		this.view = Objects.requireNonNull(view);
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		final double orientation = orientationSupplier.get().getOrientation();
		final double direction = view.getDirectionToTarget() - orientation; // forward is 90 degrees
		final double directionRadians = toRadians(direction);
		final double visionYaw = MathUtil.minChange(view.getTargetOrientation() - orientation, 0, 360); // perfect is 0 degrees
		final double distance = view.getDistanceToTarget();
		
		final double moveX = cos(directionRadians);
		final double moveY = sin(directionRadians);
		
		driveSupplier.get().setControl(moveX, moveY, .5 * max(-1, min(1, visionYaw / -30)), LineUpAction.MAX_SPEED, perspective);
		if(view.getTracker().calculateDistance() >= distance){
			setDone(true);
		}
	}
}
