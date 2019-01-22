package com.first1444.frc.robot2019;

import java.text.DecimalFormat;

public final class Constants {
	private Constants(){ throw new UnsupportedOperationException(); }
	
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(" #0.00;-#0.00");
	
	public static final int SWERVE_STEER_ABSOLUTE_ENCODER_COUNTS_PER_REVOLUTION = 1024;
	public static final int SWERVE_STEER_QUAD_ENCODER_COUNTS_PER_REVOLUTION = 1657;
	public static final int SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION = 534;
	
	public static final int FL_DRIVE = 4;
	public static final int FR_DRIVE = 3;
	public static final int RL_DRIVE = 2;
	public static final int RR_DRIVE = 1;
	public static final int FL_STEER = 8;
	public static final int FR_STEER = 7;
	public static final int RL_STEER = 6;
	public static final int RR_STEER = 5;
	
	public enum Dimensions implements RobotDimensions {
		INSTANCE;
		
		@Override
		public double getHatchManipulatorOrientationOffset() {
			return 0;
		}
		
		@Override
		public double getForwardCargoManipulatorOffsetAngle() {
			return 180;
		}
	}
	
}
