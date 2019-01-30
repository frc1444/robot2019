package com.first1444.frc.robot2019.vision;

public interface VisionSupplier {
	VisionInstant getInstant(int cameraID);
}
