package com.first1444.frc.robot2019.sensors;

import edu.wpi.first.wpilibj.interfaces.Gyro;

import static com.first1444.frc.util.MathUtil.mod;

public class DefaultOrientation implements Orientation {

	private final Gyro gyro;
	/** The starting orientation of the robot or the orientation when the gyro is reset. Normally 90 */
	private final double startingOrientation;
	private final boolean isGyroReversed;

	/**
	 * This expects that when the angle of the gyro
	 * @param gyro The gyro representing the offset from when it was reset.:
	 * @param startingOrientation The starting orientation. 0=right, 90=forward, 180=left, 270=backwards
	 * @param isGyroReversed true if a negative value represents counter-clockwise and if a positive value represents clockwise,
	 *                       if a negative value represents clockwise and a positive value represents counter-clockwise.
	 *                       This normally has to be true because of how WPILib does most things.
	 */
	public DefaultOrientation(Gyro gyro, double startingOrientation, boolean isGyroReversed) {
		this.gyro = gyro;
		this.startingOrientation = startingOrientation;
		this.isGyroReversed = isGyroReversed;
	}
	public DefaultOrientation(Gyro gyro, double startingOrientation){
		this(gyro, startingOrientation, true);
	}

	@Override
	public double getOrientation() {
		final double angle = gyro.getAngle() * (isGyroReversed ? -1 : 1); // how much the robot has turned since starting rotation
		final double r = startingOrientation + angle;
		return mod(r, 360);
	}

}
