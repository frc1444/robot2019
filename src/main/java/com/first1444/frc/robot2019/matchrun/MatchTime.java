package com.first1444.frc.robot2019.matchrun;

public interface MatchTime {
	
	/**
	 *
	 * @return The time in seconds representing the number of seconds after the start, or from the end of the {@link Mode}
	 */
	double getTime();
	/** @return A nonnull value representing the {@link Mode}*/
	Mode getMode();
	/** @return A nonnull value representing the {@link Type} which represents if {@link #getTime()} represents a time after the mode starts, or if it represents a time before the mode ends*/
	Type getType();
	
	enum Mode {
		AUTONOMOUS, TELEOP
	}
	enum Type {
		AFTER_START, FROM_END
	}
	static MatchTime of(double time, Mode mode, Type type){
		return new ImmutableMatchTime(time, mode, type);
	}
}
