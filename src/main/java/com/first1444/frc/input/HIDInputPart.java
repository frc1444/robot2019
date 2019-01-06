package com.first1444.frc.input;

import edu.wpi.first.wpilibj.GenericHID;
import me.retrodaredevil.controller.input.AutoCachingInputPart;
import me.retrodaredevil.controller.input.AxisType;

class HIDInputPart extends AutoCachingInputPart {

	private final GenericHID hid;
	private final int code;
	private final boolean inverted;
	private final boolean isAxis;

	/**
	 *
	 * @param type The axis type. {@link AxisType#isFull()} determines the range of {@link #getPosition()}
	 * @param hid The HID device to get raw axi and button presses from
	 * @param code If isAxis, starts from 0, otherwise starts from 1 if it's a button
	 * @param inverted True if it's inverted
	 * @param isAxis true if it is an axis, false if it's a button
	 */
	public HIDInputPart(AxisType type, GenericHID hid, int code, boolean inverted, boolean isAxis) {
		super(type);
		this.hid = hid;
		this.code = code;
		this.inverted = inverted;
		this.isAxis = isAxis;
		if(!isAxis){
			System.err.println("You should use HIDButtonInputPart instead for buttons!. AxisType: " + type + " button code: " + code);
			if(code <= 0){
				System.err.println("Code is out of range! code: " + code);
			}
		} else {
			if(code < 0){
				System.err.println("Code is out of range! code: " + code);
			}
		}
	}

	@Override
	protected double calculatePosition() {
		if(isAxis){
			double value = hid.getRawAxis(code) * (inverted ? -1 : 1); // a value from -1 to 1
			return getAxisType().isFull() ? value : (value + 1.0) / 2.0;
		}
		return (hid.getRawButton(code) == !inverted) ? 1 : 0;
	}

	@Override
	public boolean isConnected() {
		if(isAxis){
			if(hid.getAxisCount() >= code){ // axi indexes start at 0
				return false;
			}
		} else {
			if(hid.getButtonCount() > code){ // button indexes start at 1
				return false;
			}
		}
		return HIDUtil.isConnected(hid);
	}

}
