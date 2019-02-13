package com.first1444.frc.robot2019.autonomous.options;

import com.first1444.frc.robot2019.deepspace.GamePieceType;
import com.first1444.frc.robot2019.deepspace.SlotLevel;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

public enum AutonomousType {
	DO_NOTHING("Do Nothing", EnumSet.noneOf(StartingPosition.class),
			EnumSet.noneOf(GamePieceType.class),
			EnumSet.noneOf(SlotLevel.class)),
	CROSS_LINE_FORWARD("Cross Line", EnumSet.noneOf(StartingPosition.class),
			EnumSet.noneOf(GamePieceType.class),
			EnumSet.noneOf(SlotLevel.class)),
	CROSS_LINE_SIDE("Cross Line to the Side", EnumSet.of(StartingPosition.LEFT, StartingPosition.RIGHT),
			EnumSet.noneOf(GamePieceType.class),
			EnumSet.noneOf(SlotLevel.class)),
	
	OFF_CENTER_CARGO_SHIP("Off Center Cargo Ship (Goes straight)", EnumSet.of(StartingPosition.MIDDLE_LEFT, StartingPosition.MIDDLE_RIGHT),
		EnumSet.of(GamePieceType.HATCH, GamePieceType.CARGO),
		EnumSet.of(SlotLevel.LEVEL1)),

	SIDE_CARGO_SHIP("Side Cargo Ship", EnumSet.of(StartingPosition.LEFT, StartingPosition.RIGHT),
			EnumSet.of(GamePieceType.HATCH, GamePieceType.CARGO),
			EnumSet.of(SlotLevel.LEVEL1, SlotLevel.LEVEL2, SlotLevel.LEVEL3)),

	SIDE_ROCKET("Side Rocket", EnumSet.of(StartingPosition.LEFT, StartingPosition.RIGHT),
			EnumSet.of(GamePieceType.HATCH),
			EnumSet.of(SlotLevel.LEVEL1, SlotLevel.LEVEL2, SlotLevel.LEVEL3));

	private final String name;
	private final Collection<StartingPosition> startingPositions;
	private final Collection<GamePieceType> gamePieces;
	private final Collection<SlotLevel> slotLevels;

	AutonomousType(String name,
				   Collection<StartingPosition> startingPositions,
				   Collection<GamePieceType> gamePieces,
				   Collection<SlotLevel> slotLevels) {
		this.name = name;
		this.startingPositions = Collections.unmodifiableCollection(startingPositions);
		this.gamePieces = gamePieces;
		this.slotLevels = Collections.unmodifiableCollection(slotLevels);
	}


	public String getName(){
		return name;
	}
	
	public Collection<StartingPosition> getStartingPositions(){
		return startingPositions;
	}
	
	public Collection<GamePieceType> getGamePieces(){
		return gamePieces;
	}

	public Collection<SlotLevel> getSlotLevels(){
		return slotLevels;
	}
}
