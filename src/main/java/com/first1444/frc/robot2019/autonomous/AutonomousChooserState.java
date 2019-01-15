package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.ShuffleboardMap;
import com.first1444.frc.util.DynamicSendableChooser;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class AutonomousChooserState {
	private final ShuffleboardLayout layout;
	private final SendableChooser<AutonomousType> autonomousChooser;
	private final DynamicSendableChooser<StartingPosition> startingPositionChooser;
	private final DynamicSendableChooser<GamePieceType> gamePieceChooser;
	private final DynamicSendableChooser<Level> levelChooser;

	public AutonomousChooserState(ShuffleboardMap shuffleboardMap){
		layout = shuffleboardMap.getUserTab().getLayout("Autonomous", BuiltInLayouts.kList);
		autonomousChooser = new SendableChooser<>();
		startingPositionChooser = new DynamicSendableChooser<>();
		gamePieceChooser = new DynamicSendableChooser<>();
		levelChooser = new DynamicSendableChooser<>();

		addAutoOptions();
		updateStartingPositionChooser();
		updateGamePieceChooser();
		updateLevelChooser();

		layout.add("Autonomous Chooser", autonomousChooser);
		layout.add("Starting Position Chooser", startingPositionChooser);
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
