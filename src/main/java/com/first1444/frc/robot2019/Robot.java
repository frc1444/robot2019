/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.												*/
/* Open Source Software - may be modified and shared by FRC teams. The code	 */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.																															 */
/*----------------------------------------------------------------------------*/

package com.first1444.frc.robot2019;

import com.first1444.frc.input.DualShockRumble;
import com.first1444.frc.input.WPIInputCreator;
import com.first1444.frc.input.sendable.ControllerPartSendable;
import com.first1444.frc.input.sendable.InputPartSendable;
import com.first1444.frc.input.sendable.JoystickPartSendable;
import com.first1444.frc.robot2019.actions.SwerveCalibrateAction;
import com.first1444.frc.robot2019.actions.SwerveDriveAction;
import com.first1444.frc.robot2019.autonomous.AutonomousChooserState;
import com.first1444.frc.robot2019.autonomous.AutonomousModeCreator;
import com.first1444.frc.robot2019.autonomous.RobotAutonActionCreator;
import com.first1444.frc.robot2019.autonomous.actions.LineUpAction;
import com.first1444.frc.robot2019.event.EventSender;
import com.first1444.frc.robot2019.event.SoundEvents;
import com.first1444.frc.robot2019.event.TCPEventSender;
import com.first1444.frc.robot2019.input.DefaultRobotInput;
import com.first1444.frc.robot2019.input.InputUtil;
import com.first1444.frc.robot2019.input.RobotInput;
import com.first1444.frc.robot2019.sensors.BNO055;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.*;
import com.first1444.frc.robot2019.subsystems.swerve.*;
import com.first1444.frc.robot2019.vision.BestVisionPacketSelector;
import com.first1444.frc.robot2019.vision.PacketListener;
import com.first1444.frc.robot2019.vision.VisionSupplier;
import com.first1444.frc.util.DynamicSendableChooser;
import com.first1444.frc.util.OrientationSendable;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.frc.util.valuemap.sendable.MutableValueMapSendable;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import me.retrodaredevil.action.*;
import me.retrodaredevil.controller.ControllerManager;
import me.retrodaredevil.controller.DefaultControllerManager;
import me.retrodaredevil.controller.MutableControlConfig;
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
	private final OrientationSystem orientationSystem;
	private final RobotDimensions dimensions;

	private final ControllerManager controllerManager;
	private final RobotInput robotInput;

	private final DynamicSendableChooser<Perspective> autonomousPerspectiveChooser;
	private final SwerveDrive drive;
	
	private final PacketListener packetListener;
	private final EventSender soundSender;

	/** An {@link Action} that updates certain subsystems only when the robot is enabled*/
	private final ActionMultiplexer enabledSubsystemUpdater;
	/** An {@link Action} that updates certain subsystems all the time*/
	private final ActionMultiplexer constantSubsystemUpdater;
	/** The {@link ActionChooser} that handles an action that updates subsystems*/
	private final ActionChooser actionChooser;

	private final SwerveDriveAction swerveDriveAction;
