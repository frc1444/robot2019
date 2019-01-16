package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.ShuffleboardMap;
import com.first1444.frc.util.DynamicSendableChooser;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.ActionQueue;
import me.retrodaredevil.action.Actions;

import java.util.Optional;

public class AutonomousChooserState {
	private final DynamicSendableChooser<AutonomousType> autonomousChooser;
	private final DynamicSendableChooser<StartingPosition> startingPositionChooser;
	private final DynamicSendableChooser<GamePieceType> gamePieceChooser;
	private final DynamicSendableChooser<Level> levelChooser;

	public AutonomousChooserState(ShuffleboardMap shuffleboardMap){
		final ShuffleboardLayout layout = shuffleboardMap.getUserTab()
				.getLayout("Autonomous", BuiltInLayouts.kList)
				.withSize(2, 4);
		autonomousChooser = new DynamicSendableChooser<>();
		startingPositionChooser = new DynamicSendableChooser<>();
		gamePieceChooser = new DynamicSendableChooser<>();
		levelChooser = new DynamicSendableChooser<>();

		addAutoOptions();
		updateStartingPositionChooser();
		updateGamePieceChooser();
		updateLevelChooser();

		layout.add("Autonomous Chooser", autonomousChooser);
		layout.add("Starting Position Chooser", startingPositionChooser);
		layout.add("Game Piece Chooser", gamePieceChooser);
		layout.add("Level Chooser", levelChooser);
		autonomousChooser.addListener(newSelectionKey -> {
			updateStartingPositionChooser();
			updateGamePieceChooser();
			updateLevelChooser();
		});
	}
	public Action createAutonomousAction(AutonActionCreator actionCreator){
		final AutonomousType type = autonomousChooser.getSelected();
		if(type == null){
			throw new NullPointerException("The autonomous type cannot be null!");
		}
		final StartingPosition startingPosition = Optional.ofNullable(startingPositionChooser.getSelected()).orElse(StartingPosition.NULL);
		final GamePieceType gamePiece = Optional.ofNullable(gamePieceChooser.getSelected()).orElse(GamePieceType.NULL);
		final Level level = Optional.ofNullable(levelChooser.getSelected()).orElse(Level.NULL);
		
		final ActionQueue actionQueue = new Actions.ActionQueueBuilder()
				.canRecycle(false)
				.canBeDone(true)
				.immediatelyDoNextWhenDone(true) // once an action is finished, do the next one immediately
				.build();
		
		switch (type){
			case DO_NOTHING:
				break;
			case CROSS_LINE_FORWARD:
				actionQueue.add(actionCreator.createGoStraight(55, .5, 90)); // cross line
				break;
			case CROSS_LINE_SIDE:
				if(startingPosition == StartingPosition.NULL){
					System.err.println("Starting position is StartingPosition.NULL! This isn't valid!");
					break;
				}
				final boolean isLeft = startingPosition == StartingPosition.LEFT;
				actionQueue.add(actionCreator.createGoStraight(65, .5, isLeft ? 180 : 0)); // go towards wall
				actionQueue.add(actionCreator.createGoStraight(55, .5, 90)); // cross line
				break;
			case CENTER_CARGO_SHIP:
				break;
			case OFF_CENTER_CARGO_SHIP:
				// go 100 inches
				actionQueue.add(actionCreator.createGoStraight(40, .3, 90));
				actionQueue.add(actionCreator.createGoStraight(30, .7, 90));
				actionQueue.add(actionCreator.createGoStraight(30, .3, 90));
				// went 100 inches
				
				actionQueue.add(actionCreator.createCargoShipPlaceHatch());
				actionQueue.add(actionCreator.createCargoShipPlaceCargo());
				break;
			case SIDE_CARGO_SHIP:
				
				break;
			case SIDE_ROCKET:
				
				break;
		}
		return actionQueue;
	}
	private void addAutoOptions(){
		autonomousChooser.setDefaultOption(AutonomousType.DO_NOTHING.getName(), AutonomousType.DO_NOTHING);
		for(AutonomousType type : AutonomousType.values()){
			if(type != AutonomousType.DO_NOTHING){
				autonomousChooser.addOption(type.getName(), type);
			}
		}
	}
	private void updateStartingPositionChooser(){
		startingPositionChooser.reset();
		final AutonomousType type = autonomousChooser.getSelected();
		boolean supportsEither = false;
		if(type.isSupportsLeftSide()){
			startingPositionChooser.setDefaultOption("Left", StartingPosition.LEFT);
			supportsEither = true;
		}
		if(type.isSupportsRightSide()){
			startingPositionChooser.setDefaultOption("Right", StartingPosition.RIGHT);
			supportsEither = true;
		}
		if(!supportsEither){
			startingPositionChooser.setDefaultOption("Neither", StartingPosition.NULL);
		}
	}
	private void updateGamePieceChooser(){
		gamePieceChooser.reset();
		final AutonomousType type = autonomousChooser.getSelected();
		boolean supportsEither = false;
		if(type.isSupportsCargo()){
			gamePieceChooser.setDefaultOption("Cargo", GamePieceType.CARGO);
			supportsEither = true;
		}
		if(type.isSupportsHatch()){
			gamePieceChooser.setDefaultOption("Hatch", GamePieceType.HATCH);
			supportsEither = true;
		}
		if(!supportsEither){
			gamePieceChooser.setDefaultOption("Neither", GamePieceType.NULL);
		}
	}
	private void updateLevelChooser(){
		levelChooser.reset();
		final AutonomousType type = autonomousChooser.getSelected();
		boolean supportsAny = false;
		if(type.isSupportsLevel1()){
			levelChooser.setDefaultOption("Level 1", Level.LEVEL1);
			supportsAny = true;
		}
		if(type.isSupportsLevel2()){
			levelChooser.setDefaultOption("Level 2", Level.LEVEL2);
			supportsAny = true;
		}
		if(type.isSupportsLevel3()){
			levelChooser.setDefaultOption("Level 3", Level.LEVEL3);
			supportsAny = true;
		}
		if(!supportsAny){
			levelChooser.setDefaultOption("None", Level.NULL);
		}
	}
	
	public enum StartingPosition {
		LEFT, RIGHT, NULL
	}
	public enum GamePieceType {
		HATCH, CARGO, NULL
	}
	public enum Level {
		LEVEL1, LEVEL2, LEVEL3, NULL
	}
}
