package com.first1444.frc.robot2019.autonomous.actions;

import com.first1444.frc.robot2019.subsystems.Lift;
import me.retrodaredevil.action.SimpleAction;

public class RaiseLift extends SimpleAction {
	private final Lift lift;
	private final Double position;
	public RaiseLift(Lift lift, Double position) {
		super(true);
		this.lift = lift;
		this.position = position;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		if(position == null){
			lift.setPositionCargoIntake();
		} else {
			lift.setDesiredPosition(position);
		}
		setDone(lift.isDesiredPositionReached());
	}
}
