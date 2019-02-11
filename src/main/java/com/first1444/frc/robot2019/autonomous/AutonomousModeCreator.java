package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.RobotDimensions;
import com.first1444.frc.robot2019.autonomous.options.AutonomousType;
import com.first1444.frc.robot2019.deepspace.GamePieceType;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
import com.first1444.frc.robot2019.autonomous.options.StartingPosition;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.ActionQueue;
import me.retrodaredevil.action.Actions;

import java.util.Objects;

public class AutonomousModeCreator {
	private final AutonActionCreator actionCreator;
	private final RobotDimensions dimensions;
	
	public AutonomousModeCreator(AutonActionCreator actionCreator, RobotDimensions dimensions) {
		this.actionCreator = actionCreator;
		this.dimensions = dimensions;
	}
	
	/**
	 *
	 * @param autonomousType The autonomous type
	 * @param startingPosition The starting position of the robot. Can be null
	 * @param gamePieceType The game piece type. Can be null
	 * @param slotLevel The slot level to place the game piece type at. Can be null
	 * @param startingOrientation The starting orientation of the robot
	 * @throws IllegalArgumentException Thrown if either startingPosition, gamePieceType, or level aren't supported by autonomousType
	 * @throws NullPointerException if autonomousType is null
	 * @return The autonomous action
	 */
	public Action createAction(
			final AutonomousType autonomousType,
			final StartingPosition startingPosition,
			final GamePieceType gamePieceType,
			final SlotLevel slotLevel,
			final double startingOrientation){
		Objects.requireNonNull(autonomousType);
		
		final ActionQueue actionQueue = new Actions.ActionQueueBuilder()
				.canRecycle(false)
				.canBeDone(true)
				.immediatelyDoNextWhenDone(true) // once an action is finished, do the next one immediately
				.build();
		
		final Boolean isLeft;
		if(startingPosition == null){
			isLeft = null;
		} else {
			isLeft = startingPosition == StartingPosition.LEFT;
		}
		
		switch (autonomousType){
			case DO_NOTHING:
				break;
			case CROSS_LINE_FORWARD:
				actionQueue.add(actionCreator.createGoStraight(55, .5, 90)); // cross line
				break;
			case CROSS_LINE_SIDE:
				if(startingPosition == null){
					throw new IllegalArgumentException("The startingPosition is null for " + autonomousType + " auto!");
				}
				actionQueue.add(actionCreator.createGoStraight(65, .5, isLeft ? 180 : 0)); // go towards wall
				actionQueue.add(actionCreator.createGoStraight(55, .5, 90)); // cross line
				break;
			case OFF_CENTER_CARGO_SHIP:
				if(slotLevel != SlotLevel.LEVEL1){
					throw new IllegalArgumentException("Got level: " + slotLevel + " with autonomous mode " + autonomousType);
				}
				if(gamePieceType == null){
					throw new IllegalArgumentException("No game piece type selected! Got null! autonomous mode: " + autonomousType);
				}
				if(startingPosition == null){
					throw new IllegalArgumentException("A left or right starting position must be selected for off center auto!");
				}
				// go 100 inches
				actionQueue.add(actionCreator.createGoStraight(40, .3, 90));
				actionQueue.add(actionCreator.createGoStraight(30, .7, 90));
				actionQueue.add(actionCreator.createGoStraight(30, .3, 90));
				// went 100 inches
				
				if(gamePieceType == GamePieceType.CARGO){
					actionQueue.add(actionCreator.createCargoShipPlaceCargo(null, null));
				} else {
					actionQueue.add(actionCreator.createCargoShipPlaceHatch(null, null));
				}
				break;
			case SIDE_CARGO_SHIP:
				if(gamePieceType == null){
					throw new IllegalArgumentException("Side Cargo ship auto requires a game piece to be selected!");
				}
				if(startingPosition == null){
					throw new IllegalArgumentException("There must be a starting position specified for side autonomous!");
				}
				if(slotLevel != SlotLevel.LEVEL1){
					throw new IllegalArgumentException("Side Cargo ship auto only supports level 1. Got level: " + slotLevel);
				}
				double distance = 212.8 + (48.28 / 2.0); //we need to go this distance
				final double manipulatorOffsetAngle = gamePieceType == GamePieceType.HATCH
						? dimensions.getHatchManipulatorPerspective().getOffset(null)
						: dimensions.getCargoManipulatorPerspective().getOffset(null);
				final double faceAngle = (isLeft ? 0 : 180) + manipulatorOffsetAngle; // face the manipulator towards the cargo ship
//				actionQueue.add(actionCreator.createGoStraight())
				actionQueue.add(actionCreator.createGoStraight(40, .3, 90, startingOrientation));
				distance -= 40;
				actionQueue.add(actionCreator.createGoStraight(170, .7, 90, startingOrientation));
				distance -= 170;
				actionQueue.add(actionCreator.createGoStraight(distance, .3, 90, startingOrientation));
				
				actionQueue.add(actionCreator.createTurnToOrientation(faceAngle));
				if(gamePieceType == GamePieceType.HATCH){
					actionQueue.add(actionCreator.createCargoShipPlaceHatch(null, null));
				} else {
					actionQueue.add(actionCreator.createCargoShipPlaceCargo(null, null));
				}
				
				
				
				break;
			case SIDE_ROCKET:
				if(gamePieceType == null){
					throw new IllegalArgumentException("A game piece must be specified for side rocket autonomous!");
				}
				if(startingPosition == null){
					throw new IllegalArgumentException("A starting position needs to be specified for side rocket autonomous!");
				}
				if(slotLevel == null){
					throw new IllegalArgumentException("A Slot Level must be specified for side rocket autonomous!");
				}
				actionQueue.add(actionCreator.createGoStraight(69.56, .3, isLeft ? 180 : 0, startingOrientation));
				actionQueue.add(actionCreator.createGoStraight(201.13 - 95.28 - 60, .5, 90, startingOrientation)); // the 20 is random
//				actionQueue.add(actionCreator.createTurnToOrientation(90 - (isLeft ? -20 : 20)));
				
				if(gamePieceType == GamePieceType.HATCH){
					actionQueue.add(actionCreator.createRocketPlaceHatch(slotLevel, null, null));
				} else {
					actionQueue.add(actionCreator.createRocketPlaceCargo(slotLevel, null, null));
				}
				
				break;
		}
		return actionQueue;

	}
}
