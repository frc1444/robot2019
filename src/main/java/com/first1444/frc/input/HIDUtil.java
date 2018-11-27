package com.first1444.frc.input;

import edu.wpi.first.wpilibj.GenericHID;

final class HIDUtil {
	private HIDUtil() { throw new UnsupportedOperationException(); }

	public static boolean isConnected(GenericHID hid){
		String name = hid.getName();
		GenericHID.HIDType type = hid.getType();
		return name != null && !name.isEmpty() && type != null
				&& type != GenericHID.HIDType.kUnknown && type != GenericHID.HIDType.kXInputUnknown;
	}
}
