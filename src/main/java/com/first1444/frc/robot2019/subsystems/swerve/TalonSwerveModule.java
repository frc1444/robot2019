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
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;

import java.util.function.IntSupplier;

import static java.lang.Math.abs;

public class TalonSwerveModule extends SimpleAction implements SwerveModule {
	private static final double WHEEL_CIRCUMFERENCE = 4 * Math.PI;
	private static final boolean USE_ABSOLUTE_ENCODERS = false;
	private static final boolean QUICK_REVERSE = true;
	private static final boolean VELOCITY_CONTROL = true;
	
	private final BaseMotorController drive;
	private final TalonSRX steer;

	private final String name;
	private final IntSupplier absoluteEncoderOffsetSupplier;
	
	private EncoderType currentEncoderType = null;

	private double speed = 0;
	private double targetPositionDegrees = 0;
	
	/** The total distance gone. Make sure that you synchronize when accessing and modifying*/
	private double totalDistanceGone = 0;
	/** The most recent value for the encoder counts on the steer module. Make sure that you synchronize when accessing and modifying.*/
	private int steerEncoderCountsCache = 0;

	public TalonSwerveModule(String name, int driveID, int steerID,
							 MutableValueMap<PidKey> drivePid, MutableValueMap<PidKey> steerPid,
							 MutableValueMap<ModuleConfig> moduleConfig, ShuffleboardTab debugTab) {
		super(true);
		this.name = name;
		absoluteEncoderOffsetSupplier = () -> (int) moduleConfig.getDouble(ModuleConfig.ABS_ENCODER_OFFSET);
		
		drive = new WPI_TalonSRX(driveID);
		steer = new WPI_TalonSRX(steerID);

		drive.configFactoryDefault();
		steer.configFactoryDefault();
		
		drive.setNeutralMode(NeutralMode.Brake);
		steer.setNeutralMode(NeutralMode.Coast); // to make them easier to reposition when the robot is on

		drivePid.addListener((key) -> CTREUtil.applyPID(drive, drivePid, Constants.LOOP_TIMEOUT));
		steerPid.addListener((key) -> CTREUtil.applyPID(steer, steerPid, Constants.LOOP_TIMEOUT));
		CTREUtil.applyPID(drive, drivePid, Constants.INIT_TIMEOUT);
		CTREUtil.applyPID(steer, steerPid, Constants.INIT_TIMEOUT);
		
		moduleConfig.addListener(option -> {
			if(option == ModuleConfig.ABS_ENCODER_OFFSET){
				updateAbsoluteEncoderOffset();
			}
		});
		if(USE_ABSOLUTE_ENCODERS) {
			switchToAbsoluteEncoder();
		} else {
			switchToQuadEncoder(); // TODO Because of this, the wheels must be in the correct position when starting
		}
		
		final Thread encoderThread = new Thread(new EncoderRunnable());
		encoderThread.setDaemon(true);
		encoderThread.start();
		
	}
	private void switchToAbsoluteEncoder(){
		if(currentEncoderType == EncoderType.ABSOLUTE){
			return;
		}
		steer.configSelectedFeedbackSensor(FeedbackDevice.Analog);
		steer.configSetParameter(ParamEnum.eAnalogPosition, 0, 0, 0);
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
	
	@Override
	public Action getCalibrateAction() {
		if(!USE_ABSOLUTE_ENCODERS){
			return null;
		}
		return new SimpleAction(false){
			Long doneAt = null;
			@Override
			protected void onStart() {
				super.onStart();
				switchToAbsoluteEncoder();
				System.out.println("Starting " + getName());
			}
	
			@Override
			protected void onUpdate() {
				super.onUpdate();
				setTargetAngle(0);
				setTargetSpeed(0);
				if(doneAt != null && doneAt <= System.currentTimeMillis()){
					setDone(true);
				}
				if(doneAt == null && abs(getCurrentAngle()) < 5){
					doneAt = System.currentTimeMillis() + 300;
					System.out.println("Current angle: " + getCurrentAngle());
				}
			}
	
			@Override
			protected void onEnd(boolean peacefullyEnded) {
				super.onEnd(peacefullyEnded);
				switchToQuadEncoder();
			}
		};
	}
	
	@Override
	protected void onUpdate() { // takes about 5 ms total
		super.onUpdate();
		final double speedMultiplier;
		
		{ // steer code
			final int wrap = getCountsPerRevolution(); // in encoder counts
			final int current;
			synchronized (this){
				current = steerEncoderCountsCache;
			}
			final int desired = (int) Math.round(targetPositionDegrees * wrap / 360.0); // in encoder counts

			if(QUICK_REVERSE){
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
	public synchronized double getTotalDistanceTraveledInches() {
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
	
	/**
	 * A runnable that should be on its own thread
	 */
	private class EncoderRunnable implements Runnable {
		private static final long SLEEP_MILLIS = 60;
		@Override
		public void run() {
			double lastDistanceInches = 0;
			while(!Thread.currentThread().isInterrupted()){
				final double currentDistance = drive.getSelectedSensorPosition() // takes a long time - .9 ms to 5 ms
						* WHEEL_CIRCUMFERENCE / (double) Constants.SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION;
				synchronized (TalonSwerveModule.this) {
					totalDistanceGone += abs(currentDistance - lastDistanceInches);
				}
				lastDistanceInches = currentDistance;
				
				try {
					Thread.sleep(SLEEP_MILLIS);
				} catch(InterruptedException ex){
					break;
				}
				
				final int current = steer.getSelectedSensorPosition(Constants.PID_INDEX); // in encoder counts // takes .4 to 1 ms and sometimes even 4 ms
				synchronized (TalonSwerveModule.this){
					steerEncoderCountsCache = current;
				}
				
				try {
					Thread.sleep(SLEEP_MILLIS);
				} catch(InterruptedException ex){
					break;
				}
			}
		}
	}
}
