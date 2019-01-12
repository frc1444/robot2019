package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.Robot;
import me.retrodaredevil.action.Action;

public class RobotAutonActionCreator implements AutonActionCreator {
	private final Robot robot;

	public RobotAutonActionCreator(Robot robot) {
		this.robot = robot;
	}

	@Override
	public Action createTurnToOrientation(double desiredOrientation) {
		return new TurnToOrientation(desiredOrientation, robot::getDrive, robot::getOrientation);
	}

	@Override
	public Action createGoStraight(double distanceInches, double angleDegrees) {
		return new GoStraight(distanceInches, angleDegrees, null, robot::getDrive, robot::getOrientation);
	}

	@Override
	public Action createGoStraight(double distanceInches, double angleDegrees, double faceDirectionDegrees) {
		return new GoStraight(distanceInches, angleDegrees, faceDirectionDegrees, robot::getDrive, robot::getOrientation);
	}
}
