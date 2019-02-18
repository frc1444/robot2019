package com.first1444.frc.robot2019.subsystems.implementations;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.subsystems.HatchIntake;
import me.retrodaredevil.action.SimpleAction;

public class MotorHatchIntake extends SimpleAction implements HatchIntake {
	private static final double GRAB_SPEED = .4;
	private static final double PIVOT_SPEED = .2;
	private static final int STOW_MOTOR_MAX_ENCODER_COUNTS = 50; // TODO change
	private enum GrabMode {NEUTRAL, GRAB, DROP}
	private enum Preset {GROUND, NORMAL, STOWED, NEUTRAL}
	
	private final TalonSRX grabMotor;
	/** The stow motor. This has a reverse limit switch and uses an encoder*/
	private final TalonSRX stowMotor;
	/** The pivot motor. This uses two limit switches. One for reverse and one for forward*/
	private final TalonSRX pivotMotor;
	
	private GrabMode grabMode = GrabMode.NEUTRAL;
	private Preset preset = Preset.NEUTRAL;
	
	
	public MotorHatchIntake(TalonSRX grabMotor, TalonSRX stowMotor, TalonSRX pivotMotor) {
		super(true);
		this.grabMotor = grabMotor;
		this.pivotMotor = pivotMotor;
		this.stowMotor = stowMotor;
		
		grabMotor.configFactoryDefault(Constants.INIT_TIMEOUT);
		stowMotor.configFactoryDefault(Constants.INIT_TIMEOUT);
		pivotMotor.configFactoryDefault(Constants.INIT_TIMEOUT);
		
		// Grab
		grabMotor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, Constants.INIT_TIMEOUT);
		grabMotor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, Constants.INIT_TIMEOUT);
		grabMotor.setNeutralMode(NeutralMode.Brake);
		
		// Stow
		stowMotor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, Constants.INIT_TIMEOUT);
		stowMotor.configForwardSoftLimitEnable(true, Constants.INIT_TIMEOUT);
		stowMotor.configForwardSoftLimitThreshold(STOW_MOTOR_MAX_ENCODER_COUNTS, Constants.INIT_TIMEOUT);
		stowMotor.configClearPositionOnLimitR(true, Constants.INIT_TIMEOUT);
		stowMotor.setNeutralMode(NeutralMode.Brake);
		
		// Pivot
		pivotMotor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, Constants.INIT_TIMEOUT);
		pivotMotor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, Constants.INIT_TIMEOUT);
		pivotMotor.setNeutralMode(NeutralMode.Brake);
		
	}
	private boolean isStowFullyBack(){
		return stowMotor.getSensorCollection().isRevLimitSwitchClosed();
	}
	private boolean isStowFullyForward(){
		return stowMotor.getSelectedSensorPosition(Constants.PID_INDEX) >= STOW_MOTOR_MAX_ENCODER_COUNTS;
	}
	private boolean isPivotBack(){
		return pivotMotor.getSensorCollection().isRevLimitSwitchClosed();
	}
	private boolean isPivotDown(){
		return pivotMotor.getSensorCollection().isFwdLimitSwitchClosed();
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		switch(grabMode){
			case NEUTRAL:
				grabMotor.set(ControlMode.Disabled, 0);
				break;
			case GRAB:
				grabMotor.set(ControlMode.PercentOutput, GRAB_SPEED);
				break;
			case DROP:
				grabMotor.set(ControlMode.PercentOutput, -GRAB_SPEED);
				break;
		}
		switch(preset){
			case GROUND: // TODO Do we really want to use STOW_MOTOR_MAX_ENCODER_COUNTS and position control for the stow?
				stowMotor.set(ControlMode.Position, STOW_MOTOR_MAX_ENCODER_COUNTS);
				if(isStowFullyForward()){
					pivotMotor.set(ControlMode.PercentOutput, PIVOT_SPEED);
				}
				break;
			case NORMAL:
				stowMotor.set(ControlMode.Position, STOW_MOTOR_MAX_ENCODER_COUNTS);
				pivotMotor.set(ControlMode.PercentOutput, -PIVOT_SPEED);
				break;
			case STOWED:
				
				break;
		}
	}
	
	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		grabMode = GrabMode.NEUTRAL;
		preset = Preset.NEUTRAL;
		
		grabMotor.set(ControlMode.Disabled, 0);
		pivotMotor.set(ControlMode.Disabled, 0);
		stowMotor.set(ControlMode.Disabled, 0);
	}
	
	@Override
	public void hold(){
		grabMode = GrabMode.GRAB;
	}
	@Override
	public void drop(){
		grabMode = GrabMode.DROP;
	}
	
	@Override
	public void neutralHold() {
		grabMode = GrabMode.NEUTRAL;
	}
	
	@Override
	public void groundPosition(){
		preset = Preset.GROUND;
	}
	@Override
	public void readyPosition(){
		preset = Preset.NORMAL;
	}
	@Override
	public void stowedPosition(){
		preset = Preset.STOWED;
	}
	
	@Override
	public boolean isDesiredPositionReached() {
		return false;
	}
	
}
