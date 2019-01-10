package com.first1444.frc.robot2019.subsystems.swerve;

public interface FourSwerveCollection extends SwerveCollection{

	SwerveModule getFrontLeft();
	SwerveModule getFrontRight();
	SwerveModule getRearLeft();
	SwerveModule getRearRight();
}
