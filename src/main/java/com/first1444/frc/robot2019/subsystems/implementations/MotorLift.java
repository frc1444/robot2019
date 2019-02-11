package com.first1444.frc.robot2019.subsystems.implementations;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.subsystems.Lift;
import com.first1444.frc.util.CTREUtil;
import me.retrodaredevil.action.SimpleAction;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class MotorLift extends SimpleAction implements Lift {
	private static final int ENCODER_COUNTS = 30000; // TODO Change
	private static final TalonSRXConfiguration MASTER_CONFIG;
	private static final Map<Position, Double> POSITION_MAP = new HashMap<>();
	
	static {
		MASTER_CONFIG = new TalonSRXConfiguration();
		
		// reverse limit switch
		MASTER_CONFIG.reverseLimitSwitchSource = LimitSwitchSource.FeedbackConnector;
		
		// forward limit switch
		MASTER_CONFIG.forwardSoftLimitEnable = true;
		MASTER_CONFIG.forwardSoftLimitThreshold = ENCODER_COUNTS;
		
		MASTER_CONFIG.clearPositionOnLimitR = true; // TODO test what this does
		
		POSITION_MAP.put(Position.LEVEL1, .1);
		POSITION_MAP.put(Position.CARGO_CARGO_SHIP, .2);
		POSITION_MAP.put(Position.LEVEL2, .4);
		POSITION_MAP.put(Position.LEVEL3, .7);
	}
	
	private final TalonSRX master;
	
	private LiftMode mode = null;
	private double control = 0;
	
	public MotorLift() {
		super(true);
		master = new WPI_TalonSRX(Constants.LIFT_MASTER_ID);
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
	}
	private static int getEncoderCountsFromPosition(double position){
		if(position < 0 || position > 1){
			throw new IllegalArgumentException();
		}
		return (int) Math.round(position * ENCODER_COUNTS);
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
	 * @return true if the position set with {@link #setDesiredPosition(double)} or {@link #setPositionCargoIntake()} is reached
	 */
	public boolean isDesiredPositionReached(){
		return false;
	}
	public void setPositionCargoIntake(){
		setDesiredPosition(0); // TODO make this work
	}
	public void setManualSpeed(double speed, boolean canPickupCargo){
		if(speed < -1 || speed > 1){
			throw new IllegalArgumentException();
		}
		
	}
	
	@Override
	public void lockCurrentPosition() {
	
	}
	
	@Override
	public LiftMode getLiftMode() {
		return mode;
	}
	@Override
	protected void onUpdate() {
		super.onUpdate();
		if(mode != null) {
			switch (mode) {
				case SPEED:
					master.set(ControlMode.PercentOutput, control); // TODO make into velocity
					resetControlCache();
					break;
				case POSITION:
					final int position = getEncoderCountsFromPosition(control);
					master.set(ControlMode.Position, position);
					break;
				default:
					throw new UnsupportedOperationException();
			}
		} else {
			master.set(ControlMode.Disabled, 0);
			resetControlCache();
		}
	}
	private void resetControlCache(){
		mode = null;
		control = 0;
	}
	
	public static final class PositionValues {
		private PositionValues() { throw new UnsupportedOperationException(); }
		
	}
}