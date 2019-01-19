package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.deepspace.SlotLevel;
import me.retrodaredevil.action.Action;

public interface AutonActionCreator {
	Action createTurnToOrientation(double desiredOrientation);
	Action createGoStraight(double distanceInches, double speed, double angleDegrees);
	Action createGoStraight(double distanceInches, double speed, double angleDegrees, double faceDirectionDegrees);

	Action createCargoShipPlaceHatch();
	Action createCargoShipPlaceCargo();

	Action createRocketPlaceHatch(SlotLevel slotLevel);

	Action createRocketPlaceCargo(SlotLevel slotLevel);
}
