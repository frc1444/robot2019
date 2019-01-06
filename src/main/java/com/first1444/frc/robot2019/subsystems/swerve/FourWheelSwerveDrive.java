package com.first1444.frc.robot2019.subsystems.swerve;

import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.Robot;
import com.first1444.frc.robot2019.sensors.Orientation;
import me.retrodaredevil.action.SimpleAction;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.Math.*;

public class FourWheelSwerveDrive extends SimpleAction implements SwerveDrive{
	private final Supplier<Orientation> orientationSupplier;
	private final FourSwerveCollection swerveCollection;
	private final double cosA, sinA;

	private double speed = 0;
	/** The direction to rotate or null to not rotate*/
	private Double directionDegrees = 90.0;
	private double turnAmount = 0;
	private Perspective controlPerspective = Perspective.ROBOT_FORWARD_CAM;

	/**
	 *
     * @param orientationSupplier The getter for the orientation
	 * @param swerveCollection A collection of 4 swerve modules
	 * @param wheelBase The distance from the front wheels to the back wheels
	 * @param trackWidth The distance from the left wheels to the right wheels
	 */
	public FourWheelSwerveDrive(Supplier<Orientation> orientationSupplier, FourSwerveCollection swerveCollection, double wheelBase, double trackWidth) {
		super(true);
        this.orientationSupplier = orientationSupplier;
		this.swerveCollection = swerveCollection;

		final double diagonal = Math.hypot(wheelBase, trackWidth);
		cosA = wheelBase / diagonal;
		sinA = trackWidth / diagonal;
	}
	@Override
	public List<? extends SwerveModule> getModules() { return swerveCollection.getModules(); }

	@Override
	public void setControl(double speed, Double directionDegrees, double turnAmount, Perspective controlPerspective) {
		if(abs(speed) > 1){
			throw new IllegalArgumentException("abs(speed) cannot be greater than 1!");
		}
		if(abs(turnAmount) > 1){
			throw new IllegalArgumentException("abs(turnAmount) cannot be greater than 1!");
		}

		this.speed = speed;
		if(directionDegrees != null){
			this.directionDegrees = directionDegrees;
		}
		this.turnAmount = turnAmount;
		this.controlPerspective = Objects.requireNonNull(controlPerspective);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		final double offset = orientationSupplier.get().getOffset(controlPerspective);

		if(speed == 0 && turnAmount == 0 && directionDegrees == null){
			for(SwerveModule module : getModules()){
				module.setSpeed(0);
			}
		} else {
			/*
			Note when looking at this code, most things are defined in this order: fl, fr, rl, rr. Other swerve drive
			code may define these in a different order. That may make this code look different or even incorrect but I
			assure you, it is not.
			 */
			final double direction = (directionDegrees == null ? 90 : directionDegrees) + offset;
			final double directionRadians = toRadians(direction);
			final double y = sin(directionRadians); // forward
			final double x = cos(directionRadians); // strafe

			final double A = y - turnAmount * sinA;
			final double B = y + turnAmount * sinA;
			final double C = x - turnAmount * cosA;
			final double D = x + turnAmount * cosA;

			double flSpeed = hypot(B, D);
			double frSpeed = hypot(A, D);
			double rlSpeed = hypot(B, C);
			double rrSpeed = hypot(A, C);

			final double max = max(max(flSpeed, frSpeed), max(rlSpeed, rrSpeed));
			if(max > 1){
				flSpeed /= max;
				frSpeed /= max;
				rlSpeed /= max;
				rrSpeed /= max;
			}

			flSpeed *= speed;
			frSpeed *= speed;
			rlSpeed *= speed;
			rrSpeed *= speed;

			final double flAngle = toDegrees(atan2(B, D));
			final double frAngle = toDegrees(atan2(A, D));
			final double rlAngle = toDegrees(atan2(B, C));
			final double rrAngle = toDegrees(atan2(A, C));

			swerveCollection.getFrontLeft().set(flSpeed, flAngle);
			swerveCollection.getFrontRight().set(frSpeed, frAngle);
			swerveCollection.getRearLeft().set(rlSpeed, rlAngle);
			swerveCollection.getRearRight().set(rrSpeed, rrAngle);

		}

		resetControl();
	}
	private void resetControl(){
		speed = 0;
		turnAmount = 0;
		controlPerspective = Perspective.ROBOT_FORWARD_CAM;
	}
}
