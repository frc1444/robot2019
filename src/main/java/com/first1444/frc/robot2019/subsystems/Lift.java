package com.first1444.frc.robot2019.subsystems;


public interface Lift {
	void setDesiredPosition(double desiredPosition);
	
	/**
	 *
	 * @return true if the position set with {@link #setDesiredPosition(double)} or {@link #setPositionCargoIntake()} is reached
	 */
	boolean isDesiredPositionReached();
	void setPositionCargoIntake();
	void setManualSpeed(double speed);
	
	public static final class Position {
		private Position() { throw new UnsupportedOperationException(); }
		
		public static final double HATCH_CARGO_SHIP = .1;
		public static final double CARGO_CARGO_SHIP = .3;
		
		public static final double LEVEL1 = HATCH_CARGO_SHIP;
		public static final double LEVEL2 = .4;
		public static final double LEVEL3 = .7;
		
	}
}
