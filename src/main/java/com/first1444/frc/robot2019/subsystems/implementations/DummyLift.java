package com.first1444.frc.robot2019.subsystems.implementations;

import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.subsystems.Lift;
import com.first1444.frc.util.reportmap.ReportMap;
import me.retrodaredevil.action.SimpleAction;

import java.util.Objects;

public class DummyLift extends SimpleAction implements Lift {
	private static final String LIFT = "Lift";
	private final ReportMap reportMap;
	
	private LiftMode liftMode = LiftMode.SPEED;
	
	public DummyLift(ReportMap reportMap) {
		super(true);
		this.reportMap = reportMap;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		reportMap.report(LIFT, "robot enabled");
	}
	
	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		reportMap.report(LIFT, "robot disabled");
	}
	
	@Override
	public void setDesiredPosition(double desiredPosition) {
		reportMap.report(LIFT, "position " + Constants.DECIMAL_FORMAT.format(desiredPosition));
		liftMode = LiftMode.POSITION;
	}
	
	@Override
	public void setDesiredPosition(Position desiredPosition) {
		Objects.requireNonNull(desiredPosition);
		reportMap.report(LIFT, "position " + desiredPosition.toString());
		liftMode = LiftMode.POSITION;
	}
	
	@Override
	public void setManualSpeed(double speed, boolean overrideSpeedSafety) {
		reportMap.report(LIFT, "speed " + Constants.DECIMAL_FORMAT.format(speed) + " override: " + overrideSpeedSafety);
		liftMode = LiftMode.SPEED;
	}
	
	@Override
	public void lockCurrentPosition() {
		reportMap.report(LIFT, "position locked");
		liftMode = LiftMode.POSITION;
	}
	
	@Override
	public LiftMode getLiftMode() {
		return liftMode;
	}
	
	@Override
	public boolean isDesiredPositionReached() {
		return true;
	}
}
