package com.first1444.frc.robot2019.subsystems.swerve;

import java.util.Arrays;
import java.util.List;

public class ImmutableFourSwerveCollection implements FourSwerveCollection {
	private final SwerveModule frontLeft, frontRight, rearLeft, rearRight;
	private final List<SwerveModule> modules;

	public ImmutableFourSwerveCollection(SwerveModule frontLeft, SwerveModule frontRight,
										 SwerveModule rearLeft, SwerveModule rearRight) {
		this.frontLeft = frontLeft;
		this.frontRight = frontRight;
		this.rearLeft = rearLeft;
		this.rearRight = rearRight;
		this.modules = Arrays.asList(frontLeft, frontRight, rearLeft, rearRight);
	}

	@Override
	public SwerveModule getFrontLeft() {
        return frontLeft;
	}

	@Override
	public SwerveModule getFrontRight() {
        return frontRight;
	}

	@Override
	public SwerveModule getRearLeft() {
        return rearLeft;
	}

	@Override
	public SwerveModule getRearRight() {
        return rearRight;
	}

	@Override
	public List<? extends SwerveModule> getModules() {
        return modules;
	}
}
