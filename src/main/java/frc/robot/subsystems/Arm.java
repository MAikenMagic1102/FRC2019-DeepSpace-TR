package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.robot.Constants;
import frc.robot.subsystems.Elevator.GamePiece;
import frc.robot.subsystems.Elevator.Position;

public class Arm{

    private TalonSRX arm;

    Constants constants = new Constants();

    private double holdPosition = 0;

    public enum AMode{
        MANUAL,
        HOLD,
        HOLDING,
        MOTION_MAGIC
    };

    public enum APosition{
        HOME,
        GROUND,
        LOAD,
        FLIPLOAD,
        HIGH,
    };

    public enum AGamePiece{
        CARGO,
        HATCH
    };

    public AMode AcurrentMode = AMode.HOLDING;
    public APosition AcurrentPos = APosition.HOME;
    public AGamePiece AcurrentGP = AGamePiece.HATCH;

    public Arm(){
        arm = new TalonSRX(9);

        arm.configFactoryDefault();

        arm.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, constants.Arm_kTimeoutMs);

        arm.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, 30);
        arm.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, 30);

        arm.configNominalOutputForward(0, constants.Arm_kTimeoutMs);
        arm.configNominalOutputReverse(0, constants.Arm_kTimeoutMs);
        arm.configPeakOutputForward(1, constants.Arm_kTimeoutMs);
        arm.configPeakOutputReverse(-1, constants.Arm_kTimeoutMs);

        arm.selectProfileSlot(constants.Arm_kSlotIdx, constants.Arm_kPIDLoopIdx);
        arm.config_kF(constants.Arm_kSlotIdx, constants.Arm_kF, constants.Arm_kTimeoutMs);
        arm.config_kP(constants.Arm_kSlotIdx, constants.Arm_kP, constants.Arm_kTimeoutMs);
        arm.config_kI(constants.Arm_kSlotIdx, constants.Arm_kI, constants.Arm_kTimeoutMs);
        arm.config_kD(constants.Arm_kSlotIdx, constants.Arm_kD, constants.Arm_kTimeoutMs);

        arm.configMotionCruiseVelocity(470, constants.Arm_kTimeoutMs); //Sensor Units per 100 MS
        arm.configMotionAcceleration(200, constants.Arm_kTimeoutMs); //Sensor Units per 100 MS

        arm.setSelectedSensorPosition(0, constants.Arm_kPIDLoopIdx, constants.Arm_kTimeoutMs);
    }

    public void set(double power){
        AcurrentMode = AMode.MANUAL;
        arm.set(ControlMode.PercentOutput, power);
    }


    public void holdPosition() {
        if(AcurrentMode == AMode.MOTION_MAGIC){
            setPosition(AcurrentPos);
        }
        else{
            if(AcurrentMode == AMode.HOLDING){
                holdingPosition();
            }else{
                AcurrentMode = AMode.HOLD;
                holdPosition = arm.getSelectedSensorPosition();
                holdingPosition();
            }
        }
    }

    public void holdingPosition(){
        if(AcurrentMode == AMode.HOLD || AcurrentMode == AMode.HOLDING){
            arm.set(ControlMode.MotionMagic, holdPosition);
            AcurrentMode = AMode.HOLDING;
        }
    }

    public void setGamePiece(AGamePiece gamepiece){
        AcurrentGP = gamepiece;
    }

    public void setPosition(APosition position){
        AcurrentPos = position;
        AcurrentMode = AMode.MOTION_MAGIC;
        switch(AcurrentGP){
            case CARGO:
                switch(AcurrentPos){
                    case HOME:
                    arm.set(ControlMode.MotionMagic, 0);
                        break;
                    case GROUND:
                    arm.set(ControlMode.MotionMagic, -1055);
                        break;
                    case LOAD:
                    arm.set(ControlMode.MotionMagic, -1042);    
                        break;
                    case FLIPLOAD:
                    arm.set(ControlMode.MotionMagic, 931);
                        break;
                    case HIGH:
                    arm.set(ControlMode.MotionMagic, -426);
                        break;
                }
                break;
            case HATCH:
                switch(AcurrentPos){
                    case HOME:
                    arm.set(ControlMode.MotionMagic, -10);
                        break;
                    case GROUND:
                    arm.set(ControlMode.MotionMagic, -1055);
                        break;
                    case LOAD:
                    arm.set(ControlMode.MotionMagic, -1042);    
                        break;
                    case FLIPLOAD:
                    arm.set(ControlMode.MotionMagic, 931);
                        break;
                    case HIGH:
                    arm.set(ControlMode.MotionMagic, -426);
                        break;
                }
                break;
        }
    }

    public String getCurrentGamePiece(){
        return AcurrentGP.toString();
    }

    public String getAcurrentPositionMode(){
        return AcurrentPos.toString();
    }

    public double getActiveTrajectoryPosition(){
        return arm.getActiveTrajectoryPosition();
    }

    public String getControlMode(){
        return arm.getControlMode().toString();
    }

    public double getHoldPosition(){
        return holdPosition;
    }
    
    public String getAcurrentMode(){
        return AcurrentMode.toString();
    }

    public double getMotorOutputVoltage(){
        return arm.getMotorOutputVoltage();
    }

    public double getMotorOutputCurrent(){
        return arm.getOutputCurrent();
    }

    public int getArmSensorPosition(){
        return arm.getSelectedSensorPosition();
    }

    public int getArmSensorVelocity(){
        return arm.getSelectedSensorVelocity();
    }

    public boolean isArmProfileComplete(){
        return arm.isMotionProfileFinished();
    }

    public double getError(){
        return arm.getActiveTrajectoryPosition() - arm.getSelectedSensorPosition();
    }
        
}