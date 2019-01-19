package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.ShuffleboardMap;
import com.first1444.frc.robot2019.autonomous.options.AutonomousType;
import com.first1444.frc.robot2019.deepspace.GamePieceType;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
import com.first1444.frc.robot2019.autonomous.options.StartingPosition;
import com.first1444.frc.util.DynamicSendableChooser;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

public class AutonomousChooserState {
	private final AutonomousModeCreator autonomousModeCreator;
	
	private final DynamicSendableChooser<AutonomousType> autonomousChooser;
	private final DynamicSendableChooser<StartingPosition> startingPositionChooser;
	private final DynamicSendableChooser<GamePieceType> gamePieceChooser;
	private final DynamicSendableChooser<SlotLevel> levelChooser;

	public AutonomousChooserState(ShuffleboardMap shuffleboardMap, AutonomousModeCreator autonomousModeCreator){
		this.autonomousModeCreator = autonomousModeCreator;
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
	public Action createAutonomousAction(double startingOrientation){
		final AutonomousType type = autonomousChooser.getSelected();
		if(type == null){
			throw new NullPointerException("The autonomous type cannot be null!");
		}
		final StartingPosition startingPosition = startingPositionChooser.getSelected();
		final GamePieceType gamePiece = gamePieceChooser.getSelected();
		final SlotLevel slotLevel = levelChooser.getSelected();
		try {
			return autonomousModeCreator.createAction(type, startingPosition, gamePiece, slotLevel, startingOrientation);
		} catch (IllegalArgumentException ex){
			ex.printStackTrace();
			System.out.println("One of our choosers must not have been set correctly!");
		}
		return Actions.createRunOnce(() -> {});
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
			startingPositionChooser.setDefaultOption("Neither", null);
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
			gamePieceChooser.setDefaultOption("Neither", null);
		}
	}
	private void updateLevelChooser(){
		levelChooser.reset();
		final AutonomousType type = autonomousChooser.getSelected();
		boolean supportsAny = false;
		if(type.isSupportsLevel1()){
			levelChooser.setDefaultOption("Level 1", SlotLevel.LEVEL1);
			supportsAny = true;
		}
		if(type.isSupportsLevel2()){
			levelChooser.setDefaultOption("Level 2", SlotLevel.LEVEL2);
			supportsAny = true;
		}
		if(type.isSupportsLevel3()){
			levelChooser.setDefaultOption("Level 3", SlotLevel.LEVEL3);
			supportsAny = true;
		}
		if(!supportsAny){
			levelChooser.setDefaultOption("None", null);
		}
	}
	
}
