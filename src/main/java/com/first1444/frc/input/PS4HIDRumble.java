package com.first1444.frc.input;

import edu.wpi.first.wpilibj.GenericHID;

public class PS4HIDRumble extends HIDRumble {
	private static final double HEAVY_START = .5;
	private static final double LIGHT_END = .6;
	public PS4HIDRumble(GenericHID hid) {
		super(hid);
	}
	
	@Override
	public void rumbleForever(double intensity) {
		super.rumbleForever(getHeavy(intensity), getLight(intensity));
	}
	@Override
	public void rumbleTime(long millis, double intensity) {
		super.rumbleTime(millis, getHeavy(intensity), getLight(intensity));
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
	public void rumbleTimeout(long millisTimeout, double intensity) {
		rumbleTime(millisTimeout, intensity);
	}
	
	@Override
	public void rumbleTimeout(long millisTimeout, double leftIntensity, double rightIntensity) {
		rumbleTime(millisTimeout, leftIntensity, rightIntensity);
	}
	
	@Override
	public boolean isLeftAndRightSupported() {
		return false;
	}
}
