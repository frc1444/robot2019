package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.Constants;
import com.first1444.frc.robot2019.ShuffleboardMap;
import com.first1444.frc.robot2019.autonomous.actions.WaitAction;
import com.first1444.frc.robot2019.autonomous.options.AutonomousType;
import com.first1444.frc.robot2019.autonomous.options.LineUpType;
import com.first1444.frc.robot2019.deepspace.GamePieceType;
import com.first1444.frc.robot2019.deepspace.SlotLevel;
import com.first1444.frc.robot2019.autonomous.options.StartingPosition;
import com.first1444.frc.robot2019.input.RobotInput;
import com.first1444.frc.util.DynamicSendableChooser;
import com.first1444.frc.util.valuemap.ValueMap;
import com.first1444.frc.util.valuemap.sendable.MutableValueMapSendable;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import me.retrodaredevil.action.Action;
import me.retrodaredevil.action.Actions;

import java.util.Collection;
import java.util.Map;

public class AutonomousChooserState {
	private final AutonomousModeCreator autonomousModeCreator;
	private final RobotInput robotInput;
	
	private final DynamicSendableChooser<AutonomousType> autonomousChooser;
	private final DynamicSendableChooser<StartingPosition> startingPositionChooser;
	private final DynamicSendableChooser<GamePieceType> gamePieceChooser;
	private final DynamicSendableChooser<SlotLevel> levelChooser;
	private final DynamicSendableChooser<LineUpType> lineUpChooser;
	private final ValueMap<AutonConfig> autonConfig;

	public AutonomousChooserState(ShuffleboardMap shuffleboardMap, AutonomousModeCreator autonomousModeCreator, RobotInput robotInput){
		this.autonomousModeCreator = autonomousModeCreator;
		this.robotInput = robotInput;
		final ShuffleboardLayout layout = shuffleboardMap.getUserTab()
				.getLayout("Autonomous", BuiltInLayouts.kList).withSize(2, 4);
		autonomousChooser = new DynamicSendableChooser<>();
		startingPositionChooser = new DynamicSendableChooser<>();
		gamePieceChooser = new DynamicSendableChooser<>();
		levelChooser = new DynamicSendableChooser<>();
		lineUpChooser = new DynamicSendableChooser<>();
		final var valueMapSendable = new MutableValueMapSendable<>(AutonConfig.class);
		layout.add("Config", valueMapSendable).withProperties(Constants.ROBOT_PREFERENCES_PROPERTIES).withSize(2, 5);
		autonConfig = valueMapSendable.getMutableValueMap();

		addAutoOptions();
		updateStartingPositionChooser();
		updateGamePieceChooser();
		updateLevelChooser();
		updateLineUpChooser();

		layout.add("Autonomous Chooser", autonomousChooser);
		layout.add("Starting Position Chooser", startingPositionChooser);
		layout.add("Game Piece Chooser", gamePieceChooser);
		layout.add("Level Chooser", levelChooser);
		layout.add("Line Up Chooser", lineUpChooser);
		autonomousChooser.addListener(newSelectionKey -> {
			updateStartingPositionChooser();
			updateGamePieceChooser();
			updateLevelChooser();
			updateLineUpChooser();
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
		final LineUpType lineUpType = lineUpChooser.getSelected();
		try {
			return new Actions.ActionQueueBuilder(
					new WaitAction(
							Math.round(autonConfig.getDouble(AutonConfig.WAIT_TIME) * 1000),
							() -> robotInput.getAutonomousWaitButton().isDown(),
							() -> robotInput.getAutonomousStartButton().isDown()
					),
					autonomousModeCreator.createAction(type, startingPosition, gamePiece, slotLevel, lineUpType, startingOrientation)
			).canRecycle(false).canBeDone(true).immediatelyDoNextWhenDone(true).build();
		} catch (IllegalArgumentException ex){
			ex.printStackTrace();
			System.out.println("One of our choosers must not have been set correctly!");
		}
		return Actions.createRunOnce(() -> System.out.println("This is the autonomous action because there was an exception when creating the one we wanted."));
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
		final Collection<StartingPosition> startingPositions = type.getStartingPositions();
		if(startingPositions.isEmpty()){
			startingPositionChooser.setDefaultOption("Neither", null);
		} else {
			for(StartingPosition position : startingPositions){
				startingPositionChooser.setDefaultOption(position.toString(), position);
			}
		}
	}
	private void updateGamePieceChooser(){
		gamePieceChooser.reset();
		final AutonomousType type = autonomousChooser.getSelected();
		final Collection<GamePieceType> gamePieces = type.getGamePieces();
		if(gamePieces.isEmpty()){
			gamePieceChooser.setDefaultOption("Neither", null);
		} else {
			for(GamePieceType gamePiece : gamePieces){
				gamePieceChooser.setDefaultOption(gamePiece.toString(), gamePiece);
			}
		}
	}
	private void updateLevelChooser(){
		levelChooser.reset();
		final AutonomousType type = autonomousChooser.getSelected();
		final Collection<SlotLevel> slotLevels = type.getSlotLevels();
		if(slotLevels.isEmpty()){
			levelChooser.setDefaultOption("None", null);
		} else {
			for(SlotLevel level : slotLevels){
				levelChooser.setDefaultOption(level.toString(), level);
			}
		}
	}
	private void updateLineUpChooser(){
		lineUpChooser.reset();
		final AutonomousType type = autonomousChooser.getSelected();
		final Collection<LineUpType> lineUpTypes = type.getLineUpTypes();
		if(lineUpTypes.isEmpty()){
			throw new AssertionError("lineUpTypes should never be empty!");
		}
		for(LineUpType lineUpType : lineUpTypes){
			lineUpChooser.setDefaultOption(lineUpType.toString(), lineUpType);
		}
		if(lineUpTypes.contains(LineUpType.NO_VISION)){
			lineUpChooser.setDefaultOption(LineUpType.NO_VISION.toString(), LineUpType.NO_VISION);
		}
	}
	
}
