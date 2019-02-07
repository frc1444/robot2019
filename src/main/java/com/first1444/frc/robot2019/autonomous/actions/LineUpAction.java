package com.first1444.frc.robot2019.autonomous.actions;

import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.event.EventSender;
import com.first1444.frc.robot2019.event.SoundEvents;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.robot2019.vision.*;
import com.first1444.frc.util.MathUtil;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.LinkedAction;
import me.retrodaredevil.action.SimpleAction;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.Math.*;

public class LineUpAction extends SimpleAction implements LinkedAction {
	private static final double MAX_SPEED = .3;
	private static final long FAIL_NOTIFY_TIME = 100;
	private static final long MAX_FAIL_TIME = 500;
	
	private final VisionSupplier visionSupplier;
	private final int cameraID;
	private final Perspective perspective;
	private final PreferredTargetSelector selector;
	private final Supplier<SwerveDrive> driveSupplier;
	
	private final Action successAction;
	private final EventSender eventSender;
	
	private boolean hasFound = false;
	private Action nextAction;
	private Long failureStartTime = null;
	private Long lastFailSound = null;
	
	public LineUpAction(VisionSupplier visionSupplier, int cameraID, Perspective perspective, PreferredTargetSelector selector,
						Supplier<SwerveDrive> driveSupplier,
						Action failAction, Action successAction, EventSender eventSender) {
		super(false);
		this.visionSupplier = Objects.requireNonNull(visionSupplier);
		this.cameraID = cameraID;
		this.perspective = Objects.requireNonNull(perspective);
		this.selector = Objects.requireNonNull(selector);
		this.driveSupplier = Objects.requireNonNull(driveSupplier);
		
		this.successAction = successAction;
		this.eventSender = eventSender;
		
		nextAction = failAction;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		final VisionInstant visionInstant = visionSupplier.getInstant(cameraID);
		System.out.println("visionInstant: " + visionInstant);
		final boolean failed;
		if(visionInstant != null && visionInstant.getTimeMillis() + 750 >= System.currentTimeMillis()){ // not null and packet within .75 seconds
			final Collection<? extends VisionPacket> packets = visionInstant.getVisiblePackets();
			if(!packets.isEmpty()){
				if(!hasFound){
					if(eventSender != null) {
						eventSender.sendEvent(SoundEvents.TARGET_FOUND);
					}
					hasFound = true;
				}
				failed = false;
				final VisionPacket vision = selector.getPreferredTarget(packets);
				usePacket(vision);
			} else {
				System.out.println("No visible packets!");
				failed = true;
			}
		} else {
			failed = true;
		}
		if(failed){
			if(failureStartTime == null){
				failureStartTime = System.currentTimeMillis();
			}
			if(failureStartTime + FAIL_NOTIFY_TIME < System.currentTimeMillis() && (lastFailSound == null || lastFailSound + 1000 < System.currentTimeMillis())){
				if(eventSender != null) {
					eventSender.sendEvent(SoundEvents.TARGET_FAILED);
				}
				lastFailSound = System.currentTimeMillis();
			}
			if(failureStartTime + MAX_FAIL_TIME < System.currentTimeMillis()){ // half a second of failure
				setDone(true);
			}
		} else {
			failureStartTime = null;
		}
	}
	private void usePacket(VisionPacket vision){
		Objects.requireNonNull(vision);
		
		System.out.println("Using vision packet: " + vision);
		
		final double robotX = vision.getX();
		final double robotY = vision.getZ(); // assume negative
		final double yaw = vision.getYaw();
		System.out.println("yaw: " + Constants.DECIMAL_FORMAT.format(yaw));
		final double yawRadians = toRadians(yaw);
		
		final double rotationRadians = -yawRadians;
		final double sinRotation = sin(rotationRadians);
		final double cosRotation = cos(rotationRadians);
		
		// instead of a vector pointing backwards, we now have the direction we need to go to get to the target
		final double x = -(robotX * cosRotation - robotY * sinRotation);
		final double y = -(robotX * sinRotation + robotY * cosRotation);
		System.out.println("x: " + Constants.DECIMAL_FORMAT.format(x) + " y: " + Constants.DECIMAL_FORMAT.format(y));
		
		
		final double angle = toDegrees(atan2(y, x)); // assume in range [180..0]
		
		final double moveX = x * 1.15;
		final double moveY = y;
		final double moveMagnitude = hypot(moveX, moveY);
		
		System.out.println("Angle from robot to target: " + angle);
		
		final double turnAmount = max(-1, min(1,
				max(-1, min(1, vision.getYaw() / -30))
						+ 2 * MathUtil.conservePow(vision.getImageX(), 4)
		));
		
		
		driveSupplier.get().setControl(moveX / moveMagnitude, moveY / moveMagnitude, turnAmount, MAX_SPEED, perspective);
		if(moveY < 15){
			System.out.println("Move y is " + moveY);
			nextAction = successAction;
			setDone(true);
		}
	}
	
	@Override
	public Action getNextAction() {
		return nextAction;
	}
}
