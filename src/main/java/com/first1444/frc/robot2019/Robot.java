/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.												*/
/* Open Source Software - may be modified and shared by FRC teams. The code	 */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.																															 */
/*----------------------------------------------------------------------------*/

package com.first1444.frc.robot2019;

import com.first1444.frc.input.WPIInputCreator;
import com.first1444.frc.robot2019.input.DefaultRobotInput;
import com.first1444.frc.robot2019.input.RobotInput;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import me.retrodaredevil.controller.ControllerManager;
import me.retrodaredevil.controller.DefaultControllerManager;
import me.retrodaredevil.controller.MutableControlConfig;
import me.retrodaredevil.controller.implementations.BaseExtremeFlightJoystickControllerInput;
import me.retrodaredevil.controller.implementations.BaseStandardControllerInput;
import me.retrodaredevil.controller.implementations.ControllerPartCreator;
import me.retrodaredevil.controller.implementations.StandardControllerInputCreator;
import me.retrodaredevil.controller.implementations.mappings.DefaultExtremeFlightJoystickInputCreator;
import me.retrodaredevil.controller.implementations.mappings.PS4StandardControllerInputCreator;
import me.retrodaredevil.controller.options.OptionValues;
import me.retrodaredevil.controller.types.ExtremeFlightJoystickControllerInput;
import me.retrodaredevil.controller.types.StandardControllerInput;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

	private final ControllerManager controllerManager;
	private final RobotInput robotInput;

    /** Used to initialize final fields.*/
	public Robot(){
		super(TimedRobot.kDefaultPeriod); // same as default constructor, but we can change it if we want
		robotInput = new DefaultRobotInput(
				createPS4Controller(new WPIInputCreator(new Joystick(0))),
				createJoystick(new WPIInputCreator(new Joystick(1)))
		);
		MutableControlConfig controlConfig = new MutableControlConfig();
		// *edit values of controlConfig if desired*
		controlConfig.switchToSquareInputThreshold = 1.2;
		controllerManager = new DefaultControllerManager(controlConfig);
		controllerManager.addController(robotInput);
	}
	private static StandardControllerInput createPS4Controller(ControllerPartCreator controller){
		StandardControllerInputCreator ps4Mappings = new PS4StandardControllerInputCreator();
		Boolean physicalLocationSwapped = ps4Mappings.getPhysicalLocationsSwapped();
		Boolean buttonNamesSwapped = ps4Mappings.getButtonNamesSwapped();
		return new BaseStandardControllerInput(
				ps4Mappings,
				controller,
				OptionValues.createImmutableBooleanOptionValue(physicalLocationSwapped == null ? false : physicalLocationSwapped),
				OptionValues.createImmutableBooleanOptionValue(buttonNamesSwapped == null ? false : buttonNamesSwapped)
		);
	}
	private static ExtremeFlightJoystickControllerInput createJoystick(ControllerPartCreator controller){
		return new BaseExtremeFlightJoystickControllerInput(
				new DefaultExtremeFlightJoystickInputCreator(),
				controller
		);
	}

	/** Recommended way to initialize things. Called after constructor is called*/
	@Override
	public void robotInit() {

	}

	/** Called before every other period method no matter what state the robot is in*/
	@Override
	public void robotPeriodic() {
		controllerManager.update();

	}

	/** Called first thing when match starts. Autonomous is active for 15 seconds*/
	@Override
	public void autonomousInit() {
	}

	/** Called when going into teleop mode */
	@Override
	public void teleopInit() {
	}

	/** Called when robot is disabled and in between autonomous and teleop*/
	@Override
	public void disabledInit() {
	}
}
