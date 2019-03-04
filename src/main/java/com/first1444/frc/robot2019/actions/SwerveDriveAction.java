package com.first1444.frc.robot2019.actions;

import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.RobotDimensions;
import com.first1444.frc.robot2019.autonomous.actions.vision.LineUpCreator;
import com.first1444.frc.robot2019.input.RobotInput;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.TaskSystem;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.robot2019.vision.BestVisionPacketSelector;
import com.first1444.frc.robot2019.vision.VisionSupplier;
import com.first1444.frc.util.MathUtil;
import me.retrodaredevil.action.*;
import me.retrodaredevil.controller.input.InputPart;
import me.retrodaredevil.controller.input.JoystickPart;
import me.retrodaredevil.controller.output.ControllerRumble;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * This swerve controls for teleop and should be ended when teleop is over. This can be recycled
 */
public class SwerveDriveAction extends SimpleAction {
	private final Supplier<SwerveDrive> driveSupplier;
	private final Supplier<Orientation> orientationSupplier;
	private final Supplier<TaskSystem> taskSystemSupplier;
	private final RobotInput input;
	private final ActionChooser actionChooser;
	private final VisionSupplier visionSupplier;
	private final RobotDimensions dimensions;
	private Perspective perspective = Perspective.DRIVER_STATION;
	
	public SwerveDriveAction(Supplier<SwerveDrive> driveSupplier, Supplier<Orientation> orientationSupplier, Supplier<TaskSystem> taskSystemSupplier, RobotInput input, VisionSupplier visionSupplier, RobotDimensions dimensions) {
		super(true);
		this.driveSupplier = Objects.requireNonNull(driveSupplier);
		this.orientationSupplier = Objects.requireNonNull(orientationSupplier);
		this.taskSystemSupplier = Objects.requireNonNull(taskSystemSupplier);
		this.input = Objects.requireNonNull(input);
		this.actionChooser = Actions.createActionChooserRecyclable(WhenDone.BE_DONE);
		this.visionSupplier = visionSupplier;
		this.dimensions = dimensions;
	}
	public void setPerspective(Perspective perspective){
		this.perspective = Objects.requireNonNull(perspective);
	}

	@Override
	protected void onStart() {
		super.onStart();
		final ControllerRumble rumble = input.getDriverRumble();
		if(rumble.isConnected()){
			rumble.rumbleTime(250, .4);
		}
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		final Perspective perspective;
		final TaskSystem.Task task = taskSystemSupplier.get().getCurrentTask();
		if(input.getFirstPersonHoldButton().isDown()){
			perspective = task == TaskSystem.Task.HATCH
					? dimensions.getHatchManipulatorPerspective()
					: dimensions.getCargoManipulatorPerspective();
		} else {
			perspective = this.perspective;
		}
		if(input.getVisionAlign().isDown()){
			if(input.getVisionAlign().isPressed()){
				actionChooser.setNextAction(LineUpCreator.createLineUpAction(
						visionSupplier,
						task == TaskSystem.Task.CARGO ? dimensions.getCargoCameraID() : dimensions.getHatchCameraID(),
						task == TaskSystem.Task.CARGO ? dimensions.getCargoManipulatorPerspective() : dimensions.getHatchManipulatorPerspective(),
						new BestVisionPacketSelector(),
						driveSupplier, orientationSupplier,  null, null, null
				));
			}
			actionChooser.update();
			if(actionChooser.isDone()){
				final ControllerRumble rumble = input.getDriverRumble();
				if(rumble.isConnected()) {
					rumble.rumbleTimeout(200, .3);
				}
			}
		} else {
			if(actionChooser.isActive()){
				actionChooser.end();
			}
			final SwerveDrive drive = Objects.requireNonNull(driveSupplier.get());
			
			final JoystickPart joystick = input.getMovementJoy();
			final double x = joystick.getZonedCorrectX();
			final double y = joystick.getZonedCorrectY();
			
			final double turnAmount = input.getTurnAmount().getZonedPosition();
			
			final InputPart speedInputPart = input.getMovementSpeed();
			final double speed;
			if (speedInputPart.isDeadzone()) {
				speed = 0;
			} else {
				speed = MathUtil.conservePow(speedInputPart.getPosition(), 2);
			}
			
			drive.setControl(x, y, turnAmount, speed, perspective);
		}
	}

}
