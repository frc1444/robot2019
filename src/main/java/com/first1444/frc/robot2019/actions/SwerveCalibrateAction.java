package com.first1444.frc.robot2019.actions;

import com.first1444.frc.robot2019.input.RobotInput;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveModule;
import me.retrodaredevil.action.SimpleAction;

import java.util.function.Supplier;

public class SwerveCalibrateAction extends SimpleAction {
	private final Supplier<SwerveDrive> driveSupplier;
	private final RobotInput input;
	public SwerveCalibrateAction(Supplier<SwerveDrive> driveSupplier, RobotInput input) {
		super(false);
		this.driveSupplier = driveSupplier;
		this.input = input;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		if(input.getSwerveQuickReverseCancel().isPressed()){
			for(SwerveModule module : driveSupplier.get().getModules()){
				module.setQuickReverseAllowed(false);
			}
		} else if(input.getSwerveQuickReverseCancel().isReleased()){
			for(SwerveModule module : driveSupplier.get().getModules()){
				module.setQuickReverseAllowed(true);
			}
		}
		if(input.getSwerveRecalibrate().isPressed()){
			for(SwerveModule module : driveSupplier.get().getModules()){
				module.recalibrate();
			}
		}
	}
}
