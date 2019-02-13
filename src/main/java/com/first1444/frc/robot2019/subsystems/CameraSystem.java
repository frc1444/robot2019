package com.first1444.frc.robot2019.subsystems;

import com.first1444.frc.robot2019.ShuffleboardMap;
import com.first1444.frc.robot2019.input.RobotInput;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.shuffleboard.SendableCameraWrapper;
import me.retrodaredevil.action.SimpleAction;

public class CameraSystem extends SimpleAction {
	private final RobotInput input;
	private final UsbCamera hatch;
	private final UsbCamera cargo;
	private final VideoSink server;
	public CameraSystem(ShuffleboardMap shuffleboardMap, RobotInput input) {
		super(false);
		this.input = input;
		
		hatch = CameraServer.getInstance().startAutomaticCapture(0);
		cargo = CameraServer.getInstance().startAutomaticCapture(1);
		setupCamera(hatch);
		setupCamera(cargo);
		
		server = CameraServer.getInstance().getServer();
		server.setSource(hatch);
		shuffleboardMap.getUserTab().add("Camera", SendableCameraWrapper.wrap(server.getSource()));
	}
	private void setupCamera(UsbCamera camera){
		camera.setVideoMode(VideoMode.PixelFormat.kMJPEG, 320, 240, 9);
		camera.setConnectVerbose(0); // so it doesn't spam the console with annoying messages
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		if(input.getCameraToggleButton().isDown()){
			if(server.getSource() == hatch){
				server.setSource(cargo);
			} else {
				server.setSource(hatch);
			}
		}
	}
}
