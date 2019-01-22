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
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;

import java.util.function.IntSupplier;

import static java.lang.Math.abs;

public class TalonSwerveModule extends SimpleAction implements SwerveModule {
	private static final double WHEEL_CIRCUMFERENCE = 4 * Math.PI;
	private static final boolean SHOULD_CALIBRATE = false;
	private static final boolean QUICK_REVERSE = true;
	
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
							 MutableValueMap<ModuleConfig> moduleConfig, ShuffleboardTab debugTab) {
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
		switchToQuadEncoder(); // TODO Because of this, the wheels must be in the correct position when starting
		
		debugTab.add(getName() + " debug", new SendableBase() {
			@Override
			public void initSendable(SendableBuilder builder) {
				builder.addStringProperty("current position", () -> "" + steer.getSelectedSensorPosition(), null);
				builder.addStringProperty("quad position", () -> "" + steer.getSensorCollection().getQuadraturePosition(), null);
				builder.addStringProperty("absolute position", () -> "" + steer.getSensorCollection().getAnalogInRaw(), null);
				builder.addStringProperty("pw position", () -> "" + steer.getSensorCollection().getPulseWidthPosition(), null);
				builder.addDoubleProperty("angle degrees", () -> getCurrentAngle(), null);
			}
		});
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
	private double getCurrentDistanceInches(){
		return drive.getSelectedSensorPosition() * WHEEL_CIRCUMFERENCE / (double) Constants.SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION;
	}
	
	@Override
	public Action getCalibrateAction() {
		if(!SHOULD_CALIBRATE){
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
	protected void onUpdate() {
		super.onUpdate();
		{ // encoder code
			final double currentDistance = getCurrentDistanceInches();
			totalDistanceGone += abs(currentDistance - lastDistanceInches);
			lastDistanceInches = currentDistance;
		}
		final double speedMultiplier;
		
		{ // steer code
			final int wrap = getCountsPerRevolution(); // in encoder counts
			final int current = steer.getSelectedSensorPosition(); // in encoder counts
			final int desired = (int) Math.round(targetPositionDegrees * wrap / 360.0); // in encoder counts

			if(QUICK_REVERSE){
				final int newPosition = (int) MathUtil.minChange(desired, current, wrap / 2.0) + current;
				if(MathUtil.minDistance(newPosition, desired, wrap) < .001){
					speedMultiplier = 1;
				} else {
					speedMultiplier = -1;
				}
				steer.set(ControlMode.Position, newPosition);
			} else {
				speedMultiplier = 1;
				final int newPosition = (int) MathUtil.minChange(desired, current, wrap) + current;
				steer.set(ControlMode.Position, newPosition);
			}
		}
		
		{ // speed code
			drive.set(ControlMode.PercentOutput, speed * speedMultiplier);
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
