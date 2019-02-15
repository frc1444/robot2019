package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.RobotDimensions;
import com.first1444.frc.robot2019.autonomous.options.AutonomousType;
import com.first1444.frc.robot2019.deepspace.GamePieceType;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
import com.first1444.frc.robot2019.autonomous.options.StartingPosition;
import com.first1444.frc.util.MathUtil;
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
		
		if (autonomousType == AutonomousType.DO_NOTHING) {
			if(startingPosition != null || gamePieceType != null || slotLevel != null){
				throw new IllegalArgumentException("All should be null! startingPosition: " + startingPosition + " gamePieceType: " + gamePieceType + " slotLevel" + slotLevel);
			}
		} else if (autonomousType == AutonomousType.CROSS_LINE_FORWARD) {
			if(startingPosition != null || gamePieceType != null || slotLevel != null){
				throw new IllegalArgumentException("All should be null! startingPosition: " + startingPosition + " gamePieceType: " + gamePieceType + " slotLevel" + slotLevel);
			}
			actionQueue.add(actionCreator.createGoStraight(55, .5, 90)); // cross line
		} else if (autonomousType == AutonomousType.CROSS_LINE_SIDE) {
			if(gamePieceType != null || slotLevel != null){
				throw new IllegalArgumentException("All should be null! gamePieceType: " + gamePieceType + " slotLevel" + slotLevel);
			}
			final boolean isLeft;
			if (startingPosition == StartingPosition.LEFT) {
				isLeft = true;
			} else if (startingPosition == StartingPosition.RIGHT) {
				isLeft = false;
			} else {
				throw new IllegalArgumentException("Cross Line Side doesn't support starting position: " + startingPosition);
			}
			actionQueue.add(actionCreator.createGoStraight(65, .5, isLeft ? 180 : 0)); // go towards wall
			actionQueue.add(actionCreator.createGoStraight(55, .5, 90)); // cross line
		} else if (autonomousType == AutonomousType.OFF_CENTER_CARGO_SHIP) {
			if (slotLevel != SlotLevel.LEVEL1) {
				throw new IllegalArgumentException("Got level: " + slotLevel + " with autonomous mode " + autonomousType);
			}
			if (gamePieceType == null) {
				throw new IllegalArgumentException("No game piece type selected! Got null! autonomous mode: " + autonomousType);
			}
			if (startingPosition == null) {
				throw new IllegalArgumentException("A left or right starting position must be selected for off center auto!");
			}
			final boolean isLeft;
			if (startingPosition == StartingPosition.MIDDLE_LEFT) {
				isLeft = true;
			} else if (startingPosition == StartingPosition.MIDDLE_RIGHT) {
				isLeft = false;
			} else {
				throw new IllegalArgumentException("Off Center Cargo Ship doesn't support starting position: " + startingPosition);
			}
			// go 100 inches
			actionQueue.add(actionCreator.createGoStraight(40, .3, 90, startingOrientation));
			actionQueue.add(actionCreator.createGoStraight(30, .7, 90));
			actionQueue.add(actionCreator.createGoStraight(30, .3, 90));
			// went 100 inches
			
			if (gamePieceType == GamePieceType.CARGO) {
				actionQueue.add(actionCreator.createCargoShipPlaceCargo(null, null));
			} else {
				actionQueue.add(actionCreator.createCargoShipPlaceHatch(null, null));
			}
		} else if (autonomousType == AutonomousType.SIDE_CARGO_SHIP) {
			if (gamePieceType == null) {
				throw new IllegalArgumentException("Side Cargo ship auto requires a game piece to be selected!");
			}
			if (slotLevel != SlotLevel.LEVEL1) {
				throw new IllegalArgumentException("Side Cargo ship auto only supports level 1. Got level: " + slotLevel);
			}
			final boolean sideCargoShipIsLeft;
			if (startingPosition == StartingPosition.LEFT) {
				sideCargoShipIsLeft = true;
			} else if (startingPosition == StartingPosition.RIGHT) {
				sideCargoShipIsLeft = false;
			} else {
				throw new IllegalArgumentException("Side Cargo Ship doesn't support starting position: " + startingPosition);
			}
			double distance = 212.8 + (48.28 / 2.0); //we need to go this distance
			final double faceAngle = (sideCargoShipIsLeft ? 0 : 180) + getManipulatorOffset(gamePieceType); // face the manipulator towards the cargo ship
//				actionQueue.add(actionCreator.createGoStraight())
			actionQueue.add(actionCreator.createGoStraight(40, .3, 90, startingOrientation));
			distance -= 40;
			actionQueue.add(actionCreator.createGoStraight(170, .7, 90, startingOrientation));
			distance -= 170;
			actionQueue.add(actionCreator.createGoStraight(distance, .3, 90, startingOrientation));
			
			if (MathUtil.minDistance(faceAngle, startingOrientation, 360) > 5) { // only rotate if we need to
				actionQueue.add(actionCreator.createTurnToOrientation(faceAngle));
			}
			final Action cargoShipSuccess = new Actions.ActionQueueBuilder(
					Actions.createRunOnce(() -> System.out.println("We successfully placed something on the cargo ship. TODO: Write code to make this do more stuff."))
			).immediatelyDoNextWhenDone(true).canBeDone(true).canRecycle(false).build();
			
			if (gamePieceType == GamePieceType.HATCH) {
				actionQueue.add(actionCreator.createCargoShipPlaceHatch(
						Actions.createRunOnce(() -> System.out.println("Failed to place Hatch!")),
						cargoShipSuccess
				));
			} else {
				actionQueue.add(actionCreator.createCargoShipPlaceCargo(
						Actions.createRunOnce(() -> System.out.println("Failed to place Cargo!")),
						cargoShipSuccess
				));
			}
		} else if (autonomousType == AutonomousType.SIDE_ROCKET) {
			if (gamePieceType == null) {
				throw new IllegalArgumentException("A game piece must be specified for side rocket autonomous!");
			}
			if (slotLevel == null) {
				throw new IllegalArgumentException("A Slot Level must be specified for side rocket autonomous!");
			}
			final boolean sideRocketIsLeft;
			if (startingPosition == StartingPosition.LEFT) {
				sideRocketIsLeft = true;
			} else if (startingPosition == StartingPosition.RIGHT) {
				sideRocketIsLeft = false;
			} else {
				throw new IllegalArgumentException("Side Rocket doesn't support starting position: " + startingPosition);
			}
			actionQueue.add(actionCreator.createGoStraight(69.56, .3, sideRocketIsLeft ? 180 : 0, startingOrientation));
			actionQueue.add(actionCreator.createGoStraight(201.13 - 95.28 - 60, .5, 90, startingOrientation)); // the 60 is random
//				actionQueue.add(actionCreator.createTurnToOrientation(90 - (sideRocketIsLeft ? -20 : 20)));
			
			final Action rocketSuccess = new Actions.ActionQueueBuilder(
					Actions.createRunOnce(() -> System.out.println("We successfully placed something on the rocket. TODO: Write code to make this do more stuff."))
			).immediatelyDoNextWhenDone(true).canBeDone(true).canRecycle(false).build();
			
			if (gamePieceType == GamePieceType.HATCH) {
				actionQueue.add(actionCreator.createRocketPlaceHatch(
						slotLevel,
						Actions.createRunOnce(() -> System.out.println("Failed to place hatch on rocket")),
						rocketSuccess
				));
			} else {
				actionQueue.add(actionCreator.createRocketPlaceCargo(
						slotLevel,
						Actions.createRunOnce(() -> System.out.println("Failed to place cargo on rocket")),
						rocketSuccess
				));
			}
		} else {
			System.out.println("Doing nothing for autonomous type: " + autonomousType);
		}
		return actionQueue;

	}
	private double getManipulatorOffset(GamePieceType gamePieceType){
		Objects.requireNonNull(gamePieceType);
		return gamePieceType == GamePieceType.HATCH
				? dimensions.getHatchManipulatorPerspective().getOffset(null)
				: dimensions.getCargoManipulatorPerspective().getOffset(null);
	}
}
