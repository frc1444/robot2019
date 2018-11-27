package com.first1444.frc.input;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import me.retrodaredevil.controller.implementations.ControllerPartCreator;
import me.retrodaredevil.controller.input.AxisType;
import me.retrodaredevil.controller.input.DigitalAnalogInputPart;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.controller.input.TwoAxisJoystickPart;
import me.retrodaredevil.controller.output.ControllerRumble;

/**
 * This is used to create different types of {@link InputPart}s and {@link JoystickPart}s using a
 * {@link GenericHID} which is easily instantiated by using a {@link Joystick}
 */
public class WPIInputCreator implements ControllerPartCreator {

	private final GenericHID hid;

	public WPIInputCreator(GenericHID hid){
		this.hid = hid;
	}

	@Override
	public InputPart createDigital(int code) {
        return new HIDButtonInputPart(hid, code + 1);
	}

	@Override
	public JoystickPart createPOV(int povNumber, int xAxis, int yAxis) {
        return createPOV(povNumber);
	}

	@Override
	public JoystickPart createPOV(int povNumber) {
        return new HIDPOVJoystickPart(hid, povNumber);
	}

	@Override
	public JoystickPart createPOV(int xAxis, int yAxis) {
        return createJoystick(xAxis, yAxis);
	}

	@Override
	public JoystickPart createJoystick(int xAxis, int yAxis) {
        return new TwoAxisJoystickPart(
        		new HIDInputPart(AxisType.FULL_ANALOG, hid, xAxis, false, true),
				new HIDInputPart(AxisType.FULL_ANALOG, hid, yAxis, true, true)
		);
	}

	@Override
	public InputPart createFullAnalog(int axisCode) {
		System.err.println("Using createFullAnalog(int) method instead of createFullAnalog(int, boolean)!");
        return createFullAnalog(axisCode, false);
	}

	@Override
	public InputPart createAnalog(int axisCode) {
		System.err.println("Using createAnalog(int) method instead of createAnalog(int, boolean)!");
        return createAnalog(axisCode, false);
	}

	@Override
	public InputPart createFullAnalog(int axisCode, boolean isVertical) {
		return new HIDInputPart(AxisType.FULL_ANALOG, hid, axisCode, isVertical, true);
	}

	@Override
	public InputPart createAnalog(int axisCode, boolean isVertical) {
		return new HIDInputPart(AxisType.ANALOG, hid, axisCode, isVertical, true);
	}

	@Override
	public InputPart createAnalogTrigger(int axisCode) {
		return new HIDInputPart(AxisType.ANALOG, hid, axisCode, false, true);
	}

	@Override
	public InputPart createTrigger(int digitalCode, int analogCode) {
        return new DigitalAnalogInputPart(
        		new HIDButtonInputPart(hid, digitalCode + 1),
				new HIDInputPart(AxisType.ANALOG, hid, analogCode, false, true)
		);
	}

	@Override
	public ControllerRumble createRumble() {
        return new HIDRumble(hid);
	}

	@Override
	public boolean isConnected() {
        return HIDUtil.isConnected(hid);
	}

	@Override
	public String getName() {
        return hid.getName();
	}

	@Override
	public String toString() {
        return String.format("%s{port:%s,axi count:%s,button count:%s,pov count:%s}",
				getClass().getSimpleName(), hid.getPort(), hid.getAxisCount(), hid.getButtonCount(), hid.getPOVCount());
	}
}
