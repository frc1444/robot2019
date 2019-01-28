package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.deepspace.SlotLevel;
import me.retrodaredevil.action.Action;

public interface AutonActionCreator {
	Action createTurnToOrientation(double desiredOrientation);
	@Deprecated
	Action createGoStraight(double distanceInches, double speed, double angleDegrees);
	Action createGoStraight(double distanceInches, double speed, double angleDegrees, double faceDirectionDegrees);

	Action createCargoShipPlaceHatch(Action failAction, Action successAction);
	Action createCargoShipPlaceCargo(Action failAction, Action successAction);

	Action createRocketPlaceHatch(SlotLevel slotLevel, Action failAction, Action successAction);

	Action createRocketPlaceCargo(SlotLevel slotLevel, Action failAction, Action successAction);
}
