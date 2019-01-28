package com.first1444.frc.robot2019.sensors;


import com.first1444.frc.robot2019.Perspective;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class OrientationTest {
	@Test
	void testOrientation(){
		assertEquals(0, Perspective.DRIVER_STATION.getOffset(90.0));
		assertEquals(270, Perspective.DRIVER_STATION.getOffset(180.0));
		assertEquals(180, Perspective.DRIVER_STATION.getOffset(-90.0));
		assertEquals(90, Perspective.DRIVER_STATION.getOffset(0.0));

		for(double value : new double[]{0, 90, 180, 270}){
			assertEquals(0, Perspective.ROBOT_FORWARD_CAM.getOffset(value));
			assertEquals(270, Perspective.ROBOT_RIGHT_CAM.getOffset(value));
			assertEquals(90, Perspective.ROBOT_LEFT_CAM.getOffset(value));
			assertEquals(180, Perspective.ROBOT_BACK_CAM.getOffset(value));
		}
	}
	@Test
	void testDefaultOrientation(){
		final DummyGyro gyro = new DummyGyro(0);
		final Orientation o = new DefaultOrientation(gyro, () -> 90.0, false); // NOTE The isGyroReversed is set to false

		assertEquals(90, o.getOrientation());

		gyro.setAngle(90); // gyro has rotated 90 degrees CC
		assertEquals(180, o.getOrientation());

		gyro.setAngle(180);
		assertEquals(270, o.getOrientation());

		gyro.setAngle(-90);
		assertEquals(0, o.getOrientation());
	}
}
