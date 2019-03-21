package com.first1444.frc.robot2019.subsystems.swerve;

import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.SimpleAction;

import java.util.Arrays;
import java.util.List;

public class ImmutableActionFourSwerveCollection extends SimpleAction implements FourSwerveCollection {
	private final SwerveModule frontLeft, frontRight, rearLeft, rearRight;
	private final List<SwerveModule> modules;
	private final Action updateAction;

	public <T extends SwerveModule & Action> ImmutableActionFourSwerveCollection(T frontLeft, T frontRight,
																				 T rearLeft, T rearRight) {
		super(true);
		this.frontLeft = frontLeft;
		this.frontRight = frontRight;
		this.rearLeft = rearLeft;
		this.rearRight = rearRight;
		this.modules = Arrays.asList(frontLeft, frontRight, rearLeft, rearRight);
		this.updateAction = new Actions.ActionMultiplexerBuilder(
				frontLeft, frontRight, rearLeft, rearRight
		)
				.canRecycle(true)
				.build();
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		if(updateAction != null){
			updateAction.update();
		}
	}

	@Override
	protected void onEnd(boolean peacefullyEnded) {
		super.onEnd(peacefullyEnded);
		if(updateAction != null){
			updateAction.end();
		}
	}

	@Override
	public SwerveModule getFrontLeft() {
		return frontLeft;
	}

	@Override
	public SwerveModule getFrontRight() {
		return frontRight;
	}

	@Override
	public SwerveModule getRearLeft() {
		return rearLeft;
	}

	@Override
	public SwerveModule getRearRight() {
		return rearRight;
	}

	@Override
	public List<? extends SwerveModule> getModules() {
		return modules;
	}
}
