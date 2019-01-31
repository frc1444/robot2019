package com.first1444.frc.robot2019.subsystems;

import com.first1444.frc.robot2019.ShuffleboardMap;
import com.first1444.frc.robot2019.input.RobotInput;
import com.first1444.frc.robot2019.sensors.DefaultOrientation;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.util.DynamicSendableChooser;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.controller.input.InputPart;

public class OrientationSystem extends SimpleAction {
	private final DynamicSendableChooser<Double> startingOrientationChooser;
	private final Gyro gyro;
	private final RobotInput robotInput;
	private final Orientation orientation;
	public OrientationSystem(ShuffleboardMap shuffleboardMap, Gyro gyro, RobotInput robotInput) {
		super(false);
		startingOrientationChooser = new DynamicSendableChooser<>();
		this.gyro = gyro;
		this.robotInput = robotInput;
		orientation = new DefaultOrientation(gyro, this::getStartingOrientation);
		
		startingOrientationChooser.setDefaultOption("forward (90)", 90.0);
		startingOrientationChooser.addOption("right (0)", 0.0);
		startingOrientationChooser.addOption("left (180)", 180.0);
		startingOrientationChooser.addOption("backwards (270)", 270.0);
		
		shuffleboardMap.getUserTab().add("Starting Orientation", startingOrientationChooser);
	}
	public double getStartingOrientation(){
		return startingOrientationChooser.getSelected();
	}
	public void resetGyro(){
		gyro.reset();
	}
	public Orientation getOrientation(){
		return orientation;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		{ // resetting the gyro code
			final InputPart x = robotInput.getResetGyroJoy().getXAxis();
			final InputPart y = robotInput.getResetGyroJoy().getYAxis();
			if (x.isDown() || y.isDown()){
				gyro.reset();
				final double angle = robotInput.getResetGyroJoy().getAngle();
				startingOrientationChooser.addOption("Custom", angle);
				startingOrientationChooser.setSelectedKey("Custom");
			}
		}
	}
}
