package com.first1444.frc.robot2019.autonomous.actions;

import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.robot2019.vision.PacketListener;
import com.first1444.frc.robot2019.vision.PreferredTargetSelector;
import com.first1444.frc.robot2019.vision.VisionInstant;
import com.first1444.frc.robot2019.vision.VisionPacket;
import me.retrodaredevil.action.SimpleAction;

import java.util.Collection;
import java.util.function.Supplier;

import static java.lang.Math.*;

public class LineUpAction extends SimpleAction {
	private final PacketListener packetListener;
	private final Perspective perspective;
	private final PreferredTargetSelector selector;
	private final Supplier<SwerveDrive> driveSupplier;
	public LineUpAction(PacketListener packetListener, Perspective perspective, PreferredTargetSelector selector, Supplier<SwerveDrive> driveSupplier) {
		super(false);
		this.packetListener = packetListener;
		this.perspective = perspective;
		this.selector = selector;
		this.driveSupplier = driveSupplier;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		final VisionInstant visionInstant = packetListener.getInstant();
		if(visionInstant != null && visionInstant.getTimeMillis() + 750 >= System.currentTimeMillis()){ // not null and packet within .75 seconds
			final Collection<? extends VisionPacket> packets = visionInstant.getVisiblePackets();
			if(!packets.isEmpty()){
				final VisionPacket vision = selector.getPreferredTarget(packets);
				double visionX = vision.getX();
				double visionZ = vision.getZ();
				final double distance = hypot(visionX, visionZ);
				visionX /= distance;
				visionZ /= distance;
				
				final double turnAmount = max(-1, min(1, vision.getYaw() / -30));
				
				driveSupplier.get().setControl(visionX, visionZ, turnAmount, .3, perspective);
			}
		}
	}
}
