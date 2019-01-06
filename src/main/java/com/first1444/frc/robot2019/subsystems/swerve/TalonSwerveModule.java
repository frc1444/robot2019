package com.first1444.frc.robot2019.subsystems.swerve;

import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.ValueMap;

public class TalonSwerveModule implements SwerveModule {
//	private final BaseMotorController
	private final ValueMap<PidKey> drivePid;
	private final ValueMap<PidKey> steerPid;

	public TalonSwerveModule(ValueMap<PidKey> drivePid, ValueMap<PidKey> steerPid) {
		this.drivePid = drivePid;
		this.steerPid = steerPid;
	}

	@Override
	public void setSpeed(double speed) {

	}

	@Override
	public double getSpeed() {
		return 0;
	}

	@Override
	public void setTargetPosition(double positionDegrees) {

	}

	@Override
	public double getTargetPosition() {
		return 0;
	}

	@Override
	public double getCurrentPosition() {
		return 0;
	}

	@Override
	public String getName() {
		return null;
	}
}
