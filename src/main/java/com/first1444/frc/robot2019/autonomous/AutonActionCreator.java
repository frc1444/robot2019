package com.first1444.frc.robot2019.autonomous;

import me.retrodaredevil.action.Action;

public interface AutonActionCreator {
	Action createTurnToOrientation(double desiredOrientation);
	Action createGoStraight(double distanceInches, double speed, double angleDegrees);
	Action createGoStraight(double distanceInches, double speed, double angleDegrees, double faceDirectionDegrees);

	Action createCargoShipPlaceHatch();
	Action createCargoShipPlaceCargo();

	Action createRocket1PlaceHatch();
	Action createRocket2PlaceHatch();
	Action createRocket3PlaceHatch();

	Action createRocket1PlaceCargo();
	Action createRocket2PlaceCargo();
	Action createRocket3PlaceCargo();
}
