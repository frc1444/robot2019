package com.first1444.frc.robot2019.matchrun;

import me.retrodaredevil.action.Action;

public interface MatchScheduler {
	void schedule(Action action, MatchTime startTime);
}
