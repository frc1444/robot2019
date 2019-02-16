package com.first1444.frc.robot2019.autonomous.actions.vision;

import com.first1444.frc.robot2019.Perspective;
import com.first1444.frc.robot2019.event.EventSender;
import com.first1444.frc.robot2019.event.SoundEvents;
import com.first1444.frc.robot2019.sensors.Orientation;
import com.first1444.frc.robot2019.subsystems.swerve.SwerveDrive;
import com.first1444.frc.robot2019.vision.*;
import com.first1444.frc.util.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.LinkedAction;
import me.retrodaredevil.action.SimpleAction;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;

import static java.lang.Math.*;

public class LineUpAction extends SimpleAction implements LinkedAction {
	public static final double MAX_SPEED = .3;
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
		
		final VisionPacket moveVision;
		final boolean usingTarget;
		if(MathUtil.minDistance(toDegrees(atan(targetVision.getRobotZ() / targetVision.getRobotX())), 90, 360) > 20){
			moveVision = new ImmutableVisionPacket(targetVision.getRobotX(), targetVision.getRobotY(), targetVision.getRobotZ() - 30, targetVision.getVisionYaw(), targetVision.getVisionPitch(), targetVision.getVisionRoll(), targetVision.getImageX(), targetVision.getImageY());
			usingTarget = false;
		} else {
			moveVision = targetVision;
			usingTarget = true;
		}
		updateVisionView(moveVision);
		
		final double moveX = moveVision.getVisionX() / moveVision.getGroundDistance();
		final double moveY = moveVision.getVisionZ() / moveVision.getGroundDistance();
		
		final double yawTurnAmount = max(-1, min(1, targetVision.getVisionYaw() / -30)); // moveVision has same yaw so it doesn't matter
		final double zeroGroundAngle = MathUtil.minChange(targetVision.getGroundAngle(), 90, 360); // we want this to get close to 0 // we want to face the target
		final double faceTurnAmount = max(-1, min(1, zeroGroundAngle / -90));
		SmartDashboard.putNumber("yawTurnAmount", yawTurnAmount);
		SmartDashboard.putNumber("faceTurnAmount", faceTurnAmount);
		
//		final double turnAmount = .5 * max(-1, min(1,
//				max(-1, min(1, vision.getVisionYaw() / -30))
//						+ .5 * vision.getImageX()
//		));
		final double turnAmount;
		if(!usingTarget){
			turnAmount = faceTurnAmount;
		} else {
			assert moveVision == targetVision : "always true";
			
			final double groundDistance = targetVision.getGroundDistance();
			if (groundDistance > 40) {
				turnAmount = faceTurnAmount;
			} else if (groundDistance > 20) {
				final double percentage = (groundDistance - 20) / 20.0;
				turnAmount = percentage * faceTurnAmount + (1 - percentage) * yawTurnAmount;
			} else {
				turnAmount = yawTurnAmount;
			}
		}
		// TODO change a few of these to moveVision and stuff
		
		
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
		final double direction = visionView.getDirectionToTarget() - orientation; // forward is 90 degrees
		final double directionRadians = toRadians(direction);
		final double visionYaw = MathUtil.minChange(visionView.getTargetOrientation() - orientation, 0, 360); // perfect is 0 degrees
		final double distance = visionView.getDistanceToTarget();
		
		final double moveX = cos(directionRadians);
		final double moveY = sin(directionRadians);
		
		driveSupplier.get().setControl(moveX, moveY, .5 * max(-1, min(1, visionYaw / -30)), MAX_SPEED, perspective);
		if(visionView.getTracker().calculateDistance() >= distance){
			setDone(true);
		}
	}
	
	@Override
	public Action getNextAction() {
		return nextAction;
	}
	
}
