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
	public Action createGoStraight(double distanceInches, double speed, double angleDegrees) {
		return new GoStraight(distanceInches, speed, angleDegrees, null, robot::getDrive, robot::getOrientation);
	}

	@Override
	public Action createGoStraight(double distanceInches, double speed, double angleDegrees, double faceDirectionDegrees) {
		return new GoStraight(distanceInches, speed, angleDegrees, faceDirectionDegrees, robot::getDrive, robot::getOrientation);
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
	public Action createRocket1PlaceHatch() {
		return null;
	}

	@Override
	public Action createRocket2PlaceHatch() {
		return null;
	}

	@Override
	public Action createRocket3PlaceHatch() {
		return null;
	}

	@Override
	public Action createRocket1PlaceCargo() {
		return null;
	}

	@Override
	public Action createRocket2PlaceCargo() {
		return null;
	}

	@Override
	public Action createRocket3PlaceCargo() {
		return null;
	}


}
