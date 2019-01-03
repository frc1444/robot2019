package com.first1444.frc.util;

import com.first1444.frc.util.pid.PidKey;
import com.first1444.frc.util.valuemap.ValueMap;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

@Deprecated
public class PidSendable extends SendableBase {

    private final MutablePid pid = new MutablePid();

	public PidSendable(){
		super(false);
	}
	public PidSendable(Pid pid){
		this();
		this.pid.set(pid);
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.addDoubleProperty("p", pid::getP, pid::setP);
		builder.addDoubleProperty("i", pid::getI, pid::setI);
		builder.addDoubleProperty("d", pid::getD, pid::setD);

		builder.addDoubleProperty("f", pid::getF, pid::setF);
		builder.addDoubleProperty("closed ramp rate", pid::getClosedRampRate, pid::setClosedRampRate);
	}

	/**
	 *
	 * @return An immutable {@link Pid} with the same state as the current state from {@link #getMutablePid()}
	 */
	public Pid getImmutablePid(){
		return pid.build();
	}

	/**
	 * @return A {@link MutablePid} that you can view or alter. If altered, changes will be reflected on the dashboard
	 */
	public MutablePid getMutablePid(){
		return pid;
	}
}
