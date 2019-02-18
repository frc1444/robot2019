package com.first1444.frc.robot2019.subsystems.implementations;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.subsystems.Lift;
import com.first1444.frc.util.CTREUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import me.retrodaredevil.action.SimpleAction;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class MotorLift extends SimpleAction implements Lift {
	private static final int ENCODER_COUNTS = 24000; // max is 24786
	private static final TalonSRXConfiguration MASTER_CONFIG;
	private static final Map<Position, Double> POSITION_MAP;
	
	static {
		MASTER_CONFIG = new TalonSRXConfiguration();
		
		// reverse limit switch
		MASTER_CONFIG.reverseLimitSwitchSource = LimitSwitchSource.FeedbackConnector;
		
		// forward limit switch
		MASTER_CONFIG.forwardSoftLimitEnable = true;
		MASTER_CONFIG.forwardSoftLimitThreshold = ENCODER_COUNTS;
		
		MASTER_CONFIG.clearPositionOnLimitR = true; // TODO test what this does
		
		POSITION_MAP = Map.of(
				Position.LEVEL1, 0.0,
				Position.CARGO_CARGO_SHIP, .2,
				Position.LEVEL2, .4,
				Position.LEVEL3, .7
		);
	}
	
	private final TalonSRX master;
	private final BooleanSupplier limitDown;
	
	private LiftMode mode = null;
	/** The control based on {@link #mode}. null represents lock*/
	private Double control = 0.0;
	private boolean overrideSpeedSafety = false;
	
	private boolean desiredPositionReached = false;
	
	public MotorLift() {
		super(true);
		master = new TalonSRX(Constants.BOOM_MASTER_ID);
		final DigitalInput digitalInput = new DigitalInput(Constants.BOOM_LIMIT_DIO);
		
		/*
		For whatever reason, when the signal and ground wires aren't connected, or there's nothing connected to the
		DIO port, a value of true is returned. This is the opposite of what you expect which is weird. This means that
		if I do !digitalInput.get(), it's normally open and if I do digitalInput.get(), it's normally closed.
		 */
		limitDown = () -> !digitalInput.get(); // normally open
		CTREUtil.reportError(
				(errorCode, index) -> {
					if(errorCode != ErrorCode.OK) {
						System.err.println("Got error code: " + errorCode + " at index: " + index + " while initializing Lift!");
					}
				},
				() -> master.configFactoryDefault(Constants.INIT_TIMEOUT),
				() -> master.configAllSettings(MASTER_CONFIG, Constants.INIT_TIMEOUT),
				() -> master.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.PID_INDEX, Constants.INIT_TIMEOUT)
		);
		master.setNeutralMode(NeutralMode.Brake);
		master.setSensorPhase(true); // this needs to be called before setInverted
		master.setInverted(InvertType.InvertMotorOutput);
	}
	private static int getEncoderCountsFromPosition(double position){
		if(position < 0 || position > 1){
			throw new IllegalArgumentException();
		}
		return (int) Math.round(position * ENCODER_COUNTS);
	}
	private static double getPositionFromEncoderCounts(int encoderCounts){
		return encoderCounts / (double) ENCODER_COUNTS;
	}
	public void setDesiredPosition(double desiredPosition){
		if(desiredPosition < 0 || desiredPosition > 1){
			throw new IllegalArgumentException();
		}
		mode = LiftMode.POSITION;
		control = desiredPosition;
	}
	
	@Override
	public void setDesiredPosition(Position desiredPosition) {
		Objects.requireNonNull(desiredPosition);
		mode = LiftMode.POSITION;
		control = POSITION_MAP.computeIfAbsent(desiredPosition, key -> { throw new NoSuchElementException("key: " + key + " desiredPosition: " + desiredPosition); });
	}
	
	/**
	 *
	 * @return true if the position set with {@link #setDesiredPosition(double)} or {@link #setDesiredPosition(Position)} is reached
	 */
	@Override
	public boolean isDesiredPositionReached(){
		return false; // TODO make work
	}
	@Override
	public void setManualSpeed(double speed, boolean overrideSpeedSafety){
		if(speed < -1 || speed > 1){
			throw new IllegalArgumentException();
		}
		mode = LiftMode.SPEED;
		control = speed;
		this.overrideSpeedSafety = overrideSpeedSafety;
	}
	
	
	@Override
	public void lockCurrentPosition() { // TODO lock position
		mode = LiftMode.POSITION;
		control = null;
		overrideSpeedSafety = false;
	}
	
	@Override
	public LiftMode getLiftMode() {
		return mode;
	}
	@Override
	protected void onUpdate() {
		super.onUpdate();
		
		final boolean reverseLimit = limitDown.getAsBoolean();
		SmartDashboard.putBoolean("lift limit", reverseLimit);
		Integer encoderCounts = null;
		if(reverseLimit){
			master.setSelectedSensorPosition(0);
			encoderCounts = 0;
		}
		if(control == null){
			if(encoderCounts == null) {
				encoderCounts = master.getSelectedSensorPosition(Constants.PID_INDEX);
				if(encoderCounts < 0){
					encoderCounts = 0;
				}
			}
			control = getPositionFromEncoderCounts(encoderCounts);
			if(mode != LiftMode.POSITION){
				System.err.println("mode is " + mode + " when control was null!");
			}
			mode = LiftMode.POSITION;
		}
		
		if(mode != null) {
			if (mode == LiftMode.SPEED) {
				if (encoderCounts == null) {
					encoderCounts = master.getSelectedSensorPosition(Constants.PID_INDEX);
				}
				final double position = getPositionFromEncoderCounts(encoderCounts);
				final double desiredSpeed = control;
				final double speed;
				if(desiredSpeed > 0){ // up
					if(position > .8){
						speed = desiredSpeed * (1 - position) / .2;
					} else {
						speed = desiredSpeed;
					}
				} else if(desiredSpeed < 0){ // down
					if(position < .2){
						speed = desiredSpeed * position / .2;
					} else {
						speed = desiredSpeed;
					}
				} else{
					speed = desiredSpeed;
				}
				master.set(ControlMode.PercentOutput, speed); // TODO make into velocity
				resetControlCache(); // motor safety
				desiredPositionReached = false;
			} else if (mode == LiftMode.POSITION) {// no motor safety
				if (control == 0 && (encoderCounts != null && encoderCounts <= 0)) {
					master.set(ControlMode.Disabled, 0);
					desiredPositionReached = true;
				} else {
					final int position = getEncoderCountsFromPosition(control);
					master.set(ControlMode.Position, position);
				}
			} else {
				throw new UnsupportedOperationException();
			}
		} else {
			master.set(ControlMode.Disabled, 0);
			resetControlCache();
			desiredPositionReached = false;
		}
	}
	private void resetControlCache(){
		mode = null;
		control = 0.0;
		overrideSpeedSafety = false;
	}
	
}
