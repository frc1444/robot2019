package com.first1444.frc.robot2019.subsystems;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import me.retrodaredevil.action.SimpleAction;

public class HatchIntake extends SimpleAction {
	private final BaseMotorController grabMotor;
	private final BaseMotorController pivotMotor;
	public HatchIntake(BaseMotorController grabMotor, BaseMotorController pivotMotor) {
		super(true);
		this.grabMotor = grabMotor;
		this.pivotMotor = pivotMotor;
	}
	public void hold(){
	
	}
	public void drop(){
		
	}
	
	public void groundPosition(){
	
	}
	public void readyPosition(){
	
	}
	public void stowedPosition(){
	
	}
	
}
