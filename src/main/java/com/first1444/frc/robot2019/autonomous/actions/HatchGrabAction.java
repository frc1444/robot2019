package com.first1444.frc.robot2019.autonomous.actions;

import com.first1444.frc.robot2019.actions.TimedAction;
import com.first1444.frc.robot2019.subsystems.HatchIntake;
import me.retrodaredevil.action.Action;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HatchGrabAction extends TimedAction {
	private final Supplier<HatchIntake> hatchIntakeSupplier;
	private HatchGrabAction(Supplier<HatchIntake> hatchIntakeSupplier, Consumer<HatchIntake> hatchIntakeAction) {
		super(true, 500);
		this.hatchIntakeSupplier = hatchIntakeSupplier;
	}
	public Action createGrab(Supplier<HatchIntake> hatchIntakeSupplier){
		return new HatchGrabAction(hatchIntakeSupplier, HatchIntake::hold);
	}
	public Action createDrop(Supplier<HatchIntake> hatchIntakeSupplier){
		return new HatchGrabAction(hatchIntakeSupplier, HatchIntake::drop);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		final HatchIntake hatchIntake = hatchIntakeSupplier.get();
		Objects.requireNonNull(hatchIntake);
		hatchIntake.hold();
	}
}
