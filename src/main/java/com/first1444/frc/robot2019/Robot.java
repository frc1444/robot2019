/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.												*/
/* Open Source Software - may be modified and shared by FRC teams. The code	 */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.																															 */
/*----------------------------------------------------------------------------*/

package com.first1444.frc.robot2019;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.first1444.frc.input.DualShockRumble;
import com.first1444.frc.input.WPIInputCreator;
import com.first1444.frc.robot2019.actions.OperatorAction;
import com.first1444.frc.robot2019.actions.SwerveCalibrateAction;
import com.first1444.frc.robot2019.actions.SwerveDriveAction;
import com.first1444.frc.robot2019.autonomous.AutonomousChooserState;
import com.first1444.frc.robot2019.autonomous.AutonomousModeCreator;
import com.first1444.frc.robot2019.autonomous.RobotAutonActionCreator;
import com.first1444.frc.robot2019.autonomous.actions.vision.LineUpAction;
import com.first1444.frc.robot2019.event.EventSender;
import com.first1444.frc.robot2019.event.SoundEvents;
import com.first1444.frc.robot2019.event.TCPEventSender;
import com.first1444.frc.robot2019.input.DefaultRobotInput;
import com.first1444.frc.robot2019.input.InputUtil;
import com.first1444.frc.robot2019.input.RobotInput;
import com.first1444.frc.robot2019.sensors.DummyGyro;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.*;
import com.first1444.frc.robot2019.subsystems.implementations.*;
import com.first1444.frc.robot2019.subsystems.swerve.*;
import com.first1444.frc.robot2019.vision.BestVisionPacketSelector;
import com.first1444.frc.robot2019.vision.PacketListener;
import com.first1444.frc.robot2019.vision.VisionSupplier;
import com.first1444.frc.util.DynamicSendableChooser;
import com.first1444.frc.util.OrientationSendable;
import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.reportmap.ReportMap;
import com.first1444.frc.util.reportmap.ShuffleboardReportMap;
import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.frc.util.valuemap.sendable.MutableValueMapSendable;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
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
	private final CargoIntake cargoIntake;
	private final Climber climber;
	private final HatchIntake hatchIntake;
	private final Lift lift;
	private final TaskSystem taskSystem;
	
	private final PacketListener packetListener;
	private final EventSender soundSender;

	/** An {@link Action} that updates certain subsystems only when the robot is enabled*/
	private final ActionMultiplexer enabledSubsystemUpdater;
	/** An {@link Action} that updates certain subsystems all the time. If {@link #enabledSubsystemUpdater} is updated, this is updated after that*/
	private final ActionMultiplexer constantSubsystemUpdater;
	/** The {@link ActionChooser} that handles an action that updates subsystems*/
	private final ActionChooser actionChooser;

	private final Action teleopAction;
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

//		final BNO055 IMU = new BNO055();
//		IMU.SetMode(BNO055.IMUMode.NDOF);
		final Gyro gyro = new DummyGyro(0);
		
		dimensions = Constants.Dimensions.INSTANCE;

		orientationSystem = new OrientationSystem(shuffleboardMap, gyro, robotInput);
		
		OrientationSendable.addOrientation(shuffleboardMap.getUserTab(), this::getOrientation);
		
		autonomousPerspectiveChooser = new DynamicSendableChooser<>();
		autonomousPerspectiveChooser.setDefaultOption("Hatch Cam", dimensions.getHatchManipulatorPerspective());
		autonomousPerspectiveChooser.addOption("Cargo Cam", dimensions.getCargoManipulatorPerspective());
		autonomousPerspectiveChooser.addOption("Driver Station (blind field centric)", Perspective.DRIVER_STATION);
		autonomousPerspectiveChooser.addOption("Jumbotron on Right", Perspective.JUMBOTRON_ON_RIGHT);
		autonomousPerspectiveChooser.addOption("Jumbotron on Left", Perspective.JUMBOTRON_ON_LEFT);
		shuffleboardMap.getUserTab().add("Autonomous Perspective", autonomousPerspectiveChooser).withSize(2, 1);

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
		final ReportMap reportMap = new ShuffleboardReportMap(shuffleboardMap.getDebugTab().getLayout("Report Map", BuiltInLayouts.kList));
		final SwerveSetup swerve = Constants.Swerve2019.INSTANCE;
		final int quadCounts = swerve.getQuadCountsPerRevolution();
		final var drive = new FourWheelSwerveDrive(
				this::getOrientation,
				new ImmutableActionFourSwerveCollection(
						new TalonSwerveModule("front left", swerve.getFLDriveCAN(), swerve.getFLSteerCAN(), quadCounts, drivePid, steerPid,
								swerve.setupFL(createModuleConfig("front left module")), talonDebug),
						
						new TalonSwerveModule("front right", swerve.getFRDriveCAN(), swerve.getFRSteerCAN(), quadCounts, drivePid, steerPid,
								swerve.setupFR(createModuleConfig("front right module")), talonDebug),
						
						new TalonSwerveModule("rear left", swerve.getRLDriveCAN(), swerve.getRLSteerCAN(), quadCounts, drivePid, steerPid,
								swerve.setupRL(createModuleConfig("rear left module")), talonDebug),
						
						new TalonSwerveModule("rear right", swerve.getRRDriveCAN(), swerve.getRRSteerCAN(), quadCounts, drivePid, steerPid,
								swerve.setupRR(createModuleConfig("rear right module")), talonDebug)

						
//						new DummySwerveModule(), new DummySwerveModule(), new DummySwerveModule(), new DummySwerveModule()
				),
				swerve.getWheelBase(), swerve.getTrackWidth()
		);
		final var lift = new DummyLift(reportMap);
		final var cargoIntake = new DummyCargoIntake(reportMap);
