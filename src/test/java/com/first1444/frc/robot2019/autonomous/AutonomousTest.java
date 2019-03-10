package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.autonomous.options.AfterComplete;
import com.first1444.frc.robot2019.autonomous.options.AutonomousType;
import com.first1444.frc.robot2019.autonomous.options.LineUpType;
import com.first1444.frc.robot2019.autonomous.options.StartingPosition;
import com.first1444.frc.robot2019.deepspace.GamePieceType;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
import me.retrodaredevil.action.Action;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutonomousTest {
	public static void main(String[] args){
		final AutonomousModeCreator modeCreator = new AutonomousModeCreator(new TestAutonActionCreator(System.out), Constants.Dimensions.INSTANCE);
		runMode(modeCreator, AutonomousType.OFF_CENTER_CARGO_SHIP, StartingPosition.MIDDLE_RIGHT, GamePieceType.HATCH, SlotLevel.LEVEL1, LineUpType.NO_VISION, null, 90);
		runMode(modeCreator, AutonomousType.DO_NOTHING, null, null, null, LineUpType.NO_VISION, null, 90);
		runMode(modeCreator, AutonomousType.CROSS_LINE_FORWARD, null, null, null, LineUpType.NO_VISION, null, 90);
		runMode(modeCreator, AutonomousType.CROSS_LINE_SIDE, StartingPosition.RIGHT, null, null, LineUpType.NO_VISION, null, 90);
		for(LineUpType lineUpType : LineUpType.values()) {
			
			runMode(modeCreator, AutonomousType.SIDE_CARGO_SHIP, StartingPosition.RIGHT, GamePieceType.HATCH, SlotLevel.LEVEL1, lineUpType, null, 180);
			runMode(modeCreator, AutonomousType.SIDE_CARGO_SHIP, StartingPosition.LEFT, GamePieceType.HATCH, SlotLevel.LEVEL1, lineUpType, null, 0);
			
			runMode(modeCreator, AutonomousType.SIDE_CARGO_SHIP, StartingPosition.RIGHT, GamePieceType.CARGO, SlotLevel.LEVEL1, lineUpType, null, 0);
			runMode(modeCreator, AutonomousType.SIDE_CARGO_SHIP, StartingPosition.LEFT, GamePieceType.CARGO, SlotLevel.LEVEL1, lineUpType, null, 180);
			
			runMode(modeCreator, AutonomousType.SIDE_ROCKET, StartingPosition.RIGHT, GamePieceType.HATCH, SlotLevel.LEVEL1, lineUpType, null, 90);
		}
	}
	private static void runMode(AutonomousModeCreator modeCreator, AutonomousType autonomousType,
								StartingPosition startingPosition, GamePieceType gamePieceType,
								SlotLevel slotLevel, LineUpType lineUpType, AfterComplete afterComplete, double startingOrientation){
		System.out.println(autonomousType.getName());
		System.out.println(startingPosition);
		System.out.println(gamePieceType);
		System.out.println(slotLevel);
		System.out.println(lineUpType);
		System.out.println(afterComplete);
		System.out.println(startingOrientation);
		runUntilDone(modeCreator.createAction(autonomousType, startingPosition, gamePieceType, slotLevel, lineUpType, afterComplete, startingOrientation));
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
		for(AutonomousType type : AutonomousType.values()) for(GamePieceType gamePiece : type.getGamePieces()) for(StartingPosition startingPosition : type.getStartingPositions()) for(SlotLevel slotLevel : type.getSlotLevels()) for(LineUpType lineUpType : type.getLineUpTypes())
			for(AfterComplete afterComplete : Stream.concat(type.getAfterCompleteOptions().stream(), Stream.of((AfterComplete) null)).collect(Collectors.toList()))
		{
			for(double startingOrientation : new double[] {0, 90, 180, 270}) {
				runMode(modeCreator, type, startingPosition, gamePiece, slotLevel, lineUpType, afterComplete, startingOrientation);
			}
		}
	}
}
