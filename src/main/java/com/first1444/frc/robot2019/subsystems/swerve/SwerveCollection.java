package com.first1444.frc.robot2019.subsystems.swerve;

import me.retrodaredevil.action.Action;

import java.util.List;

public interface SwerveCollection extends Action {
	List<? extends SwerveModule> getModules();
}
