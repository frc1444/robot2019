package com.first1444.frc.util;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.ValueMap;

public final class CTREUtil {
	private CTREUtil() { throw new UnsupportedOperationException(); }

	public static void applyPID(BaseMotorController motor, ValueMap<PidKey> pid){
		motor.config_kP(0, pid.getDouble(PidKey.P));
		motor.config_kI(0, pid.getDouble(PidKey.I));
		motor.config_kD(0, pid.getDouble(PidKey.D));
		motor.config_kF(0, pid.getDouble(PidKey.F));
		motor.configClosedloopRamp(pid.getDouble(PidKey.CLOSED_RAMP_RATE));
	}
}
