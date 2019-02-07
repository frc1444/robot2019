package com.first1444.frc.robot2019.actions;

import com.first1444.frc.robot2019.Robot;
import com.first1444.frc.robot2019.input.RobotInput;
import com.first1444.frc.robot2019.subsystems.CargoIntake;
import com.first1444.frc.robot2019.subsystems.HatchIntake;
import com.first1444.frc.robot2019.subsystems.Lift;
import me.retrodaredevil.action.SimpleAction;

public class OperatorAction extends SimpleAction {
	private final Robot robot;
	private final RobotInput input;
	public OperatorAction(Robot robot, RobotInput input) {
		super(true);
		this.robot = robot;
		this.input = input;
	}
	
	@Override
	protected void onUpdate() {
		super.onUpdate();
		final boolean isDefense = input.getDefenseButton().isDown();
		
		{ // lift
			final Lift lift = robot.getLift();
			if(isDefense || input.getLevel1Preset().isDown()){
				lift.setDesiredPosition(Lift.Position.LEVEL1);
			} else if(input.getLevel2Preset().isDown()){
				lift.setDesiredPosition(Lift.Position.LEVEL2);
			} else if(input.getLevel3Preset().isDown()){
				lift.setDesiredPosition(Lift.Position.LEVEL3);
			} else if(input.getCargoPickupPreset().isDown()){
				lift.setPositionCargoIntake();
			} else if(input.getLevelCargoShipCargoPreset().isDown()){
				lift.setDesiredPosition(Lift.Position.CARGO_CARGO_SHIP);
			} else {
				lift.setManualSpeed(input.getLiftManualSpeed().getZonedPosition());
			}

		}
		{ // cargo intake
			final CargoIntake cargoIntake = robot.getCargoIntake();
			cargoIntake.setSpeed(input.getCargoIntakeSpeed().getZonedPosition());
		}
		{ // hatch intake
			final HatchIntake hatchIntake = robot.getHatchIntake();
			
			if(isDefense || input.getHatchPivotStowedPreset().isDown()){
				hatchIntake.stowedPosition();
			} else if(input.getHatchPivotReadyPreset().isDown()){
				hatchIntake.readyPosition();
			} else if(input.getHatchPivotGroundPreset().isDown()){
				hatchIntake.groundPosition();
			} else {
				// TODO Manual control
			}
			
		}
		
	}
}
