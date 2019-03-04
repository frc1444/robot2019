package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.autonomous.options.AutonomousType;
import com.first1444.frc.robot2019.autonomous.options.LineUpType;
import com.first1444.frc.robot2019.autonomous.options.StartingPosition;
import com.first1444.frc.robot2019.deepspace.GamePieceType;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
import me.retrodaredevil.action.Action;
import org.junit.jupiter.api.Test;

public class AutonomousTest {
	public static void main(String[] args){
		final AutonomousModeCreator modeCreator = new AutonomousModeCreator(new TestAutonActionCreator(System.out), Constants.Dimensions.INSTANCE);
		runMode(modeCreator, AutonomousType.OFF_CENTER_CARGO_SHIP, StartingPosition.MIDDLE_RIGHT, GamePieceType.HATCH, SlotLevel.LEVEL1, LineUpType.USE_VISION, 90);
		runMode(modeCreator, AutonomousType.DO_NOTHING, null, null, null, LineUpType.USE_VISION, 90);
		runMode(modeCreator, AutonomousType.CROSS_LINE_FORWARD, null, null, null, LineUpType.USE_VISION, 90);
		runMode(modeCreator, AutonomousType.CROSS_LINE_SIDE, StartingPosition.RIGHT, null, null, LineUpType.USE_VISION, 90);
		
		runMode(modeCreator, AutonomousType.SIDE_CARGO_SHIP, StartingPosition.RIGHT, GamePieceType.HATCH, SlotLevel.LEVEL1, LineUpType.USE_VISION, 180);
		runMode(modeCreator, AutonomousType.SIDE_CARGO_SHIP, StartingPosition.LEFT, GamePieceType.HATCH, SlotLevel.LEVEL1, LineUpType.USE_VISION, 0);
		
		runMode(modeCreator, AutonomousType.SIDE_CARGO_SHIP, StartingPosition.RIGHT, GamePieceType.CARGO, SlotLevel.LEVEL1, LineUpType.USE_VISION, 0);
		runMode(modeCreator, AutonomousType.SIDE_CARGO_SHIP, StartingPosition.LEFT, GamePieceType.CARGO, SlotLevel.LEVEL1, LineUpType.USE_VISION, 180);
		
		runMode(modeCreator, AutonomousType.SIDE_ROCKET, StartingPosition.RIGHT, GamePieceType.HATCH, SlotLevel.LEVEL1, LineUpType.USE_VISION, 90);
	}
	private static void runMode(AutonomousModeCreator modeCreator, AutonomousType autonomousType,
								StartingPosition startingPosition, GamePieceType gamePieceType, SlotLevel slotLevel, LineUpType lineUpType, double startingOrientation){
		System.out.println(autonomousType.getName());
		System.out.println(startingPosition);
		System.out.println(gamePieceType);
		System.out.println(slotLevel);
		System.out.println(lineUpType);
		runUntilDone(modeCreator.createAction(autonomousType, startingPosition, gamePieceType, slotLevel, lineUpType, startingOrientation));
		System.out.println();
	}
	private static void runUntilDone(Action action){
		System.out.println("Starting");
		do {
			action.update();
		} while (!action.isDone());
		action.end();
		System.out.println("Ended!");
	}
	@Test
	void testAllAuto(){
		final AutonomousModeCreator modeCreator = new AutonomousModeCreator(new TestAutonActionCreator(System.out), Constants.Dimensions.INSTANCE);
		for(AutonomousType type : AutonomousType.values()) for(GamePieceType gamePiece : type.getGamePieces()) for(StartingPosition startingPosition : type.getStartingPositions()) for(SlotLevel slotLevel : type.getSlotLevels()) for(LineUpType lineUpType : type.getLineUpTypes()){
			for(double startingOrientation : new double[] {0, 90, 180, 270}) {
				runMode(modeCreator, type, startingPosition, gamePiece, slotLevel, lineUpType, startingOrientation);
			}
		}
	}
}
