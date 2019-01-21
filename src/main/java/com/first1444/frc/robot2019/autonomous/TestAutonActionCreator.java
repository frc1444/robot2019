package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

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
	public Action createCargoShipPlaceHatch() {
		return createStringAction("Placing hatch at cargo ship");
	}
	
	@Override
	public Action createCargoShipPlaceCargo() {
		return createStringAction("Placing cargo at cargo ship");
	}
	
	@Override
	public Action createRocketPlaceCargo(SlotLevel slotLevel) {
		return createStringAction("Placing cargo on rocket at " + slotLevel);
	}
	
	@Override
	public Action createRocketPlaceHatch(SlotLevel slotLevel) {
		return createStringAction("Placing hatch on rocket at " + slotLevel);
	}
}
