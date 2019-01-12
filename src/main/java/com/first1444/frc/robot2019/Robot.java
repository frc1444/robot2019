/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.												*/
/* Open Source Software - may be modified and shared by FRC teams. The code	 */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.																															 */
/*----------------------------------------------------------------------------*/

package com.first1444.frc.robot2019;

import com.first1444.frc.input.WPIInputCreator;
import com.first1444.frc.robot2019.actions.TeleopAction;
import com.first1444.frc.robot2019.input.DefaultRobotInput;
import com.first1444.frc.robot2019.input.InputUtil;
import com.first1444.frc.robot2019.input.RobotInput;
import com.first1444.frc.robot2019.sensors.DefaultOrientation;
import com.first1444.frc.robot2019.sensors.DummyGyro;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.LEDHandler;
import com.first1444.frc.robot2019.subsystems.swerve.*;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.ValueMapSendable;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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

	private final ShuffleboardMap shuffleboardMap;
	private final Gyro gyro;

	private final ControllerManager controllerManager;
	private final RobotInput robotInput;

	private final Orientation orientation;
	private final SwerveDrive drive;

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
		shuffleboardMap = new DefaultShuffleboardMap();
		robotInput = new DefaultRobotInput(
				InputUtil.createPS4Controller(new WPIInputCreator(new Joystick(0))),
				InputUtil.createJoystick(new WPIInputCreator(new Joystick(1)))
		);
		MutableControlConfig controlConfig = new MutableControlConfig();
		// *edit values of controlConfig if desired*
		controlConfig.switchToSquareInputThreshold = 1.2;
		controllerManager = new DefaultControllerManager(controlConfig);
		controllerManager.addController(robotInput);

		gyro = new DummyGyro(0);

		final SendableChooser<Double> startingOrientation = new SendableChooser<>();
		startingOrientation.setName("Starting Orientation");
		startingOrientation.setDefaultOption("forward (90)", 90.0);
		startingOrientation.addOption("right (0)", 0.0);
		startingOrientation.addOption("left (180)", 180.0);
		startingOrientation.addOption("backwards (270)", 270.0);
		getShuffleboardMap().getUserTab().add(startingOrientation);
		orientation = new DefaultOrientation(gyro, startingOrientation::getSelected);

		ValueMapSendable<PidKey> drivePidSendable = new ValueMapSendable<>(PidKey.class);
		drivePidSendable.getMutableValueMap() // TODO pass this into drive once we set it up
				.setDouble(PidKey.P, 12)
				.setDouble(PidKey.I, .03); // etc

		ValueMapSendable<PidKey> steerPidSendable = new ValueMapSendable<>(PidKey.class);
		steerPidSendable.getMutableValueMap()
				.setDouble(PidKey.P, 12);
		getShuffleboardMap().getUserTab().add(drivePidSendable);
		FourWheelSwerveDrive drive = new FourWheelSwerveDrive(
				this::getOrientation,
				new ImmutableActionFourSwerveCollection(
						new TalonSwerveModule("front left", 1, 5, drivePidSendable.getMutableValueMap(), steerPidSendable.getMutableValueMap()),
						new TalonSwerveModule("front right", 2, 6, drivePidSendable.getMutableValueMap(), steerPidSendable.getMutableValueMap()),
						new TalonSwerveModule("rear left", 3, 7, drivePidSendable.getMutableValueMap(), steerPidSendable.getMutableValueMap()),
						new TalonSwerveModule("rear right", 4, 8, drivePidSendable.getMutableValueMap(), steerPidSendable.getMutableValueMap())
				),
				20, 20
		);
		this.drive = drive;
		enabledSubsystemUpdater = new Actions.ActionMultiplexerBuilder(
				drive
		)
				.clearAllOnEnd(false)
				.canRecycle(true)
				.build();
		constantSubsystemUpdater = new Actions.ActionMultiplexerBuilder(
				new LEDHandler(this)
		)
				.clearAllOnEnd(false)
				.canRecycle(false)
				.build();
		actionChooser = Actions.createActionChooser(WhenDone.CLEAR_ACTIVE);

		teleopAction = new TeleopAction(this, robotInput);

	}

	/** Just a second way to initialize things*/
	@Override
	public void robotInit() {
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
		gyro.reset();
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

	public SwerveDrive getDrive(){ return drive; }
	public Orientation getOrientation(){
		if(orientation == null){
			throw new IllegalStateException("orientation is null! This should not happen!");
		}
		return orientation;
	}
	public ShuffleboardMap getShuffleboardMap(){
		return shuffleboardMap;
	}

}
