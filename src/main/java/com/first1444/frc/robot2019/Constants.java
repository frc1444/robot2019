package com.first1444.frc.robot2019;

import java.text.DecimalFormat;

public final class Constants {
	
	private Constants(){ throw new UnsupportedOperationException(); }
	
	public static final boolean DEBUG = false;
	public static final boolean PRINT_DEBUG = true;
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(" #0.00;-#0.00");
	
	public static final int PID_INDEX = 0;
	public static final int SLOT_INDEX = 0;
	public static final int INIT_TIMEOUT = 10;
	public static final int LOOP_TIMEOUT = 3;
	public static final int LOOP_TIMEOUT_THREAD = LOOP_TIMEOUT;
	
	/** The number of encoder counts per revolution on a steer wheel on the swerve drive when using the absolute encoders*/
	public static final int SWERVE_STEER_ABSOLUTE_ENCODER_COUNTS_PER_REVOLUTION = 1024;
	/** The number of encoder counts per revolution on a steer wheel on the swerve drive when using the quad encoders*/
	public static final int SWERVE_STEER_QUAD_ENCODER_COUNTS_PER_REVOLUTION = 1657;
	/** The number of encoder counts per revolution on a drive wheel on the swerve drive*/
	public static final int SWERVE_DRIVE_ENCODER_COUNTS_PER_REVOLUTION = 534;
	
	// region CAN IDs
	public static final int FL_DRIVE = 4;
	public static final int FR_DRIVE = 3;
	public static final int RL_DRIVE = 2;
	public static final int RR_DRIVE = 1;
	
	public static final int FL_STEER = 8;
	public static final int FR_STEER = 7;
	public static final int RL_STEER = 6;
	public static final int RR_STEER = 5;
	
	public static final int LIFT_MASTER_ID = 16; // TODO make accurate
	// endregion
	
	/** Conversion of CTRE units of 100 units/ms*/
	public static final int CTRE_UNIT_CONVERSION = 600;
	private static final int MAX_CIM_RPM = 5300;
	/** The maximum RPM of a drive wheel on the swerve drive*/
	public static final int MAX_SWERVE_DRIVE_RPM = MAX_CIM_RPM;
	/** Talon SRX counts every edge of the quadrature encoder, so 4 * 20 */
	public static final int CIMCODER_COUNTS_PER_REVOLUTION = 80;
	
	public enum Dimensions implements RobotDimensions {
		INSTANCE;
		
		@Override
		public Perspective getHatchManipulatorPerspective() {
			return Perspective.ROBOT_FORWARD_CAM;
		}
		
		@Override
		public Perspective getCargoManipulatorPerspective() {
			return Perspective.ROBOT_BACK_CAM;
		}
		
		@Override
		public int getHatchCameraID() {
			return 0;
		}
		
		@Override
		public int getCargoCameraID() {
			return 1;
		}
	}
	
}