//		final var climber = new DummyClimber(reportMap);
		final var climber = new MotorClimber(new TalonSRX(Constants.CLIMB_LIFT_PIVOT_ID), new VictorSPX(Constants.CLIMB_DRIVE_ID));
		final var hatchIntake = new DummyHatchIntake(reportMap);
		final var taskSystem = new DefaultTaskSystem(robotInput);
		this.drive = drive;
		this.cargoIntake = cargoIntake;
		this.climber = climber;
		this.hatchIntake = hatchIntake;
		this.lift = lift;
		this.taskSystem = taskSystem;
		
		packetListener = new PacketListener(5801); // start in robotInit()
		soundSender = new TCPEventSender(5809);
		
		enabledSubsystemUpdater = new Actions.ActionMultiplexerBuilder(
				drive, lift, cargoIntake, climber, hatchIntake
		).clearAllOnEnd(false).canRecycle(true).build();
		
		constantSubsystemUpdater = new Actions.ActionMultiplexerBuilder( // NOTE, as of 2019.2.15 the current version of action-lib doesn't update these in order
				orientationSystem,
				taskSystem,
				new SwerveCalibrateAction(this::getDrive, robotInput),
//				new LEDHandler(this),
				new CameraSystem(shuffleboardMap, this::getTaskSystem)
		).clearAllOnEnd(false).canRecycle(false).build();
		actionChooser = Actions.createActionChooser(WhenDone.CLEAR_ACTIVE);

		swerveDriveAction = new SwerveDriveAction(this::getDrive, this::getOrientation, this::getTaskSystem, robotInput, getVisionSupplier(), getDimensions());
		teleopAction = new Actions.ActionMultiplexerBuilder(
				swerveDriveAction,
				new OperatorAction(this, robotInput)
		).clearAllOnEnd(false).canBeDone(false).canRecycle(true).build();
//		testAction = new TestAction(robotInput);
		autonomousChooserState = new AutonomousChooserState(
				shuffleboardMap,  // this will add stuff to the dashboard
				new AutonomousModeCreator(new RobotAutonActionCreator(this), dimensions),
				robotInput
		);

		System.out.println("Finished constructor");
	}
	
	@Override
	public void close() {
		super.close();
		packetListener.interrupt();
		System.out.println("close() method called! Robot program must be ending!");
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
				teleopAction
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
						teleopAction
				) .immediatelyDoNextWhenDone(true) .canBeDone(false) .canRecycle(false) .build()
		);
		swerveDriveAction.setPerspective(autonomousPerspectiveChooser.getSelected());
		soundSender.sendEvent(SoundEvents.AUTONOMOUS_ENABLE);
		lastMode = RobotMode.AUTO;
	}
	/** Called constantly during autonomous*/
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
	
	public Lift getLift(){ return lift; }
	public CargoIntake getCargoIntake(){ return cargoIntake; }
	public HatchIntake getHatchIntake(){ return hatchIntake; }
	public Climber getClimber(){ return climber; }
	public TaskSystem getTaskSystem(){ return taskSystem; }
}
