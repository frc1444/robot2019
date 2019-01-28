package com.first1444.frc.robot2019;

public interface RobotDimensions {
	
	Perspective getHatchManipulatorPerspective();
	Perspective getCargoManipulatorPerspective();
	
	int getHatchCameraID();
	int getCargoCameraID();
}
