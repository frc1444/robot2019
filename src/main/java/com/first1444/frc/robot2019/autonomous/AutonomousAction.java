package com.first1444.frc.robot2019.autonomous;

import me.retrodaredevil.action.ActionChooser;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.action.WhenDone;

import java.util.function.Supplier;

public class AutonomousAction extends SimpleAction {

	private final AutonActionCreator actionCreator;
	private final Supplier<Boolean> isLeftSideSupplier;
	private final Supplier<AutonomousType> autonomousTypeSupplier;

	private final ActionChooser actionChooser;

	public AutonomousAction(AutonActionCreator actionCreator, Supplier<Boolean> isLeftSideSupplier, Supplier<AutonomousType> autonomousTypeSupplier) {
		super(true);
		this.actionCreator = actionCreator;
		this.isLeftSideSupplier = isLeftSideSupplier;
		this.autonomousTypeSupplier = autonomousTypeSupplier;

		this.actionChooser = Actions.createActionChooserRecyclable(WhenDone.CLEAR_ACTIVE_AND_BE_DONE);
	}

	@Override
	protected void onStart() {
		super.onStart();
		final boolean isLeft = isLeftSideSupplier.get();
		final AutonomousType type = autonomousTypeSupplier.get();
		if(type == null){
			throw new NullPointerException("The autonomous type cannot be null!");
		}

		switch (type){
			case DO_NOTHING:
				break;
			case CROSS_LINE_FORWARD:
				actionChooser.setNextAction(actionCreator.createGoStraight(55, 90)); // cross line
			case CROSS_LINE_SIDE:
				actionChooser.setNextAction(
						new Actions.ActionQueueBuilder(
								actionCreator.createGoStraight(65, isLeft ? 180 : 0), // go towards wall
								actionCreator.createGoStraight(55, 90) // cross line
						)
								.canBeDone(true)
								.canRecycle(false)
								.build()
				);
		}
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		actionChooser.update();
	}

	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		actionChooser.end();
		actionChooser.setToClearAction();
	}

	@Override
	protected void onIsDoneRequest() {
		setDone(actionChooser.isDone());
	}
}
