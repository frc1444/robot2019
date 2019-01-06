package com.first1444.frc.robot2019.subsystems.swerve;

import java.util.Arrays;
import java.util.List;

public class FourSwerveCollection {
	private final SwerveModule frontLeft, frontRight, rearLeft, rearRight;
	private final List<SwerveModule> modules;

	public FourSwerveCollection(SwerveModule frontLeft, SwerveModule frontRight, SwerveModule rearLeft, SwerveModule rearRight) {
		this.frontLeft = frontLeft;
		this.frontRight = frontRight;
		this.rearLeft = rearLeft;
		this.rearRight = rearRight;
		this.modules = Arrays.asList(frontLeft, frontRight, rearLeft, rearRight);
	}

	public SwerveModule getFrontLeft() {
		return frontLeft;
	}

	public SwerveModule getFrontRight() {
		return frontRight;
	}

	public SwerveModule getRearLeft() {
		return rearLeft;
	}

	public SwerveModule getRearRight() {
		return rearRight;
	}

	public List<SwerveModule> getModules() {
		return modules;
	}
}
