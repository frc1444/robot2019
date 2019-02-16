package com.first1444.frc.robot2019.subsystems.implementations;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.subsystems.Climber;
import me.retrodaredevil.action.SimpleAction;

public class MotorClimber extends SimpleAction implements Climber {
	
	private final BaseMotorController climbMotor;
	private final BaseMotorController driveMotor;
	
	private double climbSpeed;
	private double driveSpeed;
	
	public MotorClimber(BaseMotorController climbMotor, BaseMotorController driveMotor) {
		super(true);
		this.climbMotor = climbMotor;
		this.driveMotor = driveMotor;
		
		climbMotor.configFactoryDefault(Constants.INIT_TIMEOUT);
		driveMotor.configFactoryDefault(Constants.INIT_TIMEOUT);
		
		climbMotor.setSelectedSensorPosition(0, Constants.PID_INDEX, Constants.INIT_TIMEOUT);
		
		climbMotor.configForwardSoftLimitEnable(true);
		climbMotor.configForwardSoftLimitThreshold(100, Constants.INIT_TIMEOUT); // TODO temp value
		
		climbMotor.configReverseSoftLimitEnable(true);
		climbMotor.configReverseSoftLimitThreshold(5, Constants.INIT_TIMEOUT);
	}
	
	/** @param speed The speed of the climber. A positive value raises the robot by pushing the climber down, a negative value retracts. */
	public void setClimbSpeed(double speed){
		this.climbSpeed = speed;
	}
	/** @param speed The speed of the wheels on the climber. A positive value makes the robot go forward. A negative value goes backwards and is usually never used. */
	public void setDriveSpeed(double speed){
		this.driveSpeed = speed;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		climbSpeed = 0;
		driveSpeed = 0;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		climbMotor.set(ControlMode.PercentOutput, climbSpeed);
		driveMotor.set(ControlMode.PercentOutput, driveSpeed);
	}
	private void checkPosition(){
		final int position = climbMotor.getSelectedSensorPosition(Constants.PID_INDEX);
		if(position < 0){
			climbMotor.setSelectedSensorPosition(0, Constants.PID_INDEX, Constants.LOOP_TIMEOUT);
		}
	}
}
