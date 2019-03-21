package com.first1444.frc.robot2019.matchrun;

import me.retrodaredevil.action.Actions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class MatchSchedulerTest {
	@Test
	void testMatchScheduler(){
		final var timeGetter = new DummyTimeGetter();
		timeGetter.mode = MatchTime.Mode.AUTONOMOUS;
		
		int[] value = {0};
		final DefaultMatchScheduler scheduler = new DefaultMatchScheduler(timeGetter);
		scheduler.schedule(Actions.createRunOnce(() -> value[0]++), MatchTime.of(1, MatchTime.Mode.AUTONOMOUS, MatchTime.Type.AFTER_START));
		
		timeGetter.timestamp = 0;
		scheduler.update();
		assertEquals(0, value[0]);
		
		timeGetter.timestamp = .5;
		scheduler.update();
		assertEquals(0, value[0]);
		
		timeGetter.timestamp = 1;
		scheduler.update();
		assertEquals(1, value[0]);
		
		scheduler.update();
		assertEquals(1, value[0]);
		
		scheduler.schedule(Actions.createRunOnce(() -> value[0]++), MatchTime.of(1, MatchTime.Mode.AUTONOMOUS, MatchTime.Type.FROM_END));
		scheduler.update();
		assertEquals(1, value[0]);
		
		timeGetter.timestamp = 13;
		scheduler.update();
		assertEquals(1, value[0]);
		
		timeGetter.timestamp = 14;
		scheduler.update();
		assertEquals(2, value[0]);
		
		timeGetter.mode = null;
		scheduler.update();
		
		timeGetter.mode = MatchTime.Mode.TELEOP;
		timeGetter.timestamp = 1000;
		scheduler.schedule(Actions.createRunOnce(() -> value[0]++), MatchTime.of(5, MatchTime.Mode.TELEOP, MatchTime.Type.FROM_END));
		scheduler.update();
		assertEquals(2, value[0]);
		
		timeGetter.timestamp = 1129;
		scheduler.update();
		assertEquals(2, value[0]);
		
		timeGetter.timestamp = 1130;
		scheduler.update();
		assertEquals(3, value[0]);
	}
	
	@Test
	void testTeleopSchedulingInAuto(){
		final var timeGetter = new DummyTimeGetter();
		timeGetter.mode = MatchTime.Mode.AUTONOMOUS;
		
		int[] value = {0};
		final DefaultMatchScheduler scheduler = new DefaultMatchScheduler(timeGetter);
		scheduler.schedule(Actions.createRunOnce(() -> value[0]++), MatchTime.of(1, MatchTime.Mode.TELEOP, MatchTime.Type.AFTER_START)); // TODO make this from end
		
		timeGetter.timestamp = 0;
		scheduler.update();
		assertEquals(0, value[0]);
		
		timeGetter.timestamp = .5;
		scheduler.update();
		assertEquals(0, value[0]);
		
		timeGetter.timestamp = 1;
		scheduler.update();
		assertEquals(0, value[0]);
		
	}
	
	class DummyTimeGetter implements DefaultMatchScheduler.TimeGetter {
		private MatchTime.Mode mode = null;
		private double timestamp = 0;
		
		@Override
		public double getTimestamp() {
			return timestamp;
		}
		
		@Override
		public Double getRemainingTimeInPeriod(double modeStartTimestamp) {
			final MatchTime.Mode mode = this.mode;
			if(mode == null){
				return null;
			}
			switch(mode){
				case TELEOP:
					return modeStartTimestamp + 135 - timestamp;
				case AUTONOMOUS:
					return modeStartTimestamp + 15 - timestamp;
				default: throw new UnsupportedOperationException("Unsupported mode: " + mode);
			}
		}
		
		@Override
		public double getTimeInPeriod(double modeStartTimestamp) {
			return timestamp - modeStartTimestamp;
		}
		
		@Override
		public boolean isAutonomous() {
			return mode == MatchTime.Mode.AUTONOMOUS;
		}
		
		@Override
		public boolean isTeleop() {
			return mode == MatchTime.Mode.TELEOP;
		}
		
		@Override
		public MatchTime.Mode getCurrentMode() {
			return mode;
		}
	}
}
