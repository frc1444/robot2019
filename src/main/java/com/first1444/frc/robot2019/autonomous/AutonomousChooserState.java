package com.first1444.frc.robot2019.autonomous;

import com.first1444.frc.robot2019.ShuffleboardMap;
import com.first1444.frc.util.DynamicSendableChooser;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public class AutonomousChooserState {
	private final ShuffleboardLayout layout;
	private final DynamicSendableChooser<AutonomousType> autonomousChooser;
	private final DynamicSendableChooser<StartingPosition> startingPositionChooser;

	public AutonomousChooserState(ShuffleboardMap shuffleboardMap){
		layout = shuffleboardMap.getUserTab().getLayout("Autonomous", BuiltInLayouts.kList);
		autonomousChooser = new DynamicSendableChooser<>();
		startingPositionChooser = new DynamicSendableChooser<>();

		addAutoOptions();
		updateStartingPositionChooser();

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

}
