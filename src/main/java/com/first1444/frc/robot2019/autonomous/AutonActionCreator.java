package com.first1444.frc.robot2019.autonomous;

import me.retrodaredevil.action.Action;

public interface AutonActionCreator {
	Action createTurnToOrientation(double desiredOrientation);
	Action createGoStraight(double distanceInches, double angleDegrees);
}
