package com.first1444.frc.robot2019.autonomous.actions;

import com.first1444.frc.robot2019.sensors.Orientation;
import me.retrodaredevil.action.Action;

import java.util.function.DoubleConsumer;

public class TurnToOrientationTest {
	private static final DoubleConsumer TURN_AMOUNT_PRINTER = (turnAmount) -> System.out.println("turnAmount: " + turnAmount);
	public static void main(String[] args){
		final Orientation orientation = () -> 90;
		final Action action = new TurnToOrientation(95.0, TURN_AMOUNT_PRINTER, () -> orientation);
		action.update();
	}
}
