package com.first1444.frc.robot2019.autonomous.actions;

import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.robot2019.vision.PacketListener;
import com.first1444.frc.robot2019.vision.PreferredTargetSelector;
import com.first1444.frc.robot2019.vision.VisionInstant;
import com.first1444.frc.robot2019.vision.VisionPacket;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.LinkedAction;
import me.retrodaredevil.action.SimpleAction;

import java.util.Collection;
import java.util.function.Supplier;

import static java.lang.Math.*;

public class LineUpAction extends SimpleAction implements LinkedAction {
	private static final double MAX_SPEED = .3;
	
	private final PacketListener packetListener;
	private final int cameraID;
	private final Perspective perspective;
	private final PreferredTargetSelector selector;
	private final Supplier<SwerveDrive> driveSupplier;
	
	private final Action successAction;
	
	private Action nextAction;
	private Long failureStartTime = null;
	
	public LineUpAction(PacketListener packetListener, int cameraID, Perspective perspective, PreferredTargetSelector selector,
						Supplier<SwerveDrive> driveSupplier,
						Action failAction, Action successAction) {
		super(false);
		this.packetListener = packetListener;
		this.cameraID = cameraID;
		this.perspective = perspective;
		this.selector = selector;
		this.driveSupplier = driveSupplier;
		
		this.successAction = successAction;
		nextAction = failAction;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		final VisionInstant visionInstant = packetListener.getInstant(cameraID);
		final boolean failed;
		if(visionInstant != null && visionInstant.getTimeMillis() + 750 >= System.currentTimeMillis()){ // not null and packet within .75 seconds
			final Collection<? extends VisionPacket> packets = visionInstant.getVisiblePackets();
			if(!packets.isEmpty()){
				failed = false;
				final VisionPacket vision = selector.getPreferredTarget(packets);
				System.out.println("Using vision packet: " + vision);
				
				final double robotX = vision.getX();
				final double robotY = vision.getZ(); // assume negative
				final double yaw = vision.getYaw();
				final double yawRadians = toRadians(yaw);
				
				final double rotationRadians = -yawRadians;
				final double sinRotation = sin(rotationRadians);
				final double cosRotation = cos(rotationRadians);
				
				// instead of a vector pointing backwards, we now have the direction we need to go to get to the target
				final double x = -(robotX * cosRotation - robotY * sinRotation);
				final double y = -(robotX * sinRotation + robotY * cosRotation);
				
				
				final double angle = toDegrees(atan2(y, x)); // assume in range [180..0]
				
				final double moveX = x * 1.15;
				final double moveY = y;
				final double moveMagnitude = hypot(moveX, moveY);
				
				System.out.println("Angle from robot to target: " + angle);
				
				final double turnAmount = max(-1, min(1,
						max(-1, min(1, vision.getYaw() / -30))
								+ .5 * vision.getImageX()
				));
				
				
				driveSupplier.get().setControl(moveX / moveMagnitude, moveY / moveMagnitude, turnAmount, MAX_SPEED, perspective);
				if(moveY < 15){
					nextAction = successAction;
					setDone(true);
				}
			} else {
				System.out.println("No visible packets!");
				failed = true;
			}
		} else {
			System.out.println("visionInstant: " + visionInstant);
			failed = true;
		}
		if(failed){
			if(failureStartTime == null){
				failureStartTime = System.currentTimeMillis();
			}
			if(failureStartTime + 500 < System.currentTimeMillis()){ // half a second of failure
				setDone(true);
			}
		} else {
			failureStartTime = null;
		}
	}
	
	@Override
	public Action getNextAction() {
		return nextAction;
	}
}
