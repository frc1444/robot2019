package com.first1444.frc.robot2019.autonomous;

public enum AutonomousType {
	DO_NOTHING("Do Nothing", false, false, false, false, false, false, false),

	CROSS_LINE_FORWARD("Cross Line"		 , false, false, false, false, false, false, false),
	CROSS_LINE_SIDE("Cross Line to the Side", true, true, false, false, false, false, false),

	CENTER_CARGO_SHIP("Center Cargo Ship (Goes at Angle)"		, true, true, true, true, true, false, false),
	OFF_CENTER_CARGO_SHIP("Off Center Cargo Ship (Goes straight)", false, false, true, true, true, false, false),

	SIDE_CARGO_SHIP("Side Cargo Ship", true, true, true, true, true, false, false),

	SIDE_ROCKET("Side Rocket", true, true, false, true, true, true, true);

	private final String name;
	private final boolean left, right;
	private final boolean cargo, hatch;
	private final boolean level1, level2, level3;

	AutonomousType(String name,
				   boolean left, boolean right,
				   boolean cargo, boolean hatch,
				   boolean level1, boolean level2, boolean level3) {
		this.name = name;
		this.left = left;
		this.right = right;
		this.cargo = cargo;
		this.hatch = hatch;
		this.level1 = level1;
		this.level2 = level2;
		this.level3 = level3;
	}


	public String getName(){
		return name;
	}

	public boolean isSupportsLeftSide(){
		return left;
	}
	public boolean isSupportsRightSide(){
		return right;
	}

	public boolean isSupportsCargo(){
		return cargo;
	}
	public boolean isSupportsHatch(){
		return hatch;
	}

	public boolean isSupportsLevel1(){
		return level1;
	}
	public boolean isSupportsLevel2(){
		return level2;
	}
	public boolean isSupportsLevel3(){
		return level3;
	}
}