//	private final Action testAction;
	private final AutonomousChooserState autonomousChooserState;
	
	private enum RobotMode {TELEOP, TEST, AUTO, DISABLED}
	private RobotMode lastMode = RobotMode.DISABLED;
	
	
	// region Initialize
	/** Used to initialize final fields.*/
	public Robot(){
		super(.055);
		shuffleboardMap = new DefaultShuffleboardMap();
		final ControllerRumble rumble = new DualShockRumble(new XboxController(5));
		robotInput = new DefaultRobotInput(
				InputUtil.createPS4Controller(new WPIInputCreator(new Joystick(0))),
				InputUtil.createJoystick(new WPIInputCreator(new Joystick(1))),
				InputUtil.createAttackJoystick(new WPIInputCreator(new Joystick(2))),
				rumble
		);
		MutableControlConfig controlConfig = new MutableControlConfig();
		// *edit values of controlConfig if desired*
		controlConfig.switchToSquareInputThreshold = 1.2;
		controlConfig.fullAnalogDeadzone = .075;
		controlConfig.analogDeadzone = .02;
		controlConfig.cacheAngleAndMagnitudeInUpdate = false;
		controllerManager = new DefaultControllerManager(controlConfig);
		controllerManager.addController(robotInput);
		controllerManager.update(); // update this so when calling get methods don't throw exceptions

		BNO055 IMU = new BNO055();
		IMU.SetMode(BNO055.IMUMode.NDOF);
		
		dimensions = Constants.Dimensions.INSTANCE;

		orientationSystem = new OrientationSystem(shuffleboardMap, IMU, robotInput);
		
		OrientationSendable.addOrientation(shuffleboardMap.getUserTab(), this::getOrientation);
		
		autonomousPerspectiveChooser = new DynamicSendableChooser<>();
		autonomousPerspectiveChooser.setDefaultOption("Hatch Cam", dimensions.getHatchManipulatorPerspective());
		autonomousPerspectiveChooser.addOption("Cargo Cam", dimensions.getCargoManipulatorPerspective());
		autonomousPerspectiveChooser.addOption("Driver Station (blind field centric)", Perspective.DRIVER_STATION);
		autonomousPerspectiveChooser.addOption("Jumbotron on Right", Perspective.JUMBOTRON_ON_RIGHT);
		autonomousPerspectiveChooser.addOption("Jumbotron on Left", Perspective.JUMBOTRON_ON_LEFT);
		shuffleboardMap.getUserTab().add("Autonomous Perspective", autonomousPerspectiveChooser);

		final MutableValueMapSendable<PidKey> drivePidSendable = new MutableValueMapSendable<>(PidKey.class);
		final MutableValueMapSendable<PidKey> steerPidSendable = new MutableValueMapSendable<>(PidKey.class);
		if(Constants.DEBUG) {
			shuffleboardMap.getDevTab().add("Drive PID", drivePidSendable);
			shuffleboardMap.getDevTab().add("Steer PID", steerPidSendable);
		}
		final MutableValueMap<PidKey> drivePid = drivePidSendable.getMutableValueMap();
		final MutableValueMap<PidKey> steerPid = steerPidSendable.getMutableValueMap();
		drivePid
				.setDouble(PidKey.P, 1.5)
				.setDouble(PidKey.F, 1.0)
				.setDouble(PidKey.CLOSED_RAMP_RATE, .25); // etc
		steerPid
				.setDouble(PidKey.P, 12)
				.setDouble(PidKey.I, .03);

		final ShuffleboardTab talonDebug = shuffleboardMap.getDebugTab();
		final FourWheelSwerveDrive drive = new FourWheelSwerveDrive(
				this::getOrientation,
				new ImmutableActionFourSwerveCollection(
						new TalonSwerveModule("front left", Constants.FL_DRIVE, Constants.FL_STEER, drivePid, steerPid,
								createModuleConfig("front left module")
										.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 147)
										.setDouble(ModuleConfig.MAX_ENCODER_VALUE, 899)
										.setDouble(ModuleConfig.MIN_ENCODER_VALUE, 10), talonDebug),
						
						new TalonSwerveModule("front right", Constants.FR_DRIVE, Constants.FR_STEER, drivePid, steerPid,
								createModuleConfig("front right module")
										.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 705)
										.setDouble(ModuleConfig.MAX_ENCODER_VALUE, 891)
										.setDouble(ModuleConfig.MIN_ENCODER_VALUE, 12), talonDebug),
						
						new TalonSwerveModule("rear left", Constants.RL_DRIVE, Constants.RL_STEER, drivePid, steerPid,
								createModuleConfig("rear left module")
										.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 775)
										.setDouble(ModuleConfig.MAX_ENCODER_VALUE, 872)
										.setDouble(ModuleConfig.MIN_ENCODER_VALUE, 13), talonDebug),
						
						new TalonSwerveModule("rear right", Constants.RR_DRIVE, Constants.RR_STEER, drivePid, steerPid,
								createModuleConfig("rear right module")
										.setDouble(ModuleConfig.ABS_ENCODER_OFFSET, 604)
										.setDouble(ModuleConfig.MAX_ENCODER_VALUE, 895)
										.setDouble(ModuleConfig.MIN_ENCODER_VALUE, 9), talonDebug)

						
//						new DummySwerveModule(), new DummySwerveModule(), new DummySwerveModule(), new DummySwerveModule()
				),
				27.375, 22.25
		);
		this.drive = drive;
		
		this.packetListener = new PacketListener(5801); // start in robotInit()
		this.soundSender = new TCPEventSender(5809);
		
		enabledSubsystemUpdater = new Actions.ActionMultiplexerBuilder(
				drive
		).clearAllOnEnd(false).canRecycle(true).build();
		
		constantSubsystemUpdater = new Actions.ActionMultiplexerBuilder(
				orientationSystem,
				new SwerveCalibrateAction(this::getDrive, robotInput),
				new LEDHandler(this)
		).clearAllOnEnd(false).canRecycle(false).build();
		actionChooser = Actions.createActionChooser(WhenDone.CLEAR_ACTIVE);

		swerveDriveAction = new SwerveDriveAction(this::getDrive, this::getOrientation, robotInput, getVisionSupplier(), getDimensions());
