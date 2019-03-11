package com.first1444.frc.robot2019.autonomous.actions.vision;

import com.first1444.frc.robot2019.autonomous.actions.DistanceAwayLinkedAction;
import com.first1444.frc.robot2019.event.EventSender;
import com.first1444.frc.robot2019.event.SoundEvents;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.robot2019.vision.ImmutableVisionPacket;
import com.first1444.frc.robot2019.vision.VisionPacket;
import com.first1444.frc.robot2019.vision.VisionPacketProvider;
import com.first1444.frc.util.MathUtil;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.SimpleAction;

import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.Math.*;

public class StrafeLineUpAction extends SimpleAction implements DistanceAwayLinkedAction {
	public static final double MAX_SPEED = .3;
	private static final long FAIL_NOTIFY_TIME = 100;
	private static final long MAX_FAIL_TIME = 1000;
	
	private final VisionPacketProvider packetProvider;
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
	
	StrafeLineUpAction(VisionPacketProvider packetProvider,
						Supplier<SwerveDrive> driveSupplier, Supplier<Orientation> orientationSupplier,
						Action failAction, Action successAction, EventSender eventSender) {
		super(false);
		this.packetProvider = Objects.requireNonNull(packetProvider);
		this.driveSupplier = Objects.requireNonNull(driveSupplier);
		this.orientationSupplier = Objects.requireNonNull(orientationSupplier);
		
		this.successAction = successAction;
		this.eventSender = eventSender;
		
		nextAction = failAction;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		final VisionPacket vision = packetProvider.getPacket();
		final boolean failed;
		if(vision != null){
			if(!hasFound){
				if(eventSender != null) {
					eventSender.sendEvent(SoundEvents.TARGET_FOUND);
				}
				hasFound = true;
			}
			failed = false;
			usePacket(vision);
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
		updateVisionView(targetVision);
		
		final VisionPacket faceVision = new ImmutableVisionPacket(
				targetVision.getRobotX(),
				targetVision.getRobotY(),
				targetVision.getRobotZ() - 15, // make it further away
				targetVision.getVisionYaw(), targetVision.getVisionPitch(), targetVision.getVisionRoll(), targetVision.getImageX(), targetVision.getImageY()
		);
		final double yawTurnAmount = max(-1, min(1, targetVision.getVisionYaw() / -30)); // moveVision has same yaw so it doesn't matter // to make the yaw go to 0
		final double zeroGroundAngle = MathUtil.minChange(faceVision.getGroundAngle(), 90, 360); // we want this to get close to 0 // we want to face the target
		final double faceTurnAmount = max(-1, min(1, zeroGroundAngle / -90)); // to face the target
		final double turnAmount = faceTurnAmount * .8 + yawTurnAmount * .2;
		
		final double moveY = 1;
		final double moveX = min(5, targetVision.getRobotX() / -10);
		final double moveMagnitude = hypot(moveX, moveY);
		driveSupplier.get().setControl(moveX / moveMagnitude, moveY / moveMagnitude, turnAmount, MAX_SPEED, packetProvider.getPerspective());
		if(targetVision.getRobotZ() > -5){
			nextAction = successAction;
			System.out.println("strafe success!");
			setDone(true);
		}
	}
	private void useVisionView(VisionView visionView){
		Objects.requireNonNull(visionView);
		final double orientation = orientationSupplier.get().getOrientation();
		final double direction = visionView.getDirectionToTarget() - orientation; // forward is 90 degrees
		final double directionRadians = toRadians(direction);
		final double visionYaw = MathUtil.minChange(visionView.getTargetOrientation() - orientation, 0, 360); // perfect is 0 degrees
		final double distance = visionView.getDistanceToTarget();
		
		final double moveX = cos(directionRadians);
		final double moveY = sin(directionRadians);
		
		final double distanceLeft = distance - visionView.getTracker().calculateDistance();
		if(distanceLeft <= 3){
			System.out.println("Using vision view. We went the distance we needed to");
			setDone(true);
		} else {
			driveSupplier.get().setControl(moveX, moveY, .5 * max(-1, min(1, visionYaw / -30)), MAX_SPEED, packetProvider.getPerspective());
		}
		distanceAway = max(0, distanceLeft);
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
