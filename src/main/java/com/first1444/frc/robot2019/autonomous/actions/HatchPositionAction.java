package com.first1444.frc.robot2019.autonomous.actions;

import com.first1444.frc.robot2019.subsystems.HatchIntake;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HatchPositionAction extends SimpleAction {
	private final Supplier<HatchIntake> hatchIntakeSupplier;
	private final Consumer<HatchIntake> hatchIntakeAction;
	private HatchPositionAction(Supplier<HatchIntake> hatchIntakeSupplier, Consumer<HatchIntake> hatchIntakeAction) {
		super(true);
		this.hatchIntakeSupplier = hatchIntakeSupplier;
		this.hatchIntakeAction = hatchIntakeAction;
	}
	public static Action createReady(Supplier<HatchIntake> hatchIntakeSupplier){
		return new HatchPositionAction(hatchIntakeSupplier, HatchIntake::readyPosition);
	}
	public static Action createStow(Supplier<HatchIntake> hatchIntakeSupplier){
		return new HatchPositionAction(hatchIntakeSupplier, HatchIntake::stowedPosition);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		final HatchIntake hatchIntake = hatchIntakeSupplier.get();
		Objects.requireNonNull(hatchIntake);
		hatchIntakeAction.accept(hatchIntake);
		setDone(hatchIntake.isDesiredPositionReached());
	}
}
