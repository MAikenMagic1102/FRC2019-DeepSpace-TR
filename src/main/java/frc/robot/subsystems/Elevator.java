package frc.robot.subsystems;

import frc.robot.Constants;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Timer;

public class Elevator{
    
    private Timer profileTimer = new Timer();

    Constants constants = new Constants();
    Arm arm = new Arm();

    private double holdPosition = 0;

    private CANSparkMax Master;
    private CANSparkMax Slave;

    private CANEncoder LeftEncoder;
    private CANEncoder RightEncoder;

    private CANPIDController PIDController;

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

        PIDController = Master.getPIDController();

        LeftEncoder = Master.getEncoder();
        RightEncoder = Master.getEncoder();

        RightEncoder.setPosition(0);
        LeftEncoder.setPosition(0);

        PIDController.setP(constants.Elevator_kP);
        PIDController.setI(constants.Elevator_kI);
        PIDController.setD(constants.Elevator_kD);
        PIDController.setIZone(constants.Elevator_kIzone);
        PIDController.setFF(constants.Elevator_kF); //kFF or kF?
        PIDController.setOutputRange(constants.Elevator_kMinOutput, constants.Elevator_kMaxOuput);

        PIDController.setSmartMotionMaxVelocity(constants.ElevatorMaxVelocity, constants.Elevator_kSmartMotionSlot);
        PIDController.setSmartMotionMaxAccel(constants.ElevatorMaxAccel, constants.Elevator_kSmartMotionSlot);

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
                holdPosition = RightEncoder.getPosition();
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
                    //Master.set(ControlMode.MotionMagic, -1042);    
                        break;
                    case FLIP:
                    //Master.set(ControlMode.MotionMagic, 931);
                        break;
                    case HIGH:
                    //Master.set(ControlMode.MotionMagic, -426);
                        break;
                    case MID:
                    //Master.set(ControlMode.MotionMagic, -1055);
                        break;
                    case LOW:
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
                        PIDController.setReference(constants.ElevatorHatchLoad, ControlType.kSmartMotion);
                        break;
                    case FLIP:
                        PIDController.setReference(constants.ElevatorFlip, ControlType.kSmartMotion);
                        break;
                    case HIGH:
                    //Master.set(ControlMode.MotionMagic, -426);
                        break;
                    case MID:
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
        return RightEncoder.getPosition();
    }

    public double getElevatorSensorVelocity(){
        return RightEncoder.getVelocity();
    }
        

}