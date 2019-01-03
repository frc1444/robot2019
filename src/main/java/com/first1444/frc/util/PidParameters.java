package com.first1444.frc.util;

/**
 * An immutable implementation of {@link Pid}
 */
@Deprecated
class PidParameters implements Pid {
	private final double p, i, d, f;
	private final int index;
	private final double closedRampRate;

	PidParameters(double p, double i, double d, double f, int index, double closedRampRate){
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
		this.index = index;
		this.closedRampRate = closedRampRate;
	}

	@Override
	public double getP() {
        return p;
	}

	@Override
	public double getI() {
        return i;
	}

	@Override
	public double getD() {
        return d;
	}

	@Override
	public double getF() {
        return f;
	}

	@Override
	public int getIndex() {
        return index;
	}

	@Override
	public double getClosedRampRate() {
        return closedRampRate;
	}

}
