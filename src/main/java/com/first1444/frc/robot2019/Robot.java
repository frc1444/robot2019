/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.												*/
/* Open Source Software - may be modified and shared by FRC teams. The code	 */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.																															 */
/*----------------------------------------------------------------------------*/

package com.first1444.frc.robot2019;

import com.first1444.frc.input.WPIInputCreator;
import com.first1444.frc.robot2019.subsystems.LEDHandler;
import com.first1444.frc.robot2019.actions.TeleopAction;
import com.first1444.frc.robot2019.input.DefaultRobotInput;
import com.first1444.frc.robot2019.input.InputUtil;
import com.first1444.frc.robot2019.input.RobotInput;

import com.first1444.frc.robot2019.subsystems.Drive;
import com.first1444.frc.util.PidSendable;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.ValueMapSendable;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import me.retrodaredevil.action.*;
import me.retrodaredevil.controller.ControllerManager;
import me.retrodaredevil.controller.DefaultControllerManager;
import me.retrodaredevil.controller.MutableControlConfig;

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

	private Drive drive;

	/** An {@link Action} that updates certain subsystems only when the robot is enabled*/
	private final ActionMultiplexer enabledSubsystemUpdater;
	/** An {@link Action} that updates certain subsystems all the time*/
	private final ActionMultiplexer constantSubsystemUpdater;
	/** The {@link ActionChooser} that handles an action that updates subsystems*/
	private final ActionChooser actionChooser;

	private final Action teleopAction;

    /** Used to initialize final fields.*/
	public Robot(){
		super(TimedRobot.kDefaultPeriod); // same as default constructor, but we can change it if we want
		robotInput = new DefaultRobotInput(
				InputUtil.createPS4Controller(new WPIInputCreator(new Joystick(0))),
				InputUtil.createJoystick(new WPIInputCreator(new Joystick(1)))
		);
		MutableControlConfig controlConfig = new MutableControlConfig();
		// *edit values of controlConfig if desired*
		controlConfig.switchToSquareInputThreshold = 1.2;
		controllerManager = new DefaultControllerManager(controlConfig);
		controllerManager.addController(robotInput);

		enabledSubsystemUpdater = new Actions.ActionMultiplexerBuilder()
				.clearAllOnEnd(false)
				.canRecycle(true)
				.build();
		constantSubsystemUpdater = new Actions.ActionMultiplexerBuilder()
				.clearAllOnEnd(false)
				.canRecycle(false)
				.build();
		actionChooser = Actions.createActionChooser(WhenDone.CLEAR_ACTIVE);

		teleopAction = new TeleopAction(this, robotInput);
	}

	/** Recommended way to initialize things. Called after constructor is called*/
	@Override
	public void robotInit() {
		ValueMapSendable<PidKey> drivePidSendable = new ValueMapSendable<>(PidKey.class);
		drivePidSendable.getMutableValueMap() // TODO pass this into drive once we set it up
				.setDouble(PidKey.P, 12)
				.setDouble(PidKey.I, .03); // etc
		SmartDashboard.putData(drivePidSendable);

		enabledSubsystemUpdater.add(drive = new Drive());
		constantSubsystemUpdater.add(new LEDHandler(this));
	}

	/** Called when robot is disabled and in between switching between modes such as teleop and autonomous*/
	@Override
	public void disabledInit() {
		actionChooser.setToClearAction();
		if(enabledSubsystemUpdater.isActive()) {
			enabledSubsystemUpdater.end();
		}
	}

	/** Called before every other period method no matter what state the robot is in*/
	@Override
	public void robotPeriodic() {
		controllerManager.update(); // update controllers
        actionChooser.update(); // update Actions that control the subsystems
        if(isEnabled()){
        	enabledSubsystemUpdater.update(); // update subsystems when robot is enabled
		}
        constantSubsystemUpdater.update(); // update subsystems that are always updated

	}

	/** Called when going into teleop mode */
	@Override
	public void teleopInit() {
		actionChooser.setNextAction(teleopAction);
	}

	/** Called first thing when match starts. Autonomous is active for 15 seconds*/
	@Override
	public void autonomousInit() {
		actionChooser.setNextAction(
				new Actions.ActionQueueBuilder(
						Actions.createRunOnce(() -> System.out.println("Autonomous init!")),
						Actions.createRunForever(() -> System.out.println("Autonomous running!"))
				)
						.canRecycle(false)
						.canBeDone(true)
						.build()
		);
	}

	public Drive getDrive(){ return drive; }

}
