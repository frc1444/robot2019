package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.deepspace.SlotLevel;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.LinkedAction;
import me.retrodaredevil.action.WhenDone;

/**
 * NOTE: Each returned action will probably not be a {@link LinkedAction} so you do not have to
 * use {@link me.retrodaredevil.action.Actions#createLinkedActionRunner(Action, WhenDone, boolean)} to wrap them.
 * Doing so is not recommended and most if not all of the time, it will do nothing.
 */
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
