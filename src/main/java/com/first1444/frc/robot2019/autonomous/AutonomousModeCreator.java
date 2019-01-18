package com.first1444.frc.robot2019.autonomous;

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
	
	public AutonomousModeCreator(AutonActionCreator actionCreator) {
		this.actionCreator = actionCreator;
	}
	
	/**
	 *
	 * @param autonomousType
	 * @param startingPosition
	 * @param gamePieceType
	 * @param slotLevel
	 * @throws IllegalArgumentException Thrown if either startingPosition, gamePieceType, or level aren't supported by autonomousType
	 * @throws NullPointerException if autonomousType is null
	 * @return The autonomous action
	 */
	public Action createAction(
			final AutonomousType autonomousType,
			final StartingPosition startingPosition,
			final GamePieceType gamePieceType,
			final SlotLevel slotLevel){
		Objects.requireNonNull(autonomousType);
		
		final ActionQueue actionQueue = new Actions.ActionQueueBuilder()
				.canRecycle(false)
				.canBeDone(true)
				.immediatelyDoNextWhenDone(true) // once an action is finished, do the next one immediately
				.build();
		
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
				final boolean isLeft = startingPosition == StartingPosition.LEFT;
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
					actionQueue.add(actionCreator.createCargoShipPlaceCargo());
				} else {
					actionQueue.add(actionCreator.createCargoShipPlaceHatch());
				}
				break;
			case SIDE_CARGO_SHIP:
				
				break;
			case SIDE_ROCKET:
				
				break;
		}
		return actionQueue;

	}
}
