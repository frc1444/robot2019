package com.first1444.frc.robot2019.subsystems;

import com.first1444.frc.robot2019.ShuffleboardMap;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import me.retrodaredevil.action.SimpleAction;

import java.util.Objects;
import java.util.function.Supplier;

public class CameraSystem extends SimpleAction {
	private static final VideoMode VIDEO_MODE = new VideoMode(VideoMode.PixelFormat.kMJPEG, 280, 210, 11);
	/** The compression level. A number between 0 and 100. The lower the value, the more compressed it is.*/
	private static final int COMPRESSION_LEVEL = -1;
	private final Supplier<TaskSystem> taskSystemSupplier;
	private final UsbCamera hatch;
	private final UsbCamera cargo;
	private final MjpegServer videoSink;
	private TaskSystem.Task lastTask = null;
	public CameraSystem(ShuffleboardMap shuffleboardMap, Supplier<TaskSystem> taskSystemSupplier) {
		super(false);
		this.taskSystemSupplier = taskSystemSupplier;
		
		if(RobotBase.isSimulation()){
			// These values are for your own computer. If you want to simulate something in the future, change these for yourself.
			hatch = CameraServer.getInstance().startAutomaticCapture(0);
			cargo = CameraServer.getInstance().startAutomaticCapture(2);
		} else {
			// Values for the cameras on the roborio
			hatch = CameraServer.getInstance().startAutomaticCapture(0);
			cargo = CameraServer.getInstance().startAutomaticCapture(1);
		}
		setupCamera(hatch);
		setupCamera(cargo);
		
		videoSink = CameraServer.getInstance().addSwitchedCamera("Toggle Camera");
		final VideoSource source = videoSink.getSource();
		if(COMPRESSION_LEVEL >= 0) {
			videoSink.setCompression(COMPRESSION_LEVEL);
			videoSink.setDefaultCompression(COMPRESSION_LEVEL);
		}
		shuffleboardMap.getUserTab().add("My Toggle Camera", source).withSize(6, 5).withPosition(2, 0);
	}
	private void setupCamera(UsbCamera camera){
		final double ratio = camera.getVideoMode().height / (double) camera.getVideoMode().width;
		camera.setVideoMode(VIDEO_MODE.pixelFormat, VIDEO_MODE.width, (int) (VIDEO_MODE.width * ratio), VIDEO_MODE.fps);
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
				System.out.println("Source is now cargo. Is connected: " + (cargo == null ? "null" : cargo.isConnected()));
			} else if(newTask == TaskSystem.Task.HATCH){
				videoSink.setSource(hatch);
				System.out.println("Source is now hatch. Is connected: " + (hatch == null ? "null" : hatch.isConnected()));
			} else {
				throw new UnsupportedOperationException("Unsupported task: " + newTask);
			}
		}
	}
}
