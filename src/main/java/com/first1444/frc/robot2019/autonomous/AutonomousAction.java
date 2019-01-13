package com.first1444.frc.robot2019.autonomous;

import me.retrodaredevil.action.*;

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

		final ActionQueue actionQueue = new Actions.ActionQueueBuilder()
				.canRecycle(false)
				.canBeDone(true)
				.immediatelyDoNextWhenDone(true) // once an action is finished, do the next one immediately
				.build();

		switch (type){
			case DO_NOTHING:
				break;
			case CROSS_LINE_FORWARD:
				actionQueue.add(actionCreator.createGoStraight(55, .5, 90)); // cross line
				break;
			case CROSS_LINE_SIDE:
				actionQueue.add(actionCreator.createGoStraight(65, .5, isLeft ? 180 : 0)); // go towards wall
				actionQueue.add(actionCreator.createGoStraight(55, .5, 90)); // cross line
				break;
			case CENTER_CARGO_SHIP:
				break;
			case OFF_CENTER_CARGO_SHIP:
				// go 100 inches
				actionQueue.add(actionCreator.createGoStraight(40, .3, 90));
				actionQueue.add(actionCreator.createGoStraight(30, .7, 90));
				actionQueue.add(actionCreator.createGoStraight(30, .3, 90));
				// went 100 inches

				actionQueue.add(actionCreator.createCargoShipPlaceHatch());
				actionQueue.add(actionCreator.createCargoShipPlaceCargo());
				break;
		}
		actionChooser.setNextAction(actionQueue);
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
