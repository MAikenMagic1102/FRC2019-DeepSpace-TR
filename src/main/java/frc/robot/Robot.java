/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Spark;
import frc.libs.REVAnalogPressureSensor;
import frc.libs.RazerController;

import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.GamePiece;
import frc.robot.subsystems.Elevator.Position;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.Arm.AGamePiece;
import frc.robot.subsystems.Arm.APosition;
import frc.robot.subsystems.Limelight.LightMode;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  double flipdrive = 1;
  boolean drivemode = true;
  boolean hatch = false;
  boolean game_piece;
  boolean flip_arm;
  boolean climber_deployed = false;
  boolean climber_retracting = false;
  boolean start_procedure = false;
  boolean start_procedure_full = false;

  double leftY, leftX, rightX;

  private final SendableChooser<String> m_chooser = new SendableChooser<>();


  RazerController driver = new RazerController(0);
  RazerController operator = new RazerController(1);

  PowerDistributionPanel PDP = new PowerDistributionPanel(0);
  REVAnalogPressureSensor Pressure = new REVAnalogPressureSensor(0);

  UsbCamera camera1;
  //UsbCamera camera2;
  Limelight limelight = new Limelight();

  Elevator elevator = new Elevator();
  Drivetrain drivetrain = new Drivetrain();
  Intake intake = new Intake();
  Arm arm = new Arm();
  Climber climber = new Climber();

  Spark climberwheels = new Spark(0);

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    //limelight.setCameraMode(CameraMode.eDriver);
    limelight.setLedMode(LightMode.eOff);
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto modes", m_chooser);
    updateDashboard();

    camera1 = CameraServer.getInstance().startAutomaticCapture(0);
    camera1.setExposureManual(55);
    camera1.setWhiteBalanceManual(55);
    camera1.setFPS(30);
  }

  

  public void updateDashboard(){
    try{
      new Thread(() -> {
        while(true){
            //SmartDashboard.putNumber("Left Motor Encoder", elevator.getLeftMotorPosition());
            //SmartDashboard.putNumber("Right Motor Encoder", elevator.getRightMotorPosition());
          SmartDashboard.putNumber("LeftY", driver.leftthumby());
          SmartDashboard.putNumber("RightX", driver.rightthumbx());
          SmartDashboard.putNumber("Triggers", driver.triggers());

          SmartDashboard.putBoolean("Cargo", game_piece);
          SmartDashboard.putBoolean("Hatch", !game_piece);
          SmartDashboard.putBoolean("Drive flipped?", (flipdrive == -1));
          SmartDashboard.putBoolean("Mecanum Enabled?", drivemode);
          SmartDashboard.putBoolean("Shift Solenoid State: ", drivetrain.getShiftSolenoidState());

          SmartDashboard.putNumber("Pressure: ", Pressure.getPressure());

          SmartDashboard.putNumber("Arm Current", arm.getMotorOutputCurrent());
          SmartDashboard.putNumber("Relative Arm Position", arm.getArmSensorPosition());
          SmartDashboard.putString("Arm Mode", arm.getAcurrentPositionMode() + " " + arm.getAcurrentMode());
          SmartDashboard.putNumber("Arm Hold Position: ", arm.getHoldPosition());
          SmartDashboard.putNumber("Arm Error: ", arm.getClosedLoopError());

          SmartDashboard.putNumber("Elevator Output Current: ", elevator.getMotorOutputCurrent());
          SmartDashboard.putNumber("Elevator Sensor Position: ", elevator.getElevatorSensorPosition());
          SmartDashboard.putString("Elevator Current Mode: ", elevator.getCurrentMode());
          SmartDashboard.putNumber("Elevator Hold Position: ", elevator.getHoldPosition());
          SmartDashboard.putNumber("Elevator Error: ", elevator.getError());
          SmartDashboard.putBoolean("Elevator Profile Finished", elevator.isProfileFinished());
          SmartDashboard.putNumber("Elevator Target Position", elevator.targetPosition);

          SmartDashboard.putBoolean("Arm Flipped", flip_arm);
          SmartDashboard.putNumber("POV Value", driver.getPOV());

          SmartDashboard.putBoolean("Target Aquired?", limelight.isTarget());
        }
      }).start();
    }catch(Exception e){
    }
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    //m_autoSelected = m_chooser.getSelected();
    // autoSelected = SmartDashboard.getString("Auto Selector",
    // defaultAuto);
    //System.out.println("Auto selected: " + m_autoSelected);
    elevator.zeroElevator();
    arm.zeroArm();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    if(!start_procedure_full){
    try{
      new Thread(() -> {
        boolean complete = false;
        boolean run_once = false;
        while(!complete){
          if(!run_once){
            elevator.setGamePiece(GamePiece.HATCH);
            elevator.setPosition(Position.HOME);
            start_procedure = true;
          }
          if(elevator.getElevatorSensorPosition() > 4.4){
            intake.hatch_clamp();
            complete = true;
            start_procedure_full = true;
            return;
          }
        }
        //thread kill
        return;
      }).start();
      }catch(Exception e) {}
    }

    if(start_procedure_full){
      teleopPeriodic();
    }  


    // switch (m_autoSelected) {
    //   case kCustomAuto:
    //     // Put custom auto code here
    //     break;
    //   case kDefaultAuto:
    //   default:
    //     // Put default auto code here
    //     break;
    // }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    arm.update_brake(climber_deployed);

    leftY = driver.leftthumby();
    leftX = driver.leftthumbx();
    rightX = driver.rightthumbx();

    if(driver.abutton_pressed()){
      //flipdrive = flipdrive * -1;
    }

    if(driver.bbutton_pressed()){
      drivemode = !drivemode;
    }

    if(driver.rtbutton_pressed()){
      hatch = !hatch;
      if(hatch){
        intake.stinger_cycle();
      }
    }

    if(operator.xbutton_pressed()){
      game_piece = !game_piece;
    }

    if(game_piece){
      arm.setGamePiece(AGamePiece.CARGO);
      elevator.setGamePiece(GamePiece.CARGO);
    }else{
      arm.setGamePiece(AGamePiece.HATCH);
      elevator.setGamePiece(GamePiece.HATCH);
    }
    
    //If drivemode is false arcade enabled, if drivemode is true....mecanum
    if(!drivemode){
      drivetrain.arcade_drive_openloop(flipdrive, leftY, rightX);
      limelight.setLedMode(LightMode.eOff);
    }else{
      if(driver.xbutton()){
        limelight.setLedMode(LightMode.eOn);
        if(limelight.isTarget()){
          drivetrain.limelight_mecdrive(flipdrive, limelight.getTx(), leftX, leftY);
        }else{
          drivetrain.mecanum_drive_openloop(flipdrive, leftX, rightX, leftY);
        }
      }else{
        drivetrain.mecanum_drive_openloop(flipdrive, leftX, rightX, leftY);
        limelight.setLedMode(LightMode.eOff);
      }
    }


    climberwheels.set(leftY * -1);

    if(driver.triggers() > 0.2){
      intake.set(1.0);
    }else{
      if(driver.triggers() < -0.2){
        intake.set(-1.0);
      }else{
        intake.set(0.2);
      }
    }
    intake.set(driver.triggers());

    if(driver.startbuttonPressed()){
      climber_deployed = true;
      climber.climb_cylinder_forward();
      drivemode = false;
      climber_deployed = true;
      climber_retracting = false;
    }

    if(driver.backbutton() && climber_deployed){
      climber.climb_cylinder_reverse();
      climber_retracting = true;
    }else{
      if(climber_retracting){
        climber.climb_cylinder_off();
      }
      if(!climber_deployed){
        climber.climb_cylinder_reverse();
      }
    }

    if(hatch){
      intake.hatch_release();
    }else{
      intake.hatch_clamp();
    }

    if(operator.leftthumby() > 0.2 || operator.leftthumby() < -0.2){
      elevator.set(operator.leftthumby() * -0.8);
    }else{

      if(operator.getPOV() == 0){
        elevator.set(0.45);
        //climber_deployed = true;
      }else{
        if(operator.getPOV() == 180){
          elevator.set(-0.45);
          //climber_deployed = true;
        }else{



      if(operator.rtbutton_pressed()){
        //if(!flip_arm)
          elevator.setPosition(Position.LOAD);
          arm.setPosition(APosition.LOAD);

        //if(!flip_arm && arm.getArmSensorPosition() < 10){

          //  if(arm.getArmSensorPosition() < -1005){
          //     try{
          //     new Thread(() -> {
          //      boolean complete = false;
          //      boolean run_once = false;
          //      while(!complete){
          //          if(!run_once){
          //              arm.setPosition(APosition.HOME);
          //              run_once = true;
          //          }
          //          else{
          //           complete = true;
          //           arm.setPosition(APosition.LOAD);
          //          }
                   
          //      }
          //      //thread kill
          //        return;                   
          //      }).start();
          //      }catch(Exception e) {}
          //  }else{          
        //         elevator.setPosition(Position.LOAD);
        //   // }
        // }else{
        //   if(flip_arm && arm.getArmSensorPosition() > 10){
        //     elevator.setPosition(Position.LOAD);
        //   }
          
          
        

      }

      if(operator.ltbutton()){
        elevator.setPosition(Position.HOME);
        if(game_piece && !flip_arm)
          arm.setPosition(APosition.HIGH);
      }

      if(operator.ybutton_pressed()){
        elevator.setPosition(Position.HIGH);
        if(game_piece && !flip_arm)
          arm.setPosition(APosition.HIGH);
      }

      if(operator.bbutton_pressed()){
        elevator.setPosition(Position.MID);
        if(game_piece && !flip_arm)
          arm.setPosition(APosition.HIGH);
      }

      if(operator.abutton_pressed()){
        elevator.setPosition(Position.LOW);
        if(game_piece && !flip_arm)
          arm.setPosition(APosition.HIGH);
      }

      // if(operator.ltbutton_pressed()){
      //   elevator.setPosition(Position.HOME);
      //   // try{
      //   //   new Thread(() -> {
      //   //     boolean complete = false;
      //   //     boolean run_once = false;
      //   //     while(!complete){
      //   //       if((elevator.isElevatorFlippable() && elevator.isTargetPositionFlippable()) || elevator.isTargetPositionFlippable()){
      //   //         if(!flip_arm){
      //   //           //arm.setPosition(APosition.FLIPLOAD);
      //   //           flip_arm = !flip_arm;
      //   //           complete = true;
      //   //         }else{
      //   //             arm.setPosition(APosition.LOAD); 
      //   //             flip_arm = !flip_arm;
      //   //             complete = true;
      //   //         }
      //   //       }else{
      //   //         if(!run_once){
      //   //           elevator.setPosition(Position.FLIP);
      //   //           run_once = true;
      //   //         }
      //   //       }
      //   //     }
      //   //     //thread kill
      //   //     return;
      //   //   }).start();
      //   //   }catch(Exception e) {}
      //   }

        if(!climber_deployed){
          elevator.holdPosition();
        }else{
          elevator.set(0.0);  
        }
        }
      }
    }
  
    if(operator.rightthumby() > 0.15 || operator.rightthumby() < -0.15){
      if(arm.getArmSensorPosition() >= 900 && operator.rightthumby() > 0.2){
        arm.holdPosition();
      }else{
        if(climber_deployed){
          arm.set(operator.rightthumby() * 0.6);
        }else{
          arm.set(operator.rightthumby() * 0.4);
        }
      }
    }else{
      arm.holdPosition();
    }


  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
