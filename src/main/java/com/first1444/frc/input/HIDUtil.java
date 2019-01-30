package com.first1444.frc.input;

import edu.wpi.first.wpilibj.GenericHID;

final class HIDUtil {
	private HIDUtil() { throw new UnsupportedOperationException(); }

	public static boolean isConnected(GenericHID hid){
		return hid.getAxisCount() > 0 || hid.getButtonCount() > 0 || hid.getPOV() > 0;
	}
}
