package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.Robot;
import com.first1444.frc.robot2019.autonomous.actions.GoStraight;
import com.first1444.frc.robot2019.autonomous.actions.LineUpAction;
import com.first1444.frc.robot2019.autonomous.actions.TurnToOrientation;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
import com.first1444.frc.robot2019.vision.BestVisionPacketSelector;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.WhenDone;

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
	public Action createCargoShipPlaceHatch(Action failAction, Action successAction) {
		return createRocketPlaceHatch(SlotLevel.LEVEL1, failAction, successAction);
	}

	@Override
	public Action createCargoShipPlaceCargo(Action failAction, Action successAction) {
		return createRocketPlaceCargo(SlotLevel.LEVEL1, failAction, successAction);
	}
	
	@Override
	public Action createRocketPlaceCargo(SlotLevel slotLevel, Action failAction, Action successAction) {
		
		return new LineUpAction(
				robot.getVisionSupplier(), robot.getDimensions().getCargoCameraID(),
				robot.getDimensions().getCargoManipulatorPerspective(),
				new BestVisionPacketSelector(), robot::getDrive,
				failAction,
				successAction, // TODO do something here
				robot.getSoundSender());

	}
	
	@Override
	public Action createRocketPlaceHatch(SlotLevel slotLevel, Action failAction, Action successAction) {
		return Actions.createLinkedActionRunner(
				new LineUpAction(
						robot.getVisionSupplier(), robot.getDimensions().getHatchCameraID(),
						robot.getDimensions().getHatchManipulatorPerspective(),
						new BestVisionPacketSelector(), robot::getDrive,
						failAction,
						successAction, // TODO do something here
						robot.getSoundSender()
				),
				WhenDone.CLEAR_ACTIVE_AND_BE_DONE, false
		);

	}
}
