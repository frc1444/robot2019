package com.first1444.frc.robot2019.subsystems;

import com.first1444.frc.robot2019.ShuffleboardMap;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.shuffleboard.SendableCameraWrapper;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import me.retrodaredevil.action.SimpleAction;

import java.util.Objects;
import java.util.function.Supplier;

public class CameraSystem extends SimpleAction {
	private final Supplier<TaskSystem> taskSystemSupplier;
	private final UsbCamera hatch;
	private final UsbCamera cargo;
	private final MjpegServer videoSink;
	private TaskSystem.Task lastTask = null;
	public CameraSystem(ShuffleboardMap shuffleboardMap, Supplier<TaskSystem> taskSystemSupplier) {
		super(false);
		this.taskSystemSupplier = taskSystemSupplier;
		
		hatch = CameraServer.getInstance().startAutomaticCapture();
		cargo = CameraServer.getInstance().startAutomaticCapture();
		setupCamera(hatch);
		setupCamera(cargo);
		
		videoSink = CameraServer.getInstance().addSwitchedCamera("Toggle Camera");
		videoSink.setSource(cargo);
		videoSink.setSource(hatch);
		shuffleboardMap.getUserTab().add("My Toggle Camera", SendableCameraWrapper.wrap(videoSink.getSource())).withSize(3, 4);
	}
	private void setupCamera(UsbCamera camera){
		camera.setVideoMode(VideoMode.PixelFormat.kMJPEG, 320, 240, 9);
		camera.setConnectVerbose(0); // so it doesn't spam the console with annoying messages if it's disconnected
		camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		final TaskSystem taskSystem = taskSystemSupplier.get();
		Objects.requireNonNull(taskSystem);
		final TaskSystem.Task newTask = taskSystem.getCurrentTask();
		SmartDashboard.putString("current task from camera system", newTask.toString());
		if(newTask != lastTask){
			lastTask = newTask;
			if(newTask == TaskSystem.Task.CARGO){
				videoSink.setSource(cargo);
				System.out.println("Source is now cargo. Is connected: " + cargo.isConnected());
			} else if(newTask == TaskSystem.Task.HATCH){
				videoSink.setSource(hatch);
				System.out.println("Source is now hatch. Is connected: " + hatch.isConnected());
			} else {
				throw new UnsupportedOperationException("Unsupported task: " + newTask);
			}
		}
	}
}
