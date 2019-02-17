package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.Robot;
import com.first1444.frc.robot2019.autonomous.actions.*;
import com.first1444.frc.robot2019.autonomous.actions.vision.LineUpAction;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
import com.first1444.frc.robot2019.subsystems.HatchIntake;
import com.first1444.frc.robot2019.subsystems.Lift;
import com.first1444.frc.robot2019.vision.BestVisionPacketSelector;
import me.retrodaredevil.action.*;

import java.util.Map;

public class RobotAutonActionCreator implements AutonActionCreator {
	private static final Map<SlotLevel, Lift.Position> SLOT_MAP = Map.of(
			SlotLevel.LEVEL1, Lift.Position.LEVEL1,
			SlotLevel.LEVEL2, Lift.Position.LEVEL2,
			SlotLevel.LEVEL3, Lift.Position.LEVEL3
	);
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
		return createLineUpWithRunner(true, Lift.Position.CARGO_CARGO_SHIP, failAction, successAction);
	}
	
	@Override
	public Action createRocketPlaceCargo(SlotLevel slotLevel, Action failAction, Action successAction) {
		return createLineUpWithRunner(false, SLOT_MAP.get(slotLevel), failAction, successAction);
	}
	
	@Override
	public Action createRocketPlaceHatch(SlotLevel slotLevel, Action failAction, Action successAction) {
		return createLineUpWithRunner(true, SLOT_MAP.get(slotLevel), failAction, successAction);
	}
	private Action createLineUpWithRunner(boolean hatch, Lift.Position liftPosition, Action failAction, Action successAction){
		/*
		If you're thinking this looks complicated, you are right. This code tries to use the RaiseLift action by
		updating it and ending it depending on if certain things happen. It could eventually be more simply replaced
		by just a call to the Lift's setDesiredPosition once after lining up and possibly while lining up if we're within
		less than 50 inches. Right now, it essentially does that, but it also makes sure the lift is in the correct position
		before continuing to the success action.
		 */
		final boolean[] hasLiftBeenMoved = {false};
		final Action raiseLift = Actions.createSupplementaryAction(
				new RaiseLift(robot::getLift, liftPosition),
				Actions.createRunOnce(() -> hasLiftBeenMoved[0] = true)
		);
		
		final var lineUp = new LineUpAction(
				robot.getVisionSupplier(),
				hatch ? robot.getDimensions().getHatchCameraID() : robot.getDimensions().getCargoCameraID(),
				hatch ? robot.getDimensions().getHatchManipulatorPerspective() : robot.getDimensions().getCargoManipulatorPerspective(),
				new BestVisionPacketSelector(), robot::getDrive, robot::getOrientation,
				new Actions.ActionQueueBuilder( // fail
						Actions.createDynamicActionRunner(() -> {
							final boolean liftMoved = hasLiftBeenMoved[0];
							if(raiseLift.isActive()){
								raiseLift.end();
								if(!liftMoved) throw new AssertionError();
							}
							if(liftMoved){ // if the lift has been moved, bring it back to level 1
								return new RaiseLift(robot::getLift, Lift.Position.LEVEL1);
							}
							return null;
						}),
						failAction
				).build(),
				new Actions.ActionQueueBuilder( // success
						Actions.createDynamicActionRunner(() -> { // make sure the lift gets to the correct position
							if(!hasLiftBeenMoved[0] || raiseLift.isActive()){ // if the lift hasn't been moved or if the lift still needs to finish
								return raiseLift;
							}
							return null;
						}),
						(
								hatch
								? HatchGrabAction.createDrop(robot::getHatchIntake)
								: new TimedCargoIntake(500, robot::getCargoIntake, 1)
						),
						successAction
				).build(),
				robot.getSoundSender()
		);
		
		return Actions.createLinkedActionRunner(
				Actions.createSupplementaryLinkedAction(
						lineUp,
						Actions.createWaitToStartAction(Actions.createRunForever(raiseLift::update), () -> lineUp.getInchesAway() < 50) // this action doesn't have to end because it's supplementary
				),
				WhenDone.CLEAR_ACTIVE_AND_BE_DONE, true
		);
	}
}
