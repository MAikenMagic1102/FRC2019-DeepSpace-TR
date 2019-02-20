package frc.robot.subsystems;

import frc.robot.Constants;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANEncoder;
//import com.revrobotics.CANPIDController;
import frc.libs.MagicCANPIDController;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Timer;

public class Elevator{
    
    private Timer profileTimer = new Timer();

    Constants constants = new Constants();
    Arm arm = new Arm();

    private double holdPosition = 0;
    public double targetPosition = 0;

    private CANSparkMax Master;
    private CANSparkMax Slave;

    private CANEncoder LeftEncoder;
    private CANEncoder MasterEncoder;

    private MagicCANPIDController PIDController;

    public enum Mode{
        MANUAL,
        HOLD,
        HOLDING,
        MOTION_MAGIC
    };

    public enum Position{
        HIGH,
        MID,
        LOW,
        LOAD,
        HOME,
        FLIP
    };

    public enum GamePiece{
        CARGO,
        HATCH
    };

    public Mode currentMode = Mode.HOLDING;
    public Position currentPos = Position.HOME;
    public GamePiece currentGP = GamePiece.HATCH;


    public Elevator(){
        Master = new CANSparkMax(6, MotorType.kBrushless);
        Slave = new CANSparkMax(7, MotorType.kBrushless);

        Master.restoreFactoryDefaults();
        Slave.restoreFactoryDefaults();

        Slave.follow(Master, true);

        PIDController = new MagicCANPIDController(Master);//Master.getPIDController(); //new MagicCANPIDController(Master);

        MasterEncoder = Master.getEncoder();

        MasterEncoder.setPosition(0);

        PIDController.setP(constants.Elevator_kP);
        PIDController.setI(constants.Elevator_kI);
        PIDController.setD(constants.Elevator_kD);
        PIDController.setIZone(constants.Elevator_kIzone);
        PIDController.setFF(constants.Elevator_kF); //kFF or kF?
        PIDController.setOutputRange(constants.Elevator_kMinOutput, constants.Elevator_kMaxOuput);

        PIDController.setSmartMotionMaxVelocity(constants.ElevatorMaxVelocity, constants.Elevator_kSmartMotionSlot);
        PIDController.setSmartMotionMaxAccel(constants.ElevatorMaxAccel, constants.Elevator_kSmartMotionSlot);
        PIDController.setSmartMotionAllowedClosedLoopError(constants.Elevator_AllowedError, constants.Elevator_kSmartMotionSlot);

        Master.burnFlash();
    }

    public void set(double power){
        currentMode = Mode.MANUAL;
        PIDController.setReference(power, ControlType.kDutyCycle);
    }


    public void holdPosition() {
        if(currentMode == Mode.MOTION_MAGIC){
            setPosition(currentPos);
        }
        else{
            if(currentMode == Mode.HOLDING){
                holdingPosition();
            }else{
                currentMode = Mode.HOLD;
                holdPosition = MasterEncoder.getPosition();
                targetPosition = holdPosition;
                holdingPosition();
            }
        }
    }

    public void holdingPosition(){
        if(currentMode == Mode.HOLD || currentMode == Mode.HOLDING){
            PIDController.setReference(holdPosition, ControlType.kSmartMotion);
            currentMode = Mode.HOLDING;
        }
    }

    public void setGamePiece(GamePiece gamepiece){
        currentGP = gamepiece;
    }

    public void setPosition(Position position){
        currentPos = position;
        currentMode = Mode.MOTION_MAGIC;
        switch(currentGP){
            case CARGO:
                switch(currentPos){
                    case HOME:
                    //Master.set(ControlMode.MotionMagic, 0);
                        break;
                    case LOAD:
                        targetPosition = constants.ElevatorHatchLoad;
                        PIDController.setReference(constants.ElevatorHatchLoad, ControlType.kSmartMotion);   
                        break;
                    case FLIP:
                    //Master.set(ControlMode.MotionMagic, 931);
                        break;
                    case HIGH://95
                        targetPosition = 95;
                        PIDController.setReference(95, ControlType.kSmartMotion);
                    //Master.set(ControlMode.MotionMagic, -426);
                        break;
                    case MID://54
                    targetPosition = 54;
                    PIDController.setReference(54, ControlType.kSmartMotion);
                    //Master.set(ControlMode.MotionMagic, -1055);
                        break;
                    case LOW:
                        targetPosition = constants.ElevatorHatchLoad;
                        PIDController.setReference(constants.ElevatorHatchLoad, ControlType.kSmartMotion);
                    //Master.set(ControlMode.MotionMagic, -1055);
                        break;
                }
                break;
            case HATCH:
                switch(currentPos){
                    case HOME:
                    //Master.set(ControlMode.MotionMagic, 0);
                        break;
                    case LOAD:
                        targetPosition = constants.ElevatorHatchLoad;
                        PIDController.setReference(constants.ElevatorHatchLoad, ControlType.kSmartMotion);
                        break;
                    case FLIP:
                        targetPosition = constants.ElevatorFlip;
                        PIDController.setReference(constants.ElevatorFlip, ControlType.kSmartMotion);
                        break;
                    case HIGH:
                        targetPosition = 98;
                        PIDController.setReference(98, ControlType.kSmartMotion);
                    //Master.set(ControlMode.MotionMagic, -426);
                        break;
                    case MID:
                        targetPosition = 54;
                        PIDController.setReference(54, ControlType.kSmartMotion);
                    //Master.set(ControlMode.MotionMagic, -1055);
                        break;
                    case LOW:
                    //Master.set(ControlMode.MotionMagic, -1055);
                        break;
                }
                break;
            
        }
    }


    // public String getControlMode(){
    //     return //Master.getControlMode().toString();
    // }

    public double getError(){
        return targetPosition - getElevatorSensorPosition();
    }

    public boolean isProfileFinished(){
        if(getError() < 1){
            return true;
        }else{
            return false;
        }
    }


    public double getHoldPosition(){
        return holdPosition;
    }
    
    public String getCurrentMode(){
        return currentMode.toString();
    }

    public double getMotorOutputVoltage(){
        return Master.getAppliedOutput();
    }

    public double getMotorOutputCurrent(){
        return Master.getOutputCurrent();
    }

    public double getElevatorSensorPosition(){
        return MasterEncoder.getPosition();
    }

    public double getElevatorSensorVelocity(){
        return MasterEncoder.getVelocity();
    }
    
    public boolean isElevatorFlippable(){
        if(MasterEncoder.getPosition() < 35){
            return false;
        }else{
            return true;
        }
    }

    public boolean isTargetPositionFlippable(){
        if(targetPosition > 35 && MasterEncoder.getPosition() > 20){
            return true;
        }else{
            return false;
        }
    }


}