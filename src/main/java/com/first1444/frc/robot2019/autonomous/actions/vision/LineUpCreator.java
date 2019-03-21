package com.first1444.frc.robot2019.autonomous.actions.vision;

import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.autonomous.actions.DistanceAwayLinkedAction;
import com.first1444.frc.robot2019.event.EventSender;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.robot2019.vision.PreferredTargetSelector;
import com.first1444.frc.robot2019.vision.VisionPacketProvider;
import com.first1444.frc.robot2019.vision.VisionSupplier;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.WhenDone;

import java.util.function.Supplier;

public final class LineUpCreator {
	private LineUpCreator() { throw new UnsupportedOperationException(); }
	
	public static Action createLineUpAction(VisionPacketProvider packetProvider,
											Supplier<SwerveDrive> driveSupplier, Supplier<Orientation> orientationSupplier,
											Action failAction, Action successAction, EventSender eventSender){
		return Actions.createLinkedActionRunner(
				createLinkedLineUpAction(packetProvider, driveSupplier, orientationSupplier, failAction, successAction, eventSender),
				WhenDone.CLEAR_ACTIVE_AND_BE_DONE,
				true
		);
	}
	public static DistanceAwayLinkedAction createLinkedLineUpAction(VisionPacketProvider packetProvider,
																	Supplier<SwerveDrive> driveSupplier, Supplier<Orientation> orientationSupplier,
																	Action failAction, Action successAction, EventSender eventSender){
		return new StrafeLineUpAction(packetProvider, driveSupplier, orientationSupplier, failAction, successAction, eventSender);
	}
}
