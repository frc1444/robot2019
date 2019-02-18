package com.first1444.frc.robot2019.subsystems.implementations;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.subsystems.CargoIntake;
import me.retrodaredevil.action.SimpleAction;

public class MotorCargoIntake extends SimpleAction implements CargoIntake {
	private static final int PICKUP_ENCODER_COUNTS = 50; // TODO Change
	private final BaseMotorController intake;
	private final TalonSRX pivot;
	private enum Preset {STOW, PICKUP, NEUTRAL}
	
	private double intakeSpeed;
	private Preset preset = Preset.STOW;
	
	public MotorCargoIntake(BaseMotorController intake, TalonSRX pivot) {
		super(true);
		this.intake = intake;
		this.pivot = pivot;
		
		intake.configFactoryDefault(Constants.INIT_TIMEOUT);
		pivot.configFactoryDefault(Constants.INIT_TIMEOUT);
		
		
		pivot.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed, Constants.INIT_TIMEOUT);
		pivot.configClearPositionOnLimitR(true, Constants.INIT_TIMEOUT);
		
		pivot.configForwardSoftLimitEnable(true, Constants.INIT_TIMEOUT);
		
		pivot.enableCurrentLimit(true);
		pivot.configContinuousCurrentLimit(1, Constants.INIT_TIMEOUT); // TODO change max amps
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		intake.set(ControlMode.PercentOutput, intakeSpeed);
		
		switch (preset){
			case STOW:
				pivot.set(ControlMode.PercentOutput, -.2); // rely on the limit switch
				break;
			case PICKUP:
				pivot.set(ControlMode.Position, PICKUP_ENCODER_COUNTS); // use CTRE PID stuff
				break;
			case NEUTRAL:
				pivot.set(ControlMode.Disabled, 0);
				break;
			default:
				throw new UnsupportedOperationException();
		}
	}
	
	@Override
	public void setSpeed(double speed) {
		intakeSpeed = speed;
	}
	
	@Override
	public void stow() {
		preset = Preset.STOW;
	}
	
	@Override
	public void pickup() {
		preset = Preset.PICKUP;
	}
}
