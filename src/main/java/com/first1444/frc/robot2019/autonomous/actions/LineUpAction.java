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
	private static final double MAX_SPEED = .3;
	
	private final PacketListener packetListener;
	private final int cameraID;
	private final Perspective perspective;
	private final PreferredTargetSelector selector;
	private final Supplier<SwerveDrive> driveSupplier;
	public LineUpAction(PacketListener packetListener, int cameraID, Perspective perspective, PreferredTargetSelector selector, Supplier<SwerveDrive> driveSupplier) {
		super(false);
		this.packetListener = packetListener;
		this.cameraID = cameraID;
		this.perspective = perspective;
		this.selector = selector;
		this.driveSupplier = driveSupplier;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		final VisionInstant visionInstant = packetListener.getInstant(cameraID);
		if(visionInstant != null && visionInstant.getTimeMillis() + 750 >= System.currentTimeMillis()){ // not null and packet within .75 seconds
			final Collection<? extends VisionPacket> packets = visionInstant.getVisiblePackets();
			if(!packets.isEmpty()){
				final VisionPacket vision = selector.getPreferredTarget(packets);
				System.out.println("Using vision packet: " + vision);
				
				final double visionX = vision.getX();
				final double visionZ = vision.getZ();
				if(visionZ < 0){
					System.err.println("Vision Z is is negative!");
					return;
				}
				final double angle = toDegrees(atan2(visionZ, visionX)); // The angle to the target
				final double distance = hypot(visionX, visionZ);
				System.out.println("Angle from robot to target: " + angle);
				
				final double turnAmount;
				if(angle > 80 && angle < 100){
					final double turn = max(-1, min(1, vision.getYaw() / -30)); // a negative value turns it left
					if(turn < 0){ // we are turning left because the target is on the left
						if(vision.getImageX() > .5){
							turnAmount = 0;
						} else {
							turnAmount = turn;
						}
					} else {
						if(vision.getImageX() < -.5){
							turnAmount = 0;
						} else {
							turnAmount = turn;
						}
					}
					
				} else {
					 turnAmount = max(-1, min(1, vision.getImageX()));
				}
				double x = visionX / distance;
				double y = visionZ / distance;
				
				driveSupplier.get().setControl(x, y, turnAmount, MAX_SPEED, perspective);
			} else {
				System.out.println("No visible packets!");
			}
		} else {
			System.out.println("visionInstant: " + visionInstant);
		}
	}
}
