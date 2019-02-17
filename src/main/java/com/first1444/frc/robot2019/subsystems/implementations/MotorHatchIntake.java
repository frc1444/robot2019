package com.first1444.frc.robot2019.subsystems.implementations;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.subsystems.HatchIntake;
import me.retrodaredevil.action.SimpleAction;

public class MotorHatchIntake extends SimpleAction implements HatchIntake {
	private final BaseMotorController grabMotor;
	private final BaseMotorController pivotMotor;
	
	private boolean grabbing = false;
	private PivotMode mode = PivotMode.SPEED;
	
	
	public MotorHatchIntake(TalonSRX grabMotor, BaseMotorController pivotMotor) {
		super(true);
		this.grabMotor = grabMotor;
		this.pivotMotor = pivotMotor;
		
		grabMotor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, Constants.INIT_TIMEOUT);
		grabMotor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, Constants.INIT_TIMEOUT);
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
	}
	
	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		
		grabMotor.set(ControlMode.Disabled, 0);
		pivotMotor.set(ControlMode.Disabled, 0);
	}
	
	public void hold(){
		grabbing = true;
	}
	public void drop(){
		grabbing = false;
	}
	
	public void groundPosition(){
	
	}
	public void readyPosition(){
	
	}
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
