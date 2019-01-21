package com.first1444.frc.robot2019.subsystems.swerve;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.ModuleConfig;
import com.first1444.frc.util.CTREUtil;
import com.first1444.frc.util.MathUtil;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.frc.util.valuemap.ValueMap;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import me.retrodaredevil.action.SimpleAction;

import java.util.function.IntSupplier;

public class TalonSwerveModule extends SimpleAction implements SwerveModule {
	private static final double WHEEL_CIRCUMFERENCE = 4 * Math.PI;
	
	private final TalonSRX drive;
	private final TalonSRX steer;

	private final String name;
	private final IntSupplier absoluteEncoderOffsetSupplier;
	
	private EncoderType currentEncoderType = null;

	private double speed = 0;
	private double targetPositionDegrees = 0;
	
	private double lastDistanceInches;
	private double totalDistanceGone = 0;

	public TalonSwerveModule(String name, int driveID, int steerID,
							 MutableValueMap<PidKey> drivePid, MutableValueMap<PidKey> steerPid,
							 MutableValueMap<ModuleConfig> moduleConfig) {
		super(true);
		this.name = name;
		absoluteEncoderOffsetSupplier = () -> (int) moduleConfig.getDouble(ModuleConfig.ABS_ENCODER_OFFSET);
		
		drive = new TalonSRX(driveID);
		steer = new TalonSRX(steerID);

		drive.configFactoryDefault();
		steer.configFactoryDefault();

		drivePid.addListener((key) -> CTREUtil.applyPID(drive, drivePid));
		steerPid.addListener((key) -> CTREUtil.applyPID(steer, steerPid));
		CTREUtil.applyPID(drive, drivePid);
		CTREUtil.applyPID(steer, steerPid);
		
		moduleConfig.addListener(option -> {
			if(option == ModuleConfig.ABS_ENCODER_OFFSET){
				updateAbsoluteEncoderOffset();
			}
		});
//		switchToAbsoluteEncoder();
		switchToAbsoluteEncoder();
		switchToQuadEncoder(); // TODO Because of this, the wheels must be in the correct position when starting
	}
	private void switchToAbsoluteEncoder(){
		if(currentEncoderType == EncoderType.ABSOLUTE){
			return;
		}
		steer.configSelectedFeedbackSensor(FeedbackDevice.Analog);
		steer.configSetParameter(ParamEnum.eFeedbackNotContinuous, 0, 0, 0);
		updateAbsoluteEncoderOffset();
		steer.setSensorPhase(false);
		
		currentEncoderType = EncoderType.ABSOLUTE;
	}
	private void updateAbsoluteEncoderOffset(){
		if(currentEncoderType == EncoderType.ABSOLUTE) {
			steer.setSelectedSensorPosition(steer.getSensorCollection().getAnalogInRaw() - absoluteEncoderOffsetSupplier.getAsInt());
		}
	}
	private void switchToQuadEncoder(){
		if(currentEncoderType == EncoderType.QUAD){
			return;
		}
		steer.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
		steer.configSetParameter(ParamEnum.eFeedbackNotContinuous, 0, 0, 0);
		steer.setSelectedSensorPosition(0);
		steer.setSensorPhase(true);
		
		currentEncoderType = EncoderType.QUAD;
	}
	private double getCurrentDistanceInches(){
		return drive.getSelectedSensorPosition() * WHEEL_CIRCUMFERENCE / (double) Constants.SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION;
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		{ // encoder code
			final double currentDistance = getCurrentDistanceInches();
			totalDistanceGone += Math.abs(currentDistance - lastDistanceInches);
			lastDistanceInches = currentDistance;
		}
		
		{ // steer code
			final int wrap = getCountsPerRevolution();
			final int current = steer.getSelectedSensorPosition();
			final long desired = Math.round(targetPositionDegrees * wrap / 360.0);

			final int newPosition = (int) MathUtil.minChange(desired, current, wrap) + current;
			SmartDashboard.putNumber(getName() + " newPosition", newPosition);
			steer.set(ControlMode.Position, newPosition);
		}
		
		{ // speed code
			drive.set(ControlMode.PercentOutput, speed);
			speed = 0;
		}
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
	public double getTotalDistanceTraveledInches() {
		return totalDistanceGone;
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
		final int encoderPosition = steer.getSelectedSensorPosition();
		final int totalCounts = getCountsPerRevolution();
		return MathUtil.mod(encoderPosition * 360.0 / totalCounts, 360.0);
	}

	@Override
	public String getName() {
		return name;
	}
	
	
	/** @return The number of encoder counds per revolution for the current {@link EncoderType} for the steer*/
	private int getCountsPerRevolution(){
		if(currentEncoderType == null){
			throw new IllegalStateException("Trying to get encoder counts per rev when an encoder type isn't set!");
		}
		switch(currentEncoderType){
			case QUAD:
				return Constants.SWERVE_STEER_QUAD_ENCODER_COUNTS_PER_REVOLUTION;
			case ABSOLUTE:
				return Constants.SWERVE_STEER_ABSOLUTE_ENCODER_COUNTS_PER_REVOLUTION;
		}
		throw new UnsupportedOperationException("We should have already returned!");
	}
	
	private enum EncoderType {
		QUAD, ABSOLUTE
	}
}
