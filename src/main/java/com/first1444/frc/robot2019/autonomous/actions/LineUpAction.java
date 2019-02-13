package com.first1444.frc.robot2019.autonomous.actions;

import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.event.EventSender;
import com.first1444.frc.robot2019.event.SoundEvents;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDistanceTracker;
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
	private final Supplier<Orientation> orientationSupplier;
	
	private final Action successAction;
	private final EventSender eventSender;
	
	private VisionView visionView = null;
	private boolean hasFound = false;
	private Action nextAction;
	private Long failureStartTime = null;
	private Long lastFailSound = null;
	
	public LineUpAction(VisionSupplier visionSupplier, int cameraID, Perspective perspective, PreferredTargetSelector selector,
						Supplier<SwerveDrive> driveSupplier, Supplier<Orientation> orientationSupplier,
						Action failAction, Action successAction, EventSender eventSender) {
		super(false);
		this.visionSupplier = Objects.requireNonNull(visionSupplier);
		this.cameraID = cameraID;
		this.perspective = Objects.requireNonNull(perspective);
		this.selector = Objects.requireNonNull(selector);
		this.driveSupplier = Objects.requireNonNull(driveSupplier);
		this.orientationSupplier = Objects.requireNonNull(orientationSupplier);
		
		this.successAction = successAction;
		this.eventSender = eventSender;
		
		nextAction = failAction;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		final VisionInstant visionInstant = visionSupplier.getInstant(cameraID);
//		System.out.println("visionInstant: " + visionInstant);
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
				updateVisionView(vision);
				usePacket(vision);
			} else {
				if(visionView != null){
					useVisionView(visionView);
				}
				System.out.println("No visible packets!");
				failed = true;
			}
		} else {
			if(visionView != null){
				useVisionView(visionView);
			}
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
			if(failureStartTime + MAX_FAIL_TIME < System.currentTimeMillis()){
				setDone(true);
			}
		} else {
			failureStartTime = null;
		}
	}
	private void updateVisionView(VisionPacket vision){
		final double orientation = orientationSupplier.get().getOrientation();
		final double visionYaw = vision.getVisionYaw() + orientation;
		
		final double direction = vision.getGroundAngle() + orientation;
		visionView = new VisionView(direction, vision.getGroundDistance(), visionYaw, driveSupplier.get());
	}
	private void usePacket(VisionPacket vision){
		Objects.requireNonNull(vision);
		
		final double moveX = vision.getVisionX() / vision.getGroundDistance();
		final double moveY = vision.getVisionZ() / vision.getGroundDistance();
		System.out.println("x: " + Constants.DECIMAL_FORMAT.format(moveX) + " z/y: " + Constants.DECIMAL_FORMAT.format(moveY));
		
		final double yawTurnAmount = max(-1, min(1, vision.getVisionYaw() / -30));
		final double faceTurnAmount = max(-1, min(1, MathUtil.minChange(vision.getGroundAngle(), 90, 360) / -30));
		
//		final double turnAmount = .5 * max(-1, min(1,
//				max(-1, min(1, vision.getVisionYaw() / -30))
//						+ .5 * vision.getImageX()
//		));
		// TODO this doesn't work, fix it
		final double turnAmount;
		if(vision.getGroundDistance() > 40){
			turnAmount = faceTurnAmount;
		} else if(vision.getGroundDistance() > 20){
			final double percentage = (vision.getGroundDistance() - 20) / 20.0;
			turnAmount = percentage * faceTurnAmount + (1 - percentage) * yawTurnAmount;
		} else {
			turnAmount = yawTurnAmount;
		}
		
		
		
		driveSupplier.get().setControl(moveX, moveY, turnAmount, MAX_SPEED, perspective);
		if(moveY < 15){
			System.out.println("Move y is " + moveY);
			nextAction = successAction;
			setDone(true);
		}
	}
	private void useVisionView(VisionView visionView){
		Objects.requireNonNull(visionView);
		final double orientation = orientationSupplier.get().getOrientation();
		final double direction = visionView.directionToTarget - orientation; // forward is 90 degrees
		final double directionRadians = toRadians(direction);
		final double visionYaw = MathUtil.minChange(visionView.targetOrientation - orientation, 0, 360); // perfect is 0 degrees
		final double distance = visionView.distanceToTarget;
		
		final double moveX = cos(directionRadians);
		final double moveY = sin(directionRadians);
		
		driveSupplier.get().setControl(moveX, moveY, .5 * max(-1, min(1, visionYaw / -30)), MAX_SPEED, perspective);
		if(visionView.tracker.calculateDistance() >= distance){
			setDone(true);
		}
	}
	
	@Override
	public Action getNextAction() {
		return nextAction;
	}
	
	private static class VisionView {
		/** The direction to the target in relation to the orientation. In degrees*/
		private final double directionToTarget;
		/** The distance to the target in inches. */
		private final double distanceToTarget;

		/** Orientation of the vision in relation to the orientation. In degrees*/
		private final double targetOrientation;
		
		private final SwerveDistanceTracker tracker;
		
		private VisionView(double directionToTarget, double distanceToTarget, double targetOrientation, SwerveDrive swerveDrive) {
			this.directionToTarget = directionToTarget;
			this.distanceToTarget = distanceToTarget;
			this.targetOrientation = targetOrientation;
			Objects.requireNonNull(swerveDrive);
			this.tracker = new SwerveDistanceTracker(swerveDrive);
		}
	}
}
