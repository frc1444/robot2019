package com.first1444.frc.robot2019.subsystems.swerve;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRXPIDSetConfiguration;
import com.first1444.frc.util.CTREUtil;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.frc.util.valuemap.ValueMap;
import me.retrodaredevil.action.SimpleAction;

public class TalonSwerveModule extends SimpleAction implements SwerveModule {
	private final BaseMotorController drive;
	private final BaseMotorController steer;

	private final String name;
	private final ValueMap<PidKey> drivePid;
	private final ValueMap<PidKey> steerPid;

	private double speed = 0;
	private double targetPositionDegrees = 0;

	public TalonSwerveModule(String name, int driveID, int steerID, MutableValueMap<PidKey> drivePid, MutableValueMap<PidKey> steerPid) {
		super(true);
		this.name = name;
		drive = new TalonSRX(driveID);
		steer = new TalonSRX(steerID);
		this.drivePid = drivePid;
		this.steerPid = steerPid;

		drive.configFactoryDefault();
		steer.configFactoryDefault();
//		TalonSRXConfiguration config = new TalonSRXConfiguration();

		drivePid.addListener((key) -> CTREUtil.applyPID(drive, drivePid));
		steerPid.addListener((key) -> CTREUtil.applyPID(steer, steerPid));
		CTREUtil.applyPID(drive, drivePid);
		CTREUtil.applyPID(steer, steerPid);
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();

		speed = 0;
	}

	@Override
	public void setTargetSpeed(double speed) {
		this.speed = speed;
	}

	@Override
	public double getTargetSpeed() {
		return speed;
	}

	@Override
	public void setTargetAngle(double positionDegrees) {
		this.targetPositionDegrees = positionDegrees;
	}

	@Override
	public double getTargetAngle() {
        return targetPositionDegrees;
	}

	@Override
	public double getCurrentAngle() {
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}
}
