package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.RobotDimensions;
import com.first1444.frc.robot2019.autonomous.options.AutonomousType;
import com.first1444.frc.robot2019.autonomous.options.LineUpType;
import com.first1444.frc.robot2019.deepspace.GamePieceType;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
import com.first1444.frc.robot2019.autonomous.options.StartingPosition;
import com.first1444.frc.robot2019.subsystems.Lift;
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
			final LineUpType lineUpType,
			final double startingOrientation){
		Objects.requireNonNull(autonomousType);
		Objects.requireNonNull(lineUpType);
		
		final ActionQueue actionQueue = new Actions.ActionQueueBuilder()
				.canRecycle(false)
				.canBeDone(true)
				.immediatelyDoNextWhenDone(true) // once an action is finished, do the next one immediately
				.build();
		
		if (autonomousType == AutonomousType.DO_NOTHING) {
			if(startingPosition != null || gamePieceType != null || slotLevel != null)
				throw new IllegalArgumentException("All should be null! startingPosition: " + startingPosition + " gamePieceType: " + gamePieceType + " slotLevel" + slotLevel);
			if(lineUpType != LineUpType.NO_VISION)
				throw new IllegalArgumentException("lineUpType must be NO_VISION! It's: " + lineUpType);
		} else if (autonomousType == AutonomousType.CROSS_LINE_FORWARD) {
			if(startingPosition != null || gamePieceType != null || slotLevel != null)
				throw new IllegalArgumentException("All should be null! startingPosition: " + startingPosition + " gamePieceType: " + gamePieceType + " slotLevel" + slotLevel);
			if(lineUpType != LineUpType.NO_VISION)
				throw new IllegalArgumentException("lineUpType must be NO_VISION! It's: " + lineUpType);
			actionQueue.add(actionCreator.createGoStraight(55, .5, 90)); // cross line
		} else if (autonomousType == AutonomousType.CROSS_LINE_SIDE) {
			if(gamePieceType != null || slotLevel != null)
				throw new IllegalArgumentException("All should be null! gamePieceType: " + gamePieceType + " slotLevel" + slotLevel);
			if(lineUpType != LineUpType.NO_VISION)
				throw new IllegalArgumentException("lineUpType must be NO_VISION! It's: " + lineUpType);
			
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
			if(gamePieceType == GamePieceType.CARGO){
				System.out.println("Creating auto off center cargo ship with cargo. We shouldn't use this mode because the cargo ship will never have hatches on the front two bays");
			}
			final boolean isLeft;
			if (startingPosition == StartingPosition.MIDDLE_LEFT) {
				isLeft = true;
			} else if (startingPosition == StartingPosition.MIDDLE_RIGHT) {
				isLeft = false;
			} else {
				throw new IllegalArgumentException("Off Center Cargo Ship doesn't support starting position: " + startingPosition);
			}
			// It's about 130 inches to the cargo ship
			
			// go 100 inches
			actionQueue.add(actionCreator.createGoStraight(40, .3, 90, startingOrientation)); // get off hab
			actionQueue.add(actionCreator.createGoStraight(30, .7, 90, startingOrientation)); // drive a little
			actionQueue.add(actionCreator.createGoStraight(30, .3, 90, startingOrientation)); // drive slower
			// went 100 inches
			final double faceAngle = 90 + getManipulatorOffset(gamePieceType); // face the manipulator towards the cargo ship
			if (MathUtil.minDistance(faceAngle, startingOrientation, 360) > 5) { // only rotate if we need to
				actionQueue.add(actionCreator.createTurnToOrientation(faceAngle));
				System.out.println("Creating auto mode where we have to turn to face cargo ship. Why would you make the robot start in another orientation anyway?");
			}
			
			if(lineUpType == LineUpType.USE_VISION) {
				if (gamePieceType == GamePieceType.CARGO) {
					actionQueue.add(actionCreator.createCargoShipPlaceCargoUseVision(null, null));
				} else {
					actionQueue.add(actionCreator.createCargoShipPlaceHatchUseVision(null, null));
				}
			} else {
				// drive a total of 30 more inches
				actionQueue.add(actionCreator.createGoStraight(10, .3, 90, startingOrientation)); // go forward
				actionQueue.add(actionCreator.createGoStraight(20, .15, 90, startingOrientation));
				if(gamePieceType == GamePieceType.CARGO){
					actionQueue.add(actionCreator.createRaiseLift(Lift.Position.CARGO_CARGO_SHIP));
					actionQueue.add(actionCreator.createReleaseCargo());
				} else {
					actionQueue.add(actionCreator.createDropHatch());
				}
			}
		} else if (autonomousType == AutonomousType.SIDE_CARGO_SHIP) {
			if (gamePieceType == null) {
				throw new IllegalArgumentException("Side Cargo ship auto requires a game piece to be selected!");
			}
			if (slotLevel != SlotLevel.LEVEL1) {
				throw new IllegalArgumentException("Side Cargo ship auto only supports level 1. Got level: " + slotLevel);
			}
			final boolean isLeft;
			if (startingPosition == StartingPosition.LEFT) {
				isLeft = true;
			} else if (startingPosition == StartingPosition.RIGHT) {
				isLeft = false;
			} else {
				throw new IllegalArgumentException("Side Cargo Ship doesn't support starting position: " + startingPosition);
			}
			double distance = 212.8 - (48.28 / 2.0); //we need to go this distance
//				actionQueue.add(actionCreator.createGoStraight())
			actionQueue.add(actionCreator.createGoStraight(40, .3, 90, startingOrientation));
			distance -= 40;
			actionQueue.add(actionCreator.createGoStraight(130, .7, 90, startingOrientation));
			distance -= 130;
			actionQueue.add(actionCreator.createGoStraight(distance, .3, 90, startingOrientation));
			
			final double faceAngle = (isLeft ? 0 : 180) + getManipulatorOffset(gamePieceType); // face the manipulator towards the cargo ship
			if (MathUtil.minDistance(faceAngle, startingOrientation, 360) > 5) { // only rotate if we need to
				actionQueue.add(actionCreator.createTurnToOrientation(faceAngle));
				System.out.println("Creating a side cargo auto mode where we have to rotate! Why not just start the robot in the correct orientation?");
			}
			final Action cargoShipSuccess = new Actions.ActionQueueBuilder(
					Actions.createRunOnce(() -> System.out.println("We successfully placed something on the cargo ship. TODO: Write code to make this do more stuff."))
			).immediatelyDoNextWhenDone(true).canBeDone(true).canRecycle(false).build();
			
			if(lineUpType == LineUpType.USE_VISION) {
				if (gamePieceType == GamePieceType.HATCH) {
					actionQueue.add(actionCreator.createCargoShipPlaceHatchUseVision(
							Actions.createRunOnce(() -> System.out.println("Failed to place Hatch!")),
							cargoShipSuccess
					));
				} else {
					actionQueue.add(actionCreator.createCargoShipPlaceCargoUseVision(
							Actions.createRunOnce(() -> System.out.println("Failed to place Cargo!")),
							cargoShipSuccess
					));
				}
			} else {
				actionQueue.add(actionCreator.createGoStraight(20, .2, isLeft ? 0 : 180, faceAngle)); // go towards cargo ship
				if(gamePieceType == GamePieceType.HATCH){
					actionQueue.add(actionCreator.createDropHatch());
				} else {
					actionQueue.add(actionCreator.createRaiseLift(Lift.Position.CARGO_CARGO_SHIP));
					actionQueue.add(actionCreator.createReleaseCargo());
				}
			}
		} else if (autonomousType == AutonomousType.SIDE_ROCKET) {
			if(gamePieceType != GamePieceType.HATCH){
				throw new IllegalArgumentException("Side rocket only supports the hatch! Got: " + gamePieceType);
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
			
			final Action rocketSuccess = new Actions.ActionQueueBuilder(
					Actions.createRunOnce(() -> System.out.println("We successfully placed something on the rocket. TODO: Write code to make this do more stuff."))
			).immediatelyDoNextWhenDone(true).canBeDone(true).canRecycle(false).build();
			
			if(lineUpType == LineUpType.USE_VISION) {
				actionQueue.add(actionCreator.createRocketPlaceHatchUseVision(
						slotLevel,
						Actions.createRunOnce(() -> System.out.println("Failed to place hatch on rocket")),
						rocketSuccess
				));
			} else {
				actionQueue.add(actionCreator.createGoStraight(60, .3, 90, startingOrientation));
				final double driveAngle = 90 - (sideRocketIsLeft ? -20 : 20);
				actionQueue.add(actionCreator.createTurnToOrientation(driveAngle));
				actionQueue.add(actionCreator.createGoStraight(20, .2, driveAngle, driveAngle));
				actionQueue.add(actionCreator.createDropHatch());
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