//		testAction = new TestAction(robotInput);
		autonomousChooserState = new AutonomousChooserState(
				shuffleboardMap,  // this will add stuff to the dashboard
				new AutonomousModeCreator(new RobotAutonActionCreator(this), dimensions),
				robotInput
		);

		if(Constants.DEBUG) {
			final ShuffleboardTab inputTab = shuffleboardMap.getDebugTab();
			inputTab.add("Movement Joy", new JoystickPartSendable(robotInput::getMovementJoy));
			inputTab.add("Movement Speed", new InputPartSendable(robotInput::getMovementSpeed));
			inputTab.add("Driver Rumble", new ControllerPartSendable(robotInput::getDriverRumble));
			
			inputTab.add("Cargo Intake", new InputPartSendable(robotInput::getCargoIntakeSpeed));
			inputTab.add("Lift Speed", new InputPartSendable(robotInput::getLiftManualSpeed));
			inputTab.add("Hatch Pivot Speed", new InputPartSendable(robotInput::getHatchManualPivotSpeed));
		}
		System.out.println("Finished constructor");
	}
	private MutableValueMap<ModuleConfig> createModuleConfig(String name){
		final MutableValueMapSendable<ModuleConfig> config = new MutableValueMapSendable<>(ModuleConfig.class);
		if(Constants.DEBUG) {
			shuffleboardMap.getDevTab().add(name, config);
		}
		return config.getMutableValueMap();
	}

	/** Just a second way to initialize things*/
	@Override
	public void robotInit() {
		try {
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
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
		
	}
	
	/** Called when robot is disabled and in between switching between modes such as teleop and autonomous*/
	@Override
	public void disabledInit() {
		actionChooser.setToClearAction();
		if(enabledSubsystemUpdater.isActive()) {
			enabledSubsystemUpdater.end();
		}
		if(lastMode == RobotMode.TELEOP){
			soundSender.sendEvent(SoundEvents.MATCH_END);
		} else {
			soundSender.sendEvent(SoundEvents.DISABLE);
		}
		lastMode = RobotMode.DISABLED;
	}
	@Override public void disabledPeriodic() { }

	/** Called when going into teleop mode */
	@Override
	public void teleopInit() {
		actionChooser.setNextAction(new Actions.ActionMultiplexerBuilder(
				swerveDriveAction
		).canRecycle(false).canBeDone(true).build());
		swerveDriveAction.setPerspective(Perspective.DRIVER_STATION);
		soundSender.sendEvent(SoundEvents.TELEOP_ENABLE);
		lastMode = RobotMode.TELEOP;
	}
	@Override public void teleopPeriodic() { }
	
	/** Called first thing when match starts. Autonomous is active for 15 seconds*/
	@Override
	public void autonomousInit() {
		orientationSystem.resetGyro();
		
		actionChooser.setNextAction(
				new Actions.ActionQueueBuilder(
						autonomousChooserState.createAutonomousAction(orientationSystem.getStartingOrientation()),
						swerveDriveAction
				) .immediatelyDoNextWhenDone(true) .canBeDone(false) .canRecycle(false) .build()
		);
		swerveDriveAction.setPerspective(autonomousPerspectiveChooser.getSelected());
		soundSender.sendEvent(SoundEvents.AUTONOMOUS_ENABLE);
		lastMode = RobotMode.AUTO;
	}
	/** Called constantly during autonomous*/
	@Override
	public void autonomousPeriodic() {
		if(!swerveDriveAction.isActive()){
			if(robotInput.getAutonomousCancelButton().isDown()){
				actionChooser.setNextAction(swerveDriveAction);
				System.out.println("Letting teleop take over now");
			}
		}
	}
	
	@Override
	public void testInit() {
//		actionChooser.setNextAction(testAction);
		actionChooser.setNextAction(new Actions.ActionQueueBuilder(
//				new TurnToOrientation(-90, this::getDrive, this::getOrientation),
//				new GoStraight(10, .2, 0, 1, 90.0, this::getDrive, this::getOrientation),
				Actions.createLinkedActionRunner(
						new LineUpAction(
								packetListener, dimensions.getHatchCameraID(), Perspective.ROBOT_FORWARD_CAM,
								new BestVisionPacketSelector(), this::getDrive, this::getOrientation,
								Actions.createRunOnce(() -> System.out.println("Failed!")), Actions.createRunOnce(() -> System.out.println("Success!")),
								getSoundSender()),
						WhenDone.CLEAR_ACTIVE_AND_BE_DONE, false
				),
				Actions.createRunOnce(() -> robotInput.getDriverRumble().rumbleTime(500, .2))
		).canRecycle(false).canBeDone(true).immediatelyDoNextWhenDone(true).build());
		lastMode = RobotMode.TEST;
	}
	@Override
	public void testPeriodic() { }
	// endregion
	
	public SwerveDrive getDrive(){ return drive; }
	public Orientation getOrientation(){
		return orientationSystem.getOrientation();
	}
	
	public VisionSupplier getVisionSupplier() {
		return packetListener;
	}
	
	public RobotDimensions getDimensions() {
		return dimensions;
	}
	
	public EventSender getSoundSender(){
		return soundSender;
	}
	
	public Lift getLift(){
		throw new UnsupportedOperationException();
	}
	public CargoIntake getCargoIntake(){
		throw new UnsupportedOperationException();
	}
	public HatchIntake getHatchIntake(){
		throw new UnsupportedOperationException();
	}
	public Climber getClimber(){
		throw new UnsupportedOperationException();
	}
}
