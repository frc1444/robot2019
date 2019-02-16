package com.first1444.frc.robot2019.subsystems;

import com.first1444.frc.robot2019.ShuffleboardMap;
import com.first1444.frc.robot2019.input.RobotInput;
import edu.wpi.cscore.*;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.shuffleboard.SendableCameraWrapper;
import me.retrodaredevil.action.SimpleAction;

import java.util.Objects;
import java.util.function.Supplier;

public class CameraSystem extends SimpleAction {
	private final Supplier<TaskSystem> taskSystemSupplier;
	private final UsbCamera hatch;
	private final UsbCamera cargo;
	private final VideoSink videoSink;
	private TaskSystem.Task lastTask = null;
	public CameraSystem(ShuffleboardMap shuffleboardMap, Supplier<TaskSystem> taskSystemSupplier) {
		super(false);
		this.taskSystemSupplier = taskSystemSupplier;
		
		hatch = CameraServer.getInstance().startAutomaticCapture(0);
		cargo = CameraServer.getInstance().startAutomaticCapture(1);
		setupCamera(hatch);
		setupCamera(cargo);
		
		videoSink = CameraServer.getInstance().addSwitchedCamera("Toggle Camera");
		videoSink.setSource(hatch);
		shuffleboardMap.getUserTab().add(SendableCameraWrapper.wrap(videoSink.getSource()));
	}
	private void setupCamera(UsbCamera camera){
		camera.setVideoMode(VideoMode.PixelFormat.kMJPEG, 320, 240, 9);
		camera.setConnectVerbose(0); // so it doesn't spam the console with annoying messages if it's disconnected
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		final TaskSystem taskSystem = taskSystemSupplier.get();
		Objects.requireNonNull(taskSystem);
		final TaskSystem.Task newTask = taskSystem.getCurrentTask();
		if(newTask != lastTask){
			lastTask = newTask;
			if(newTask == TaskSystem.Task.CARGO){
				videoSink.setSource(cargo);
			} else if(newTask == TaskSystem.Task.HATCH){
				videoSink.setSource(hatch);
			} else {
				throw new UnsupportedOperationException("Unsupported task: " + newTask);
			}
		}
	}
}
