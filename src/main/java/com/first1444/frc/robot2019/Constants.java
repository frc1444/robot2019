package com.first1444.frc.robot2019;

import java.text.DecimalFormat;

public final class Constants {
	private Constants(){ throw new UnsupportedOperationException(); }
	
	public static final DecimalFormat format = new DecimalFormat(" #0.00;-#0.00");
	
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
