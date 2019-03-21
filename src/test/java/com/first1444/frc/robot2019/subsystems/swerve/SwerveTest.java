package com.first1444.frc.robot2019.subsystems.swerve;

import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.util.MathUtil;
import org.junit.jupiter.api.Test;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

final class SwerveTest {

	@Test
	void testStraightSwerve(){
		testStraightSwerve(90, 1, 90,0, 1, 1, Perspective.DRIVER_STATION);
		testStraightSwerve(90, 1, 0,1, 0, 1, Perspective.DRIVER_STATION);
		testStraightSwerve(90, 1, -90,0, -1, 1, Perspective.DRIVER_STATION);
		testStraightSwerve(90, 1, 180,-1, 0, 1, Perspective.DRIVER_STATION);
	}
	@Test
	void testStillRightTurn(){
		final Orientation orientation = () -> 90; // straight forward
		final FourSwerveCollection collection = new ImmutableFourSwerveCollection(
				new TestSwerveModule(1, 45), // front left
				new TestSwerveModule(1, 270 + 45), // front right
				new TestSwerveModule(1, 90 + 45), // rear left
				new TestSwerveModule(1, 180 + 45) // rear right
		);
		final FourWheelSwerveDrive drive = new FourWheelSwerveDrive(() -> orientation, collection, 20, 20);
//		final FourWheelSwerveDrive drive = new FourWheelSwerveDrive(() -> orientation, collection, 27.375, 22.25);
		drive.setControl(0, 0, 1, 1.0, Perspective.DRIVER_STATION); // turn right
		drive.update();
	}
	@Test
	void testStillLeftTurn(){
		final Orientation orientation = () -> 90; // straight forward
		final FourSwerveCollection collection = new ImmutableFourSwerveCollection(
				new TestSwerveModule(1, 180 + 45),
				new TestSwerveModule(1, 90 + 45),
				new TestSwerveModule(1, 270 + 45),
				new TestSwerveModule(1, 45)
		);
		final FourWheelSwerveDrive drive = new FourWheelSwerveDrive(() -> orientation, collection, 20, 20);
		drive.setControl(0, 0, -1, 1.0, Perspective.DRIVER_STATION); // turn right
		drive.update();
	}

	private void testStraightSwerve(double orientationValue, double expectedSpeed, double expectedPosition, double x, double y, double speed, Perspective perspective){
		final Orientation orientation = () -> orientationValue; // straight forward
		final FourSwerveCollection collection = createStraightFourSwerveCollection(expectedSpeed, expectedPosition);
		final FourWheelSwerveDrive drive = new FourWheelSwerveDrive(() -> orientation, collection, 20, 20);
		drive.setControl(x, y, 0, speed, perspective);
		drive.update();
	}

	private static FourSwerveCollection createStraightFourSwerveCollection(double expectedSpeed, double expectedPosition){
		final SwerveModule module = new TestSwerveModule(expectedSpeed, expectedPosition);
		return new ImmutableFourSwerveCollection(module, module, module, module);
	}

	private static class TestSwerveModule implements SwerveModule {
		private final double expectedSpeed;
		private final double expectedPosition;

		private TestSwerveModule(double expectedSpeed, double expectedPosition) {
			this.expectedSpeed = expectedSpeed;
			this.expectedPosition = expectedPosition;
		}

		@Override
		public void setTargetSpeed(double speed) {
			if(abs(speed) > 1){
				fail();
			}
			assertEquals(expectedSpeed, speed, 0.01);
		}

		@Override
		public void setTargetAngle(double positionDegrees) {
			final double expected = MathUtil.mod(expectedPosition, 360);
			final double position = MathUtil.mod(positionDegrees, 360);
			assertEquals(expected, position, .01);

		}
		
		@Override
		public double getTotalDistanceTraveledInches() {
            throw new UnsupportedOperationException();
		}
		
		@Override
		public double getTargetSpeed() {
			throw new UnsupportedOperationException();
		}
		@Override
		public double getTargetAngle() {
			throw new UnsupportedOperationException();
		}
		@Override
		public double getCurrentAngle() {
			throw new UnsupportedOperationException();
		}
		@Override
		public String getName() {
            throw new UnsupportedOperationException();
		}
	}

}
