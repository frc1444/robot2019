package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.Robot;
import com.first1444.frc.robot2019.autonomous.actions.GoStraight;
import com.first1444.frc.robot2019.autonomous.actions.TurnToOrientation;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
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
	public Action createGoStraight(double distanceInches, double speed, double angleDegrees) {
		return GoStraight.createGoStraightAtHeading(distanceInches, speed, angleDegrees, null, robot::getDrive, robot::getOrientation);
	}

	@Override
	public Action createGoStraight(double distanceInches, double speed, double angleDegrees, double faceDirectionDegrees) {
		return GoStraight.createGoStraightAtHeading(distanceInches, speed, angleDegrees, faceDirectionDegrees, robot::getDrive, robot::getOrientation);
	}

	@Override
	public Action createCargoShipPlaceHatch() {
		return null;
	}

	@Override
	public Action createCargoShipPlaceCargo() {
		return null;
	}
	
	@Override
	public Action createRocketPlaceCargo(SlotLevel slotLevel) {
		return null;
	}
	
	@Override
	public Action createRocketPlaceHatch(SlotLevel slotLevel) {
		return null;
	}
}
