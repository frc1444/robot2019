package com.first1444.frc.robot2019;

import java.text.DecimalFormat;

public final class Constants {
	private Constants(){ throw new UnsupportedOperationException(); }
	
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(" #0.00;-#0.00");
	
	public static final int SWERVE_STEER_ABSOLUTE_ENCODER_COUNTS_PER_REVOLUTION = 1024;
	public static final int SWERVE_STEER_QUAD_ENCODER_COUNTS_PER_REVOLUTION = 1657;
	public static final int SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION = 534;
	
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
