package com.first1444.frc.robot2019.subsystems.swerve;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.*;
import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.util.CTREUtil;
import com.first1444.frc.util.MathUtil;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.frc.util.valuemap.ValueMap;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;

import java.util.function.IntSupplier;

import static java.lang.Math.abs;

public class TalonSwerveModule extends SimpleAction implements SwerveModule {
	private static final int CLOSED_LOOP_TIME = 4;
	private static final double WHEEL_CIRCUMFERENCE = 4 * Math.PI;
//	private static final boolean QUICK_REVERSE = true;
	private static final boolean VELOCITY_CONTROL = true;
	
	private final String name;
	
	private final BaseMotorController drive;
	private final TalonSRX steer;
	private final ValueMap<ModuleConfig> moduleConfig;

	private boolean quickReverseAllowed = true;
	private double speed = 0;
	private double targetPositionDegrees = 0;
	
	/** The total distance gone. This is changed in another thread and should only be read*/
	private volatile double totalDistanceGone = 0;
	/** The most recent value for the encoder counts on the steer module. This is changed in another thread and should only be read*/
	private volatile int steerEncoderCountsCache = 0;

	public TalonSwerveModule(String name, int driveID, int steerID,
							 MutableValueMap<PidKey> drivePid, MutableValueMap<PidKey> steerPid,
							 MutableValueMap<ModuleConfig> moduleConfig, ShuffleboardTab debugTab) {
		super(true);
		this.name = name;
		
		drive = new WPI_TalonSRX(driveID);
		steer = new WPI_TalonSRX(steerID);
		this.moduleConfig = moduleConfig;

		drive.configFactoryDefault(Constants.INIT_TIMEOUT);
		steer.configFactoryDefault(Constants.INIT_TIMEOUT);
		
		drive.setNeutralMode(NeutralMode.Brake);
		steer.setNeutralMode(NeutralMode.Coast); // to make them easier to reposition when the robot is on
		drive.configClosedLoopPeriod(Constants.SLOT_INDEX, CLOSED_LOOP_TIME, Constants.INIT_TIMEOUT);
		
		steer.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, Constants.PID_INDEX, Constants.INIT_TIMEOUT);
		steer.configSetParameter(ParamEnum.eFeedbackNotContinuous, 0, 0, 0, Constants.INIT_TIMEOUT);
		steer.setSensorPhase(true);
		steer.configClosedLoopPeriod(Constants.SLOT_INDEX, CLOSED_LOOP_TIME, Constants.INIT_TIMEOUT);

		drivePid.addListener((key) -> CTREUtil.applyPID(drive, drivePid, Constants.LOOP_TIMEOUT));
		steerPid.addListener((key) -> CTREUtil.applyPID(steer, steerPid, Constants.LOOP_TIMEOUT));
		CTREUtil.applyPID(drive, drivePid, Constants.INIT_TIMEOUT);
		CTREUtil.applyPID(steer, steerPid, Constants.INIT_TIMEOUT);
		
		moduleConfig.addListener(option -> {
			updateEncoderOffset(moduleConfig);
		});
		updateEncoderOffset(moduleConfig);
		
		final Thread encoderThread = new Thread(new EncoderRunnable());
		encoderThread.setDaemon(true);
		encoderThread.start();
		
	}
	private void updateEncoderOffset(ValueMap<ModuleConfig> config){
		final int min = (int) config.getDouble(ModuleConfig.MIN_ENCODER_VALUE);
		final int max = (int) config.getDouble(ModuleConfig.MAX_ENCODER_VALUE);
		final int difference = max - min;
//		final int quadEncoderOffset = (int) (config.getDouble(ModuleConfig.ABS_ENCODER_OFFSET) - min) * getCountsPerRevolution() / difference;
//		final int analogQuadCountsPosition = (steer.getSensorCollection().getAnalogInRaw() - min) * getCountsPerRevolution() / difference;
		final int currentPosition = (steer.getSensorCollection().getAnalogInRaw() - (int)config.getDouble(ModuleConfig.ABS_ENCODER_OFFSET)) * getCountsPerRevolution() / difference;
		
		steer.setSelectedSensorPosition(
//				analogQuadCountsPosition - quadEncoderOffset,
				currentPosition,
				Constants.PID_INDEX, Constants.LOOP_TIMEOUT
		);
		steerEncoderCountsCache = currentPosition;
	}
	
	@Override
	public void recalibrate() {
		updateEncoderOffset(moduleConfig);
	}
	
	@Override
	public void setQuickReverseAllowed(boolean quickReverseAllowed) {
		this.quickReverseAllowed = quickReverseAllowed;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		final double speedMultiplier;
		
		{ // steer code
			final int wrap = getCountsPerRevolution(); // in encoder counts
			final int current = steerEncoderCountsCache;
			final int desired = (int) Math.round(targetPositionDegrees * wrap / 360.0); // in encoder counts

			if(quickReverseAllowed){
				final int newPosition = (int) MathUtil.minChange(desired, current, wrap / 2.0) + current;
				if(MathUtil.minDistance(newPosition, desired, wrap) < .001){ // check if equal
					speedMultiplier = 1;
				} else {
					speedMultiplier = -1;
				}
				steer.set(ControlMode.Position, newPosition); // taking .6 ms to 1.7 ms
			} else {
				speedMultiplier = 1;
				final int newPosition = (int) MathUtil.minChange(desired, current, wrap) + current;
				steer.set(ControlMode.Position, newPosition);
			}
		}
		
		{ // speed code
			if(VELOCITY_CONTROL){
				final double velocity = speed * speedMultiplier * Constants.CIMCODER_COUNTS_PER_REVOLUTION
						* Constants.MAX_SWERVE_DRIVE_RPM / (double) Constants.CTRE_UNIT_CONVERSION;
				drive.set(ControlMode.Velocity, velocity); // taking .015 ms
			} else {
				drive.set(ControlMode.PercentOutput, speed * speedMultiplier);
			}
			speed = 0;
		}
	}
	
	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		drive.set(ControlMode.PercentOutput, 0);
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
		final int encoderPosition = steerEncoderCountsCache;
		final int totalCounts = getCountsPerRevolution();
		return MathUtil.mod(encoderPosition * 360.0 / totalCounts, 360.0);
	}

	@Override
	public String getName() {
		return name;
	}
	
	
	/** @return The number of encoder counds per revolution steer*/
	private int getCountsPerRevolution(){
		return Constants.SWERVE_STEER_QUAD_ENCODER_COUNTS_PER_REVOLUTION;
	}
	
	/**
	 * A runnable that should be on its own thread
	 */
	private class EncoderRunnable implements Runnable {
		private static final long SLEEP_MILLIS = 60;
		@Override
		public void run() {
			double totalDistanceGone = 0;
			double lastDistanceInches = 0;
			while(!Thread.currentThread().isInterrupted()){
				final double currentDistance = drive.getSelectedSensorPosition() // takes a long time - .9 ms to 5 ms
						* WHEEL_CIRCUMFERENCE / (double) Constants.SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION;
				totalDistanceGone += abs(currentDistance - lastDistanceInches);
				lastDistanceInches = currentDistance;
				TalonSwerveModule.this.totalDistanceGone = totalDistanceGone;
				
				try {
					Thread.sleep(SLEEP_MILLIS);
				} catch(InterruptedException ex){
					break;
				}
				
				steerEncoderCountsCache = steer.getSelectedSensorPosition(Constants.PID_INDEX);
				
				try {
					Thread.sleep(SLEEP_MILLIS);
				} catch(InterruptedException ex){
					break;
				}
			}
		}
	}
}
