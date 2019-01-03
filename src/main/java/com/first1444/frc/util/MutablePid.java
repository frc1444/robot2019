package com.first1444.frc.util;

@Deprecated
public class MutablePid implements Pid{
	private double p, i, d, f;
	private int index;
	private double closedRampRate;

	private Pid cachedPid = null;

	public MutablePid(){

	}
	public MutablePid(Pid pid){
		set(pid);
	}

	public MutablePid setP(double p){
		this.p = p;
		cachedPid = null;
		return this;
	}
	public MutablePid setI(double i){
		this.i = i;
		cachedPid = null;
		return this;
	}
	public MutablePid setD(double d){
		this.d = d;
		cachedPid = null;
		return this;
	}
	public MutablePid setF(double f){
		this.f = f;
		cachedPid = null;
		return this;
	}
	public MutablePid setIndex(int index){
		this.index = index;
		cachedPid = null;
		return this;
	}
	public MutablePid setClosedRampRate(double closedRampRate){
		this.closedRampRate = closedRampRate;
		cachedPid = null;
		return this;
	}
	public MutablePid set(Pid pid){
		p = pid.getP();
		i = pid.getI();
		d = pid.getD();
		f = pid.getF();
		index = pid.getIndex();
		closedRampRate = pid.getClosedRampRate();
		return this;
	}

	/** @return A new immutable Pid */
	public Pid build(){
		if(cachedPid != null){
			return cachedPid;
		}
		return cachedPid = new PidParameters(p, i, d, f, index, closedRampRate);
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
