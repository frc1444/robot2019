package com.first1444.frc.util;

@Deprecated
public interface Pid {
	double getP();
	double getI();
	double getD();
	double getF();
	int getIndex();
	double getClosedRampRate();
}
