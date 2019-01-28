package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.WhenDone;

import java.io.PrintStream;

public class TestAutonActionCreator implements AutonActionCreator {
	private final PrintStream out;
	
	public TestAutonActionCreator(PrintStream out) {
		this.out = out;
	}
	public TestAutonActionCreator(){
		this(System.out);
	}
	private Action createStringAction(String string){
		return Actions.createRunOnce(() -> out.println(string));
	}
	
	@Override
	public Action createTurnToOrientation(double desiredOrientation) {
		return createStringAction("Turning to orientation: " + desiredOrientation + " degrees");
	}
	
	@Override
	public Action createGoStraight(double distanceInches, double speed, double angleDegrees) {
		return createStringAction("Going straight for " + distanceInches + " inches at " + Constants.DECIMAL_FORMAT.format(speed)
				+ " with " + angleDegrees + " degrees heading.");
	}
	
	@Override
	public Action createGoStraight(double distanceInches, double speed, double angleDegrees, double faceDirectionDegrees) {
		return createStringAction("Going straight for " + distanceInches + " inches at " + Constants.DECIMAL_FORMAT.format(speed)
				+ " with " + angleDegrees + " degrees heading while facing " + faceDirectionDegrees + " degrees.");
	}
	
	@Override
	public Action createCargoShipPlaceHatch(Action failAction, Action successAction) {
		return Actions.createLinkedActionRunner(
				Actions.createLinkedAction(createStringAction("Placing hatch at cargo ship"), successAction),
				WhenDone.CLEAR_ACTIVE_AND_BE_DONE, true
		);
	}
	
	@Override
	public Action createCargoShipPlaceCargo(Action failAction, Action successAction) {
		return Actions.createLinkedActionRunner(
				Actions.createLinkedAction(createStringAction("Placing cargo at cargo ship"), successAction),
				WhenDone.CLEAR_ACTIVE_AND_BE_DONE, true
		);
	}
	
	@Override
	public Action createRocketPlaceCargo(SlotLevel slotLevel, Action failAction, Action successAction) {
		return Actions.createLinkedActionRunner(
				Actions.createLinkedAction(createStringAction("Placing cargo on rocket at " + slotLevel), successAction),
				WhenDone.CLEAR_ACTIVE_AND_BE_DONE, true
		);
	}
	
	@Override
	public Action createRocketPlaceHatch(SlotLevel slotLevel, Action failAction, Action successAction) {
		return Actions.createLinkedActionRunner(
				Actions.createLinkedAction(createStringAction("Placing hatch on rocket at " + slotLevel), successAction),
				WhenDone.CLEAR_ACTIVE_AND_BE_DONE, true
		);
	}
}
