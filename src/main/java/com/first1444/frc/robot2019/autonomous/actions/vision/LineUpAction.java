package com.first1444.frc.robot2019.autonomous.actions.vision;

import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.autonomous.actions.DistanceAwayAction;
import com.first1444.frc.robot2019.autonomous.actions.DistanceAwayLinkedAction;
import com.first1444.frc.robot2019.autonomous.actions.GoStraight;
import com.first1444.frc.robot2019.event.EventSender;
import com.first1444.frc.robot2019.event.SoundEvents;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.robot2019.vision.*;
import com.first1444.frc.util.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.LinkedAction;
import me.retrodaredevil.action.SimpleAction;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.Math.*;

class LineUpAction extends SimpleAction implements DistanceAwayLinkedAction {
	public static final double MAX_SPEED = .3;
	private static final long FAIL_NOTIFY_TIME = 100;
	private static final long MAX_FAIL_TIME = 1000;
	/** The number of inches in front of the vision to target when the angle is too big.*/
	private static final double TARGET_IN_FRONT_INCHES = 20;
	private static final double VISION_PACKET_X_MULT = 1.5;
	private static final double VISION_VIEW_X_MULT = 1.2;
	
	private final VisionSupplier visionSupplier;
	private final int cameraID;
	private final Perspective perspective;
	private final PreferredTargetSelector selector;
	private final Supplier<SwerveDrive> driveSupplier;
	private final Supplier<Orientation> orientationSupplier;
	
	private final Action successAction;
	private final EventSender eventSender;
	
	private VisionView visionView = null;
	/** Used for sending sounds. Set to true once we found it. Set to false when we lose it.*/
	private boolean hasFound = false;
	private Action nextAction;
	private Long failureStartTime = null;
	private Long lastFailSound = null;
	
	private double distanceAway = Double.MAX_VALUE;
	
	LineUpAction(VisionSupplier visionSupplier, int cameraID, Perspective perspective, PreferredTargetSelector selector,
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
		final VisionInstant visionInstant;
		try{
			visionInstant = visionSupplier.getInstant(cameraID);
		} catch(NoSuchElementException ex){
			ex.printStackTrace();
			System.out.println("No vision with cameraID of: " + cameraID);
			setDone(true);
			return;
		}
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
				usePacket(vision);
			} else {
				System.out.println("No visible packets!");
				failed = true;
			}
		} else {
			failed = true;
		}
		if(failed){
			if(visionView != null){
				useVisionView(visionView);
			}
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
				System.out.println("Failed vision. setDone(true) now");
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
	private void usePacket(final VisionPacket targetVision){
		Objects.requireNonNull(targetVision);
		
		distanceAway = targetVision.getGroundDistance();
		final VisionPacket moveVision = new ImmutableVisionPacket(
				targetVision.getRobotX(),
				targetVision.getRobotY(),
				targetVision.getRobotZ() + TARGET_IN_FRONT_INCHES,
				targetVision.getVisionYaw(), targetVision.getVisionPitch(), targetVision.getVisionRoll(), targetVision.getImageX(), targetVision.getImageY()
		);
		final VisionPacket faceVision = new ImmutableVisionPacket(
				targetVision.getRobotX(),
				targetVision.getRobotY(),
				targetVision.getRobotZ() - 15,
				targetVision.getVisionYaw(), targetVision.getVisionPitch(), targetVision.getVisionRoll(), targetVision.getImageX(), targetVision.getImageY()
		);
		updateVisionView(moveVision);
		
		final double moveX = moveVision.getVisionX() * VISION_PACKET_X_MULT;
		final double moveY = moveVision.getVisionZ();
		final double moveMagnitude = hypot(moveX, moveY);
		
		final double yawTurnAmount = max(-1, min(1, targetVision.getVisionYaw() / -30)); // moveVision has same yaw so it doesn't matter // to make the yaw go to 0
		final double zeroGroundAngle = MathUtil.minChange(faceVision.getGroundAngle(), 90, 360); // we want this to get close to 0 // we want to face the target
		final double faceTurnAmount = max(-1, min(1, zeroGroundAngle / -90)); // to face the target
//		SmartDashboard.putNumber("yawTurnAmount", yawTurnAmount);
//		SmartDashboard.putNumber("faceTurnAmount", faceTurnAmount);
		
//		final double turnAmount = .5 * max(-1, min(1,
//				max(-1, min(1, vision.getVisionYaw() / -30))
//						+ .5 * vision.getImageX()
//		));
		final double turnAmount;
		final double groundDistance = moveVision.getGroundDistance();
//		if (groundDistance > 40) {
//			turnAmount = faceTurnAmount;
//		} else if (groundDistance > 20) {
//			final double percentage = (groundDistance - 20) / 20.0;
//			turnAmount = percentage * faceTurnAmount + (1 - percentage) * yawTurnAmount;
//		} else {
//			turnAmount = yawTurnAmount;
//		}
		turnAmount = faceTurnAmount * .8 + yawTurnAmount * .2;
		
		
		driveSupplier.get().setControl(moveX / moveMagnitude, moveY / moveMagnitude, turnAmount, MAX_SPEED, perspective);
//		SmartDashboard.putNumber("ground distance", groundDistance);
		if(moveVision.getGroundDistance() < 5){
//			final double x = targetVision.getVisionX();
//			final double y = targetVision.getVisionZ();
//			final double magnitude = targetVision.getGroundDistance();
//			nextAction = Actions.createLinkedAction(
//					new GoStraight(hypot(x, y), .3, x / magnitude, y / magnitude, null, driveSupplier, orientationSupplier),
//					successAction
//			);
			nextAction = successAction;
			System.out.println("success!");
			setDone(true);
		}
//		SmartDashboard.putString("Vision Movement", "packet");
	}
	private void useVisionView(VisionView visionView){
		Objects.requireNonNull(visionView);
//		SmartDashboard.putString("Vision Movement", "view");
		final double orientation = orientationSupplier.get().getOrientation();
		final double direction = visionView.getDirectionToTarget() - orientation; // forward is 90 degrees
		final double directionRadians = toRadians(direction);
		final double visionYaw = MathUtil.minChange(visionView.getTargetOrientation() - orientation, 0, 360); // perfect is 0 degrees
		final double distance = visionView.getDistanceToTarget();
		
		final double moveX = cos(directionRadians) * VISION_VIEW_X_MULT;
		final double moveY = sin(directionRadians);
		final double moveMagnitude = hypot(moveX, moveY);
		
		final double distanceLeft = distance - visionView.getTracker().calculateDistance();
		if(distanceLeft <= 5){
			driveSupplier.get().setControl(moveX / moveMagnitude, moveY / moveMagnitude, 0, 0, perspective); // stay still, wait to detect target again
			System.out.println("Using vision view. We went the distance we needed to");
			setDone(true);
		} else {
			driveSupplier.get().setControl(moveX / moveMagnitude, moveY / moveMagnitude, .5 * max(-1, min(1, visionYaw / -30)), MAX_SPEED, perspective);
		}
		distanceAway = max(0, distanceLeft + TARGET_IN_FRONT_INCHES);
	}
	
	@Override
	public Action getNextAction() {
		return nextAction;
	}
	
	@Override
	public double getInchesAway() {
		return distanceAway;
	}
}
