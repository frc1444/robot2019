package com.first1444.frc.robot2019.actions;

import com.first1444.frc.robot2019.Robot;
import com.first1444.frc.robot2019.input.RobotInput;
import com.first1444.frc.robot2019.subsystems.*;
import me.retrodaredevil.action.SimpleAction;
import me.retrodaredevil.controller.input.InputPart;

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
		
		final TaskSystem taskSystem = robot.getTaskSystem();
		
		{ // lift
			final Lift lift = robot.getLift();
			if(isDefense || input.getLevel1Preset().isDown()){
				lift.setDesiredPosition(Lift.Position.LEVEL1);
			} else if(input.getLevel2Preset().isDown()){
				lift.setDesiredPosition(Lift.Position.LEVEL2);
			} else if(input.getLevel3Preset().isDown()){
				lift.setDesiredPosition(Lift.Position.LEVEL3);
			} else if(input.getCargoPickupPreset().isDown()){
				lift.setDesiredPosition(Lift.Position.LEVEL1);
				// TODO bring out cargo pivot here
				taskSystem.setCurrentTask(TaskSystem.Task.CARGO);
			} else if(input.getLevelCargoShipCargoPreset().isDown()){
				lift.setDesiredPosition(Lift.Position.CARGO_CARGO_SHIP);
				taskSystem.setCurrentTask(TaskSystem.Task.CARGO);
			} else {
				final InputPart speedInputPart = input.getLiftManualSpeed();
				if(speedInputPart.isDeadzone()){
					if(lift.getLiftMode() == Lift.LiftMode.SPEED) {
						lift.lockCurrentPosition();
					}
				} else {
					lift.setManualSpeed(speedInputPart.getPosition(), false);
				}
			}

		}
		{ // cargo intake
			final CargoIntake cargoIntake = robot.getCargoIntake();
			final double speed = input.getCargoIntakeSpeed().getZonedPosition();
			cargoIntake.setSpeed(speed);
			if(speed < 0){ // if we're intaking, set to cargo task
				taskSystem.setCurrentTask(TaskSystem.Task.CARGO);
			}
		}
		{ // hatch intake
			final HatchIntake hatchIntake = robot.getHatchIntake();
			
			if(isDefense || input.getHatchPivotStowedPreset().isDown()){
				hatchIntake.stowedPosition();
			} else if(input.getHatchPivotReadyPreset().isDown()){
				hatchIntake.readyPosition();
				taskSystem.setCurrentTask(TaskSystem.Task.HATCH);
			} else if(input.getHatchPivotGroundPreset().isDown()){
				hatchIntake.groundPosition();
				taskSystem.setCurrentTask(TaskSystem.Task.HATCH);
			}
			
			if(input.getHatchDrop().isDown()){
				hatchIntake.drop();
				taskSystem.setCurrentTask(TaskSystem.Task.HATCH);
			} else if(input.getHatchGrab().isDown()){
				hatchIntake.hold();
				taskSystem.setCurrentTask(TaskSystem.Task.HATCH);
			}
			
		}
		{
			final Climber climber = robot.getClimber();
			climber.setDriveSpeed(input.getClimbWheelSpeed().getZonedPosition());
			climber.setClimbSpeed(input.getClimbLiftSpeed().getZonedPosition());
		}
		
	}
}
