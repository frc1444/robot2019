package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.autonomous.options.AutonomousType;
import com.first1444.frc.robot2019.autonomous.options.StartingPosition;
import com.first1444.frc.robot2019.deepspace.GamePieceType;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
import me.retrodaredevil.action.Action;

public class AutonomousTest {
	public static void main(String[] args){
		final AutonActionCreator actionCreator = new TestAutonActionCreator(System.out);
		final AutonomousModeCreator modeCreator = new AutonomousModeCreator(actionCreator);
		runUntilDone(modeCreator.createAction(AutonomousType.OFF_CENTER_CARGO_SHIP, StartingPosition.RIGHT, GamePieceType.HATCH, SlotLevel.LEVEL1));
	}
	private static void runUntilDone(Action action){
		System.out.println("Starting");
		do {
			action.update();
		} while (!action.isDone());
		action.end();
		System.out.println("Ended!");
	}
}
