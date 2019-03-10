package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.RobotDimensions;
import com.first1444.frc.robot2019.autonomous.options.AutonomousType;
import com.first1444.frc.robot2019.autonomous.options.LineUpType;
import com.first1444.frc.robot2019.deepspace.FieldDimensions;
import com.first1444.frc.robot2019.deepspace.GamePieceType;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
import com.first1444.frc.robot2019.autonomous.options.StartingPosition;
import com.first1444.frc.robot2019.subsystems.Lift;
import com.first1444.frc.util.MathUtil;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.ActionQueue;
import me.retrodaredevil.action.Actions;

import java.util.Objects;

import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class AutonomousModeCreator {
	private static final double SIDE_CARGO_SHIP_LONG_DISTANCE_ANGLE = 4.5;
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
			actionQueue.add(actionCreator.createLogMessageAction("Do nothing autonomous action starting and complete!"));
		} else if (autonomousType == AutonomousType.CROSS_LINE_FORWARD) {
			if(startingPosition != null || gamePieceType != null || slotLevel != null)
				throw new IllegalArgumentException("All should be null! startingPosition: " + startingPosition + " gamePieceType: " + gamePieceType + " slotLevel" + slotLevel);
			if(lineUpType != LineUpType.NO_VISION)
				throw new IllegalArgumentException("lineUpType must be NO_VISION! It's: " + lineUpType);
			actionQueue.add(actionCreator.createLogMessageAction("Cross line forward autonomous starting!"));
			actionQueue.add(actionCreator.createGoStraight(55, .5, 90)); // cross line
			actionQueue.add(actionCreator.createLogMessageAction("Cross line forward autonomous ending!"));
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
			if (gamePieceType != GamePieceType.HATCH) {
				throw new IllegalArgumentException("autonomousType: " + autonomousType + " (off center cargo ship) must use game piece hatch! It's: " + gamePieceType);
			}
			if (startingPosition == null) {
				throw new IllegalArgumentException("A left or right starting position must be selected for off center auto!");
			}
			final boolean isLeft; // this value was initially going to be used to tell which vision target to use, but I haven't got around to refactoring
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
			assert gamePieceType == GamePieceType.HATCH : "always true";
			final double faceAngle = 90 + getManipulatorOffset(GamePieceType.HATCH); // face the manipulator towards the cargo ship
			if (MathUtil.minDistance(faceAngle, startingOrientation, 360) > 5) { // only rotate if we need to
				actionQueue.add(actionCreator.createLogWarningAction("We are turning to face the target. Next time, start the robot in the correct position. faceAngle: " + faceAngle + " startingOrientation: " + startingOrientation));
				actionQueue.add(actionCreator.createTurnToOrientation(faceAngle));
				System.out.println("Creating auto mode where we have to turn to face cargo ship. Why would you make the robot start in another orientation anyway?");
			}
			// we are now turned to faceAngle
			
			if(lineUpType == LineUpType.USE_VISION) {
				actionQueue.add(actionCreator.createCargoShipPlaceHatchUseVision(
						actionCreator.createLogMessageAction("Placed hatch on center cargo ship with vision!"),
						actionCreator.createLogWarningAction("Failed to place hatch on center cargo ship with vision!")
				));
			} else {
				// drive a total of 30 more inches
				actionQueue.add(new Actions.ActionMultiplexerBuilder(
						actionCreator.createExtendHatch(), // ready position
						actionCreator.createGoStraight(10, .3, faceAngle, faceAngle) // go forward
				).build());
				final double hatchExtend = dimensions.getHatchManipulatorActiveExtendDistance();
				if(hatchExtend < 20) {
					actionQueue.add(actionCreator.createGoStraight(20 - hatchExtend, .15, faceAngle, faceAngle));
				}
				actionQueue.add(actionCreator.createDropHatch());
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
			final double longDistanceAngle = isLeft ? 90 + SIDE_CARGO_SHIP_LONG_DISTANCE_ANGLE : 90 - SIDE_CARGO_SHIP_LONG_DISTANCE_ANGLE;
			
			final double towardsCargoShipAngle = (isLeft ? 0 : 180);
			final double faceAngle = towardsCargoShipAngle + getManipulatorOffset(gamePieceType); // face the manipulator towards the cargo ship
			final double distanceDegreesToFaceAngle = MathUtil.minDistance(faceAngle, startingOrientation, 360); // in range [0..180]
			
			double distance = 212.8 - FieldDimensions.HAB_FLAT_DISTANCE - 22; //we need to go this distance // 22 is random, but I measured it
			if(distanceDegreesToFaceAngle > 135 || distanceDegreesToFaceAngle < 45){ // turn 180 or turn 0
				distance += getManipulatorSideWidth(gamePieceType) / 2.0;
			} else {
				distance += getManipulatorSideDepth(gamePieceType) / 2.0;
			}
			System.out.println("We will go a distance of " + distance);
			actionQueue.add(actionCreator.createGoStraight(40, .3, 90, startingOrientation));
			distance -= 40;
			actionQueue.add(actionCreator.createGoStraight(115 / sin(toRadians(longDistanceAngle)), .7, longDistanceAngle, startingOrientation));
			distance -= 115;
			actionQueue.add(actionCreator.createGoStraight(distance, .3, 90, startingOrientation));
			
			if (distanceDegreesToFaceAngle > 5) { // only rotate if we need to
				actionQueue.add(actionCreator.createTurnToOrientation(faceAngle));
				System.out.println("Creating a side cargo auto mode where we have to rotate! Why not just start the robot in the correct orientation?");
			}
			final Action cargoShipSuccess = new Actions.ActionQueueBuilder(
					Actions.createRunOnce(actionCreator.createLogMessageAction("We successfully placed something on the cargo ship. TODO: Write code to make this do more stuff."))
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
				final Action driveAction = actionCreator.createGoStraight(20, .2, towardsCargoShipAngle, faceAngle);
				if(gamePieceType == GamePieceType.HATCH){
					actionQueue.add(new Actions.ActionMultiplexerBuilder(
							actionCreator.createExtendHatch(),
							driveAction // go towards cargo ship
					).build());
					actionQueue.add(actionCreator.createDropHatch());
				} else {
					actionQueue.add(new Actions.ActionMultiplexerBuilder(
							actionCreator.createLogMessageAction("The lift will be raised to CARGO_CARGO_SHIP while we drive the 20 inches"),
							actionCreator.createRaiseLift(Lift.Position.CARGO_CARGO_SHIP),
							driveAction // go towards cargo ship
					).forceUpdateInOrder(true).build());
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
			actionQueue.add(actionCreator.createGoStraight(69.56 + 6, .3, sideRocketIsLeft ? 180 : 0, startingOrientation)); // the 6 is random
			actionQueue.add(actionCreator.createGoStraight(201.13 - 95.28 - FieldDimensions.HAB_LIP_DISTANCE - 60, .5, 90, startingOrientation)); // the 60 is random
			
			final Action rocketSuccess = new Actions.ActionQueueBuilder(
					Actions.createRunOnce(actionCreator.createLogMessageAction("We successfully placed something on the rocket. TODO: Write code to make this do more stuff."))
			).immediatelyDoNextWhenDone(true).canBeDone(true).canRecycle(false).build();
			
			if(lineUpType == LineUpType.USE_VISION) {
				actionQueue.add(actionCreator.createRocketPlaceHatchUseVision(
						slotLevel,
						Actions.createRunOnce(() -> System.out.println("Failed to place hatch on rocket")),
						rocketSuccess
				));
			} else {
				actionQueue.add(actionCreator.createGoStraight(60 - 1, .3, 90, startingOrientation)); // the 3 is random
				final double driveAngle = 90 - (sideRocketIsLeft ? -25 : 25);
				actionQueue.add(actionCreator.createTurnToOrientation(driveAngle));
				actionQueue.add(actionCreator.createGoStraight(30, .2, driveAngle, driveAngle));
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
	private double getManipulatorSideWidth(GamePieceType gamePieceType){
		Objects.requireNonNull(gamePieceType);
		return gamePieceType == GamePieceType.HATCH
				? dimensions.getHatchSideWidth()
				: dimensions.getCargoSideWidth();
	}
	private double getManipulatorSideDepth(GamePieceType gamePieceType){
		Objects.requireNonNull(gamePieceType);
		return gamePieceType == GamePieceType.HATCH
				? dimensions.getHatchSideDepth()
				: dimensions.getCargoSideDepth();
	}
}
