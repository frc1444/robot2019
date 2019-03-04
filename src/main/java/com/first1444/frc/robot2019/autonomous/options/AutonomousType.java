package com.first1444.frc.robot2019.autonomous.options;

import com.first1444.frc.robot2019.deepspace.GamePieceType;
import com.first1444.frc.robot2019.deepspace.SlotLevel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

import static java.util.Arrays.*;
import static java.util.Collections.*;

public enum AutonomousType {
	DO_NOTHING("Do Nothing", emptySet(),
			emptySet(),
			emptySet(),
			singleton(LineUpType.NO_VISION)),
	CROSS_LINE_FORWARD("Cross Line", emptySet(),
			emptySet(),
			emptySet(),
			singleton(LineUpType.NO_VISION)),
	CROSS_LINE_SIDE("Cross Line to the Side", asList(StartingPosition.LEFT, StartingPosition.RIGHT),
			emptySet(),
			emptySet(),
			singleton(LineUpType.NO_VISION)),
	
	OFF_CENTER_CARGO_SHIP("Off Center Cargo Ship (Goes straight)", asList(StartingPosition.MIDDLE_LEFT, StartingPosition.MIDDLE_RIGHT),
			asList(GamePieceType.HATCH, GamePieceType.CARGO),
			singleton(SlotLevel.LEVEL1),
			asList(LineUpType.NO_VISION, LineUpType.USE_VISION)),

	SIDE_CARGO_SHIP("Side Cargo Ship", asList(StartingPosition.LEFT, StartingPosition.RIGHT),
			asList(GamePieceType.HATCH, GamePieceType.CARGO),
			singleton(SlotLevel.LEVEL1),
			asList(LineUpType.NO_VISION, LineUpType.USE_VISION)),

	SIDE_ROCKET("Side Rocket", asList(StartingPosition.LEFT, StartingPosition.RIGHT),
			singleton(GamePieceType.HATCH),
			asList(SlotLevel.LEVEL1, SlotLevel.LEVEL2, SlotLevel.LEVEL3),
			asList(LineUpType.NO_VISION, LineUpType.USE_VISION));

	private final String name;
	private final Collection<StartingPosition> startingPositions;
	private final Collection<GamePieceType> gamePieces;
	private final Collection<SlotLevel> slotLevels;
	private final Collection<LineUpType> lineUpTypes;

	AutonomousType(String name,
				   Collection<StartingPosition> startingPositions,
				   Collection<GamePieceType> gamePieces,
				   Collection<SlotLevel> slotLevels,
				   Collection<LineUpType> lineUpTypes) {
		this.name = name;
		this.startingPositions = enumSetOf(startingPositions);
		this.gamePieces = enumSetOf(gamePieces);
		this.slotLevels = enumSetOf(slotLevels);
		if(lineUpTypes.isEmpty()) throw new IllegalArgumentException("The Collection lineUpTypes must not be empty!");
		this.lineUpTypes = enumSetOf(lineUpTypes);
	}
	private static <T extends Enum<T>> Collection<T> enumSetOf(Collection<T> collection){
		if(collection.isEmpty()){
			return Collections.emptySet();
		}
		return unmodifiableCollection(EnumSet.copyOf(collection));
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
	
	/**
	 *
	 * @return A non-empty Collection representing the valid ways to line up.
	 */
	public Collection<LineUpType> getLineUpTypes(){
		return lineUpTypes;
	}
}
