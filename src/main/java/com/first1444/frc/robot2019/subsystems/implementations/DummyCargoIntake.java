package com.first1444.frc.robot2019.subsystems.implementations;

import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.subsystems.CargoIntake;
import com.first1444.frc.util.reportmap.ReportMap;
import me.retrodaredevil.action.SimpleAction;

public class DummyCargoIntake extends SimpleAction implements CargoIntake {
	private static final String INTAKE_SPEED = "Cargo Intake Speed";
	private final ReportMap reportMap;
	
	public DummyCargoIntake(ReportMap reportMap) {
		super(true);
		this.reportMap = reportMap;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		reportMap.report(INTAKE_SPEED, "enabled");
	}
	
	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		reportMap.report(INTAKE_SPEED, "disabled");
	}
	
	@Override
	public void setSpeed(double speed) {
		reportMap.report(INTAKE_SPEED, Constants.DECIMAL_FORMAT.format(speed));
	}
}