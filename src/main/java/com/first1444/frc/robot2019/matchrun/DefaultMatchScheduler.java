package com.first1444.frc.robot2019.matchrun;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.ActionMultiplexer;
import me.retrodaredevil.action.Actions;
import me.retrodaredevil.action.SimpleAction;

import java.util.*;


public class DefaultMatchScheduler extends SimpleAction implements MatchScheduler {
	private final Map<MatchTime, Set<Action>> timeMap = new HashMap<>();
	private final TimeGetter timeGetter;
	private final ActionMultiplexer multiplexer = new Actions.ActionMultiplexerBuilder().clearAllOnEnd(true).canBeDone(false).canRecycle(false).build();
	
	private Double modeStartTimestamp = null;
	private MatchTime.Mode mode = null;
	
	/**
	 *
	 * @param timeGetter The TimeGetter
	 */
	public DefaultMatchScheduler(TimeGetter timeGetter) {
		super(false);
		this.timeGetter = timeGetter;
	}
	public DefaultMatchScheduler(){
		this(DriverStationTimeGetter.INSTANCE);
	}
	
	@Override
	public void schedule(Action action, MatchTime startTime) {
		timeMap.computeIfAbsent(startTime, (key) -> new HashSet<>()).add(action);
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		MatchTime.Mode currentMode = timeGetter.getCurrentMode();
		if(currentMode != mode){
			mode = currentMode;
			modeStartTimestamp = timeGetter.getTimestamp();
		}
		if(modeStartTimestamp == null){ // basically, set the start timestamp for the null mode (usually disabled)
			mode = null;
			modeStartTimestamp = timeGetter.getTimestamp();
		}
		checkScheduling();
		updateActions();
	}
	private void checkScheduling(){
		Objects.requireNonNull(modeStartTimestamp, "The modeStartTimestamp cannot be null when this method is run!");
		final double modeStartTimestamp = this.modeStartTimestamp;
		Double timeRemaining = timeGetter.getRemainingTimeInPeriod(modeStartTimestamp);
		double timeTotal = timeGetter.getTimeInPeriod(modeStartTimestamp);
//		SmartDashboard.putString("remaining time", "" + timeRemaining);
//		SmartDashboard.putNumber("Match time", DriverStation.getInstance().getMatchTime());
		
		final MatchTime.Mode currentMode = timeGetter.getCurrentMode();
		
		for(final var it = timeMap.entrySet().iterator(); it.hasNext(); ){
			final var entry = it.next();
			final MatchTime matchTime = entry.getKey();
			if(currentMode == MatchTime.Mode.TELEOP && matchTime.getMode() != MatchTime.Mode.TELEOP){ // if it should have already been ran
				addActions(entry.getValue());
				it.remove();
			} else if(currentMode == matchTime.getMode()){
				switch (matchTime.getType()) {
					case FROM_END:
						if(timeRemaining != null && matchTime.getTime() >= timeRemaining) { // If the time has passed in the current mode
							addActions(entry.getValue());
							it.remove();
						}
						break;
					case AFTER_START:
						if(matchTime.getTime() <= timeTotal){
							addActions(entry.getValue());
							it.remove();
						}
						break;
				}
			}
		}
		updateActions();
	}
	private void addActions(Set<Action> actions){
		for(Action action : actions){
			multiplexer.add(action);
		}
	}
	private void updateActions(){
		multiplexer.update();
	}
	public interface TimeGetter {
		/** @return A timestamp that increases over time. In seconds.*/
		double getTimestamp();
		/** @return If known, returns the amount of time remaining in the current mode/period in seconds. Otherwise returns null.*/
		Double getRemainingTimeInPeriod(double modeStartTimestamp);
		/** @return The amount of time that's passed in the current mode/period in seconds*/
		double getTimeInPeriod(double modeStartTimestamp);
		
		boolean isAutonomous();
		boolean isTeleop();
		
		default MatchTime.Mode getCurrentMode(){
			if(isAutonomous()){
				return MatchTime.Mode.AUTONOMOUS;
			} else if(isTeleop()){
				return MatchTime.Mode.TELEOP;
			}
			return null;
		}
	}
	public enum DriverStationTimeGetter implements TimeGetter {
		INSTANCE;
		
		@Override
		public double getTimestamp() {
			return Timer.getFPGATimestamp();
		}
		
		@Override
		public Double getRemainingTimeInPeriod(double modeStartTimestamp) {
			final double r = DriverStation.getInstance().getMatchTime();
			if(r == -1){
				return null;
			}
			return r;
		}
		
		@Override
		public double getTimeInPeriod(double modeStartTimestamp) {
			if(DriverStation.getInstance().isFMSAttached()) { // if this is the real deal
				final Double remainingTime = getRemainingTimeInPeriod(modeStartTimestamp);
				if(remainingTime != null) {
					if (isAutonomous()) {
						return 15 - remainingTime;
					} else if (isTeleop()) {
						return 135 - remainingTime;
					}
					return 0.0;
				}
			}
			return getTimestamp() - modeStartTimestamp;
		}
		
		@Override
		public boolean isAutonomous() {
			return DriverStation.getInstance().isAutonomous();
		}
		
		@Override
		public boolean isTeleop() {
			return DriverStation.getInstance().isOperatorControl();
		}
		
	}
}
