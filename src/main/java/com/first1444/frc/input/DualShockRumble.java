package com.first1444.frc.input;

import edu.wpi.first.wpilibj.GenericHID;
import me.retrodaredevil.controller.SimpleControllerPart;
import me.retrodaredevil.controller.output.ControllerRumble;

/**
 * A {@link ControllerRumble} where it supports ligth and heavy rumble. This assumes that the left rumble motor is
 * heavy and the right rumble motor is light
 */
public class DualShockRumble extends SimpleControllerPart implements ControllerRumble {
	private static final double HEAVY_START = .5;
	private static final double LIGHT_END = .6;
	
	private ControllerRumble rumble;
	
	public DualShockRumble(GenericHID hid) {
		this(new HIDRumble(hid));
	}
	private DualShockRumble(ControllerRumble rumble){
		this.rumble = rumble;
		addChildren(false, false, rumble);
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		rumble.update(config);
	}
	
	@Override
	public void rumbleForever(double intensity) {
		rumble.rumbleForever(getHeavy(intensity), getLight(intensity));
	}
	@Override
	public void rumbleTime(long millis, double intensity) {
		rumble.rumbleTime(millis, getHeavy(intensity), getLight(intensity));
	}
	@Override
	public void rumbleTimeout(long millisTimeout, double intensity) {
		rumble.rumbleTime(millisTimeout, getHeavy(intensity), getLight(intensity));
	}
	private double getHeavy(double intensity){
		if(intensity > HEAVY_START){
			return (intensity - HEAVY_START) / (1 - HEAVY_START);
		}
		return 0;
	}
	private double getLight(double intensity){
		if(intensity > LIGHT_END){
			return 1;
		}
		return intensity / LIGHT_END;
	}
	
	@Override
	public void rumbleForever(double leftIntensity, double rightIntensity) {
		rumbleForever((leftIntensity + rightIntensity) / 2.0);
	}
	
	@Override
	public void rumbleTime(long millis, double leftIntensity, double rightIntensity) {
		rumbleTime(millis, (leftIntensity + rightIntensity) / 2.0);
	}
	
	@Override
	public void rumbleTimeout(long millisTimeout, double leftIntensity, double rightIntensity) {
		rumbleTimeout(millisTimeout, (leftIntensity + rightIntensity) / 2.0);
	}
	
	@Override
	public boolean isLeftAndRightSupported() {
		return false;
	}
	@Override
	public boolean isAnalogRumbleSupported() {
		return rumble.isAnalogRumbleSupported();
	}
	@Override
	public boolean isAnalogRumbleNativelySupported() {
		return rumble.isAnalogRumbleNativelySupported();
	}
	
	@Override
	public boolean isConnected() {
		return rumble.isConnected();
	}
}
