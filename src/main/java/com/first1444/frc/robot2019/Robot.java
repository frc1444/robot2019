/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.												*/
/* Open Source Software - may be modified and shared by FRC teams. The code	 */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.																															 */
/*----------------------------------------------------------------------------*/

package com.first1444.frc.robot2019;

import com.first1444.frc.input.PS4HIDRumble;
import com.first1444.frc.input.WPIInputCreator;
import com.first1444.frc.input.sendable.ControllerPartSendable;
import com.first1444.frc.input.sendable.InputPartSendable;
import com.first1444.frc.input.sendable.JoystickPartSendable;
import com.first1444.frc.robot2019.actions.TeleopAction;
import com.first1444.frc.robot2019.actions.TestAction;
import com.first1444.frc.robot2019.autonomous.AutonomousChooserState;
import com.first1444.frc.robot2019.autonomous.AutonomousModeCreator;
import com.first1444.frc.robot2019.autonomous.RobotAutonActionCreator;
import com.first1444.frc.robot2019.input.DefaultRobotInput;
import com.first1444.frc.robot2019.input.InputUtil;
import com.first1444.frc.robot2019.input.RobotInput;
import com.first1444.frc.robot2019.sensors.DefaultOrientation;
import com.first1444.frc.robot2019.sensors.DummyGyro;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.LEDHandler;
import com.first1444.frc.robot2019.subsystems.swerve.DummySwerveModule;
import com.first1444.frc.robot2019.subsystems.swerve.FourWheelSwerveDrive;
import com.first1444.frc.robot2019.subsystems.swerve.ImmutableActionFourSwerveCollection;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.robot2019.vision.PacketListener;
import com.first1444.frc.util.DynamicSendableChooser;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.frc.util.valuemap.sendable.ValueMapLayout;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import me.retrodaredevil.action.*;
import me.retrodaredevil.controller.ControllerManager;
import me.retrodaredevil.controller.DefaultControllerManager;
import me.retrodaredevil.controller.MutableControlConfig;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.output.ControllerRumble;

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
	private final RobotDimensions dimensions;

	private final ControllerManager controllerManager;
	private final RobotInput robotInput;

	private final DynamicSendableChooser<Double> startingOrientationChooser;
	private final Orientation orientation;
	private final SwerveDrive drive;
	
	private final PacketListener packetListener;

	/** An {@link Action} that updates certain subsystems only when the robot is enabled*/
	private final ActionMultiplexer enabledSubsystemUpdater;
	/** An {@link Action} that updates certain subsystems all the time*/
	private final ActionMultiplexer constantSubsystemUpdater;
	/** The {@link ActionChooser} that handles an action that updates subsystems*/
	private final ActionChooser actionChooser;

	private final Action teleopAction;
	private final Action testAction;
	private final AutonomousChooserState autonomousChooserState;
	
	private UsbCamera camera = null;


	// region Initialize
	/** Used to initialize final fields.*/
	public Robot(){
		super(.02); // same as default constructor, but we can change it if we want
		shuffleboardMap = new DefaultShuffleboardMap();
		final ControllerRumble rumble = new PS4HIDRumble(new XboxController(2));
		robotInput = new DefaultRobotInput(
				InputUtil.createPS4Controller(new WPIInputCreator(new Joystick(0))),
				InputUtil.createJoystick(new WPIInputCreator(new Joystick(1))),
				rumble
		);
		MutableControlConfig controlConfig = new MutableControlConfig();
		// *edit values of controlConfig if desired*
		controlConfig.switchToSquareInputThreshold = 1.2;
		controlConfig.fullAnalogDeadzone = .075;
		controllerManager = new DefaultControllerManager(controlConfig);
		controllerManager.addController(robotInput);

		gyro = new DummyGyro(0);
		dimensions = Constants.Dimensions.INSTANCE;

		startingOrientationChooser = new DynamicSendableChooser<>();
		startingOrientationChooser.setName("Starting Orientation");
		startingOrientationChooser.setDefaultOption("forward (90)", 90.0);
		startingOrientationChooser.addOption("right (0)", 0.0);
		startingOrientationChooser.addOption("left (180)", 180.0);
		startingOrientationChooser.addOption("backwards (270)", 270.0);
		shuffleboardMap.getUserTab().add(startingOrientationChooser)
				.withSize(2, 1);
		orientation = new DefaultOrientation(gyro, startingOrientationChooser::getSelected);

		
		final MutableValueMap<PidKey> drivePid = new ValueMapLayout<>(PidKey.class, "Drive PID", shuffleboardMap.getDevTab()).getMutableValueMap();
		final MutableValueMap<PidKey> steerPid = new ValueMapLayout<>(PidKey.class, "Steer PID", shuffleboardMap.getDevTab()).getMutableValueMap();
		drivePid
				.setDouble(PidKey.P, 1.5)
				.setDouble(PidKey.F, 1.0)
				.setDouble(PidKey.CLOSED_RAMP_RATE, .25); // etc
		steerPid
				.setDouble(PidKey.P, 12)
				.setDouble(PidKey.I, .03);

		final FourWheelSwerveDrive drive = new FourWheelSwerveDrive(
				this::getOrientation,
				new ImmutableActionFourSwerveCollection(
//						new TalonSwerveModule("front left", 1, 5, drivePid, steerPid),
//						new TalonSwerveModule("front right", 2, 6, drivePid, steerPid),
//						new TalonSwerveModule("rear left", 3, 7, drivePid, steerPid),
//						new TalonSwerveModule("rear right", 4, 8, drivePid, steerPid)
						new DummySwerveModule(), new DummySwerveModule(), new DummySwerveModule(), new DummySwerveModule()
				),
				20, 20
		);
		this.drive = drive;
		
		this.packetListener = new PacketListener(); // start in robotInit()
		
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
		testAction = new TestAction(robotInput);
		autonomousChooserState = new AutonomousChooserState(
				shuffleboardMap,  // this will add stuff to the dashboard
				new AutonomousModeCreator(new RobotAutonActionCreator(this), dimensions)
		);

		controllerManager.update(); // update this so when calling get methods don't throw exceptions
		final ShuffleboardTab inputTab = shuffleboardMap.getDebugTab();
		inputTab.add("Movement Joy", new JoystickPartSendable(robotInput::getMovementJoy)).withSize(2, 2);
		inputTab.add("Movement Speed", new InputPartSendable(robotInput::getMovementSpeed));
		inputTab.add("Driver Rumble", new ControllerPartSendable(robotInput::getDriverRumble));


		System.out.println("Finished constructor");
	}

	/** Just a second way to initialize things*/
	@Override
	public void robotInit() {
		try {
			camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setVideoMode(VideoMode.PixelFormat.kMJPEG, 320, 240, 9);
			
			shuffleboardMap.getUserTab().add(camera);
		} catch(Exception e){
			e.printStackTrace();
			System.out.println("Couldn't initialize the camera!");
		}
		packetListener.start();

		System.out.println("Finished robot init!");
	}
	// endregion
	
	// region Overridden Methods
	
	/** Called before every other period method no matter what state the robot is in*/
	@Override
	public void robotPeriodic() {
		controllerManager.update(); // update controllers
		actionChooser.update(); // update Actions that control the subsystems
		if(isEnabled()){
			enabledSubsystemUpdater.update(); // update subsystems when robot is enabled
		}
		constantSubsystemUpdater.update(); // update subsystems that are always updated
		
		{
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
	
	/** Called when robot is disabled and in between switching between modes such as teleop and autonomous*/
	@Override
	public void disabledInit() {
		actionChooser.setToClearAction();
		if(enabledSubsystemUpdater.isActive()) {
			enabledSubsystemUpdater.end();
		}
	}
	@Override public void disabledPeriodic() { }

	/** Called when going into teleop mode */
	@Override
	public void teleopInit() {
		actionChooser.setNextAction(teleopAction);
	}
	@Override public void teleopPeriodic() { }
	
	/** Called first thing when match starts. Autonomous is active for 15 seconds*/
	@Override
	public void autonomousInit() {
		gyro.reset();
		actionChooser.setNextAction(
				new Actions.ActionQueueBuilder(
						autonomousChooserState.createAutonomousAction(startingOrientationChooser.getSelected()),
						teleopAction
				)
						.build()
		);
//		actionChooser.setNextAction(Actions.createRunForever(() -> {})); // for testing
	}
	@Override
	public void autonomousPeriodic() {
		if(!teleopAction.isActive()){
			if(robotInput.getAutonomousCancelButton().isDown()){
				actionChooser.setNextAction(teleopAction);
				System.out.println("Letting teleop take over now");
			}
		}
	}
	
	@Override
	public void testInit() {
		actionChooser.setNextAction(testAction);
	}
	@Override
	public void testPeriodic() { }
	// endregion
	
	public SwerveDrive getDrive(){ return drive; }
	public Orientation getOrientation(){
		return orientation;
	}
}
