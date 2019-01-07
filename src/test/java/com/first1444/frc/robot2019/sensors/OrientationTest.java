package com.first1444.frc.robot2019.sensors;


import com.first1444.frc.robot2019.Perspective;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class OrientationTest {
	@Test
	void testOrientation(){
		assertEquals(0, createOrientation(90).getOffset(Perspective.DRIVER_STATION));
		assertEquals(270, createOrientation(180).getOffset(Perspective.DRIVER_STATION));
		assertEquals(180, createOrientation(-90).getOffset(Perspective.DRIVER_STATION));
		assertEquals(90, createOrientation(0).getOffset(Perspective.DRIVER_STATION));

		for(double value : new double[]{0, 90, 180, 270}){
			assertEquals(0, createOrientation(value).getOffset(Perspective.ROBOT_FORWARD_CAM));
			assertEquals(270, createOrientation(value).getOffset(Perspective.ROBOT_RIGHT_CAM));
			assertEquals(90, createOrientation(value).getOffset(Perspective.ROBOT_LEFT_CAM));
			assertEquals(180, createOrientation(value).getOffset(Perspective.ROBOT_BACK_CAM));
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
	private Orientation createOrientation(double orientation){
		return () -> orientation;
	}
}
