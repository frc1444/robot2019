package com.first1444.frc.robot2019;

import com.first1444.frc.robot2019.subsystems.swerve.ModuleConfig;
import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.frc.util.valuemap.ValueMap;

import java.text.DecimalFormat;

public final class Constants {
	
	private Constants(){ throw new UnsupportedOperationException(); }
	
	public static final boolean DEBUG = true;
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
	
	public static final int LIFT_MASTER_ID = 16; // TODO make accurate
	// endregion
	
	/** Conversion of CTRE units of 100 units/ms*/
	public static final int CTRE_UNIT_CONVERSION = 600;
	private static final int MAX_CIM_RPM = 5300;
	/** The maximum RPM of a drive wheel on the swerve drive*/
	public static final int MAX_SWERVE_DRIVE_RPM = MAX_CIM_RPM;
	/** Talon SRX counts every edge of the quadrature encoder, so 4 * 20 */
	public static final int CIMCODER_COUNTS_PER_REVOLUTION = 80;
	
	public enum Swerve2018 implements SwerveSetup{
		INSTANCE;
		
		@Override public int getFLDriveCAN() { return 4; }
		@Override public int getFRDriveCAN() { return 3; }
		@Override public int getRLDriveCAN() { return 2; }
		@Override public int getRRDriveCAN() { return 1; }
		
		@Override public int getFLSteerCAN() { return 8; }
		@Override public int getFRSteerCAN() { return 7; }
		@Override public int getRLSteerCAN() { return 6; }
		@Override public int getRRSteerCAN() { return 5; }
		
		@Override
		public double getWheelBase() {
			return 27.375;
		}
		
		@Override
		public double getTrackWidth() {
			return 22.25;
		}
		
		@Override
		public MutableValueMap<ModuleConfig> setupFL(MutableValueMap<ModuleConfig> config) {
			return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 147)
					.setDouble(ModuleConfig.MAX_ENCODER_VALUE, 899)
					.setDouble(ModuleConfig.MIN_ENCODER_VALUE, 10);
		}
		
		@Override
		public MutableValueMap<ModuleConfig> setupFR(MutableValueMap<ModuleConfig> config) {
			return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 705)
					.setDouble(ModuleConfig.MAX_ENCODER_VALUE, 891)
					.setDouble(ModuleConfig.MIN_ENCODER_VALUE, 12);
		}
		
		@Override
		public MutableValueMap<ModuleConfig> setupRL(MutableValueMap<ModuleConfig> config) {
			return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 775)
					.setDouble(ModuleConfig.MAX_ENCODER_VALUE, 872)
					.setDouble(ModuleConfig.MIN_ENCODER_VALUE, 13);
		}
		
		@Override
		public MutableValueMap<ModuleConfig> setupRR(MutableValueMap<ModuleConfig> config) {
			return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 604)
					.setDouble(ModuleConfig.MAX_ENCODER_VALUE, 895)
					.setDouble(ModuleConfig.MIN_ENCODER_VALUE, 9);
		}
	}
	
	public enum Swerve2019 implements SwerveSetup{
		INSTANCE;
		
		@Override public int getFLDriveCAN() { return 4; }
		@Override public int getFRDriveCAN() { return 3; }
		@Override public int getRLDriveCAN() { return 2; }
		@Override public int getRRDriveCAN() { return 1; }
		
		@Override public int getFLSteerCAN() { return 8; }
		@Override public int getFRSteerCAN() { return 7; }
		@Override public int getRLSteerCAN() { return 6; }
		@Override public int getRRSteerCAN() { return 5; }
		
		@Override
		public double getWheelBase() {
			return 22.75;
		}
		
		@Override
		public double getTrackWidth() {
			return 24;
		}
		
		@Override
		public MutableValueMap<ModuleConfig> setupFL(MutableValueMap<ModuleConfig> config) {
			return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 147)
					.setDouble(ModuleConfig.MAX_ENCODER_VALUE, 899)
					.setDouble(ModuleConfig.MIN_ENCODER_VALUE, 10);
		}
		
		@Override
		public MutableValueMap<ModuleConfig> setupFR(MutableValueMap<ModuleConfig> config) {
			return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 705)
					.setDouble(ModuleConfig.MAX_ENCODER_VALUE, 891)
					.setDouble(ModuleConfig.MIN_ENCODER_VALUE, 12);
		}
		
		@Override
		public MutableValueMap<ModuleConfig> setupRL(MutableValueMap<ModuleConfig> config) {
			return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 775)
					.setDouble(ModuleConfig.MAX_ENCODER_VALUE, 872)
					.setDouble(ModuleConfig.MIN_ENCODER_VALUE, 13);
		}
		
		@Override
		public MutableValueMap<ModuleConfig> setupRR(MutableValueMap<ModuleConfig> config) {
			return config.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 604)
					.setDouble(ModuleConfig.MAX_ENCODER_VALUE, 895)
					.setDouble(ModuleConfig.MIN_ENCODER_VALUE, 9);
		}
	}
	
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
