package com.first1444.frc.robot2019.matchrun;

import java.util.Objects;

class ImmutableMatchTime implements MatchTime{
	private final double time;
	private final Mode mode;
	private final Type type;
	
	ImmutableMatchTime(double time, Mode mode, Type type) {
		this.time = time;
		this.mode = Objects.requireNonNull(mode);
		this.type = Objects.requireNonNull(type);
		if(Double.isNaN(time) || Double.isInfinite(time)){
			throw new IllegalArgumentException("time cannot be infinite or NaN. time=" + time);
		}
	}
	
	@Override
	public double getTime() {
		return time;
	}
	
	@Override
	public Mode getMode() {
		return mode;
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MatchTime){
			final MatchTime matchTime = (MatchTime) obj;
			return matchTime.getTime() == time
					&& matchTime.getMode() == mode
					&& matchTime.getType() == type;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(time, mode, type);
	}
}
