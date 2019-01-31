package com.first1444.frc.robot2019.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.util.CTREUtil;
import me.retrodaredevil.action.SimpleAction;

public class Lift extends SimpleAction {
	private static final int ENCODER_COUNTS = 30000; // TODO Change
	private static final TalonSRXConfiguration MASTER_CONFIG;
	static {
		MASTER_CONFIG = new TalonSRXConfiguration();
		
		// reverse limit switch
		MASTER_CONFIG.reverseLimitSwitchSource = LimitSwitchSource.FeedbackConnector;
		
		// forward limit switch
		MASTER_CONFIG.forwardSoftLimitEnable = true;
		MASTER_CONFIG.forwardSoftLimitThreshold = ENCODER_COUNTS;
		
		MASTER_CONFIG.clearPositionOnLimitR = true; // TODO test what this does
	}
	private final TalonSRX master;
	
	private double desiredPosition;
	
	public Lift() {
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
	public void setDesiredPosition(double desiredPosition){
		if(desiredPosition < 0 || desiredPosition > 1){
			throw new IllegalArgumentException();
		}
		this.desiredPosition = desiredPosition;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
	}
	
	
	public static final class Position {
		private Position() { throw new UnsupportedOperationException(); }
		
		public static final double HATCH_CARGO_SHIP = .1;
		public static final double CARGO_CARGO_SHIP = .3;
		
		public static final double HATCH_LEVEL1 = HATCH_CARGO_SHIP;
		public static final double HATCH_LEVEL2 = .4;
		public static final double HATCH_LEVEL3 = .7;
		
		public static final double CARGO_LEVEL1 = .2;
		public static final double CARGO_LEVEL2 = .5;
		public static final double CARGO_LEVEL3 = .8;
	}
}
