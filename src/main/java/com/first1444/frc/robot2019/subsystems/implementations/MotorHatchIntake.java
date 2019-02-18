package com.first1444.frc.robot2019.subsystems.implementations;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.subsystems.HatchIntake;
import me.retrodaredevil.action.SimpleAction;

public class MotorHatchIntake extends SimpleAction implements HatchIntake {
	private static final double GRAB_SPEED = .4;
	private enum GrabMode {NEUTRAL, GRAB, DROP}
	
	private final TalonSRX grabMotor;
	private final TalonSRX pivotMotor;
	private final TalonSRX stowMotor;
	
	private GrabMode grabMode = GrabMode.NEUTRAL;
	private PivotMode mode = PivotMode.SPEED;
	
	
	public MotorHatchIntake(TalonSRX grabMotor, TalonSRX pivotMotor, TalonSRX stowMotor) {
		super(true);
		this.grabMotor = grabMotor;
		this.pivotMotor = pivotMotor;
		this.stowMotor = stowMotor;
		
		grabMotor.configFactoryDefault(Constants.INIT_TIMEOUT);
		pivotMotor.configFactoryDefault(Constants.INIT_TIMEOUT);
		
		grabMotor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, Constants.INIT_TIMEOUT);
		grabMotor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, Constants.INIT_TIMEOUT);
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
	}
	
	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		grabMode = GrabMode.NEUTRAL;
		
		grabMotor.set(ControlMode.Disabled, 0);
		pivotMotor.set(ControlMode.Disabled, 0);
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
	public void groundPosition(){
	
	}
	@Override
	public void readyPosition(){
	
	}
	@Override
	public void stowedPosition(){
	
	}
	
	@Override
	public void setManualPivotSpeed(double speed) {
	
	}
	
	@Override
	public boolean isDesiredPositionReached() {
		return false;
	}
	
	@Override
	public void lockCurrentPosition() {
	
	}
	
	@Override
	public PivotMode getPivotMode() {
		return null;
	}
}
