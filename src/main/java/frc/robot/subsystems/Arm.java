package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.robot.Constants;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

public class Arm{

    TalonSRX arm;
    Solenoid diskbrake;

    Constants constants = new Constants();

    private double holdPosition = 0;
    public double armPosition;

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
        diskbrake = new Solenoid(1, 3);

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

        arm.configMotionCruiseVelocity(constants.ArmMaxVelocity, constants.Arm_kTimeoutMs); //Sensor Units per 100 MS
        arm.configMotionAcceleration(constants.ArmMaxAccel, constants.Arm_kTimeoutMs); //Sensor Units per 100 MS

        arm.setSelectedSensorPosition(0, constants.Arm_kPIDLoopIdx, constants.Arm_kTimeoutMs);
    }

    public void zeroArm(){
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
                    arm.set(ControlMode.MotionMagic,  -1186);
                        break;
                    case GROUND:
                    arm.set(ControlMode.MotionMagic, -1045);
                        break;
                    case LOAD:
                    arm.set(ControlMode.MotionMagic, -1186);    
                        break;
                    case FLIPLOAD:
                    arm.set(ControlMode.MotionMagic, 931);
                        break;
                    case HIGH:
                    arm.set(ControlMode.MotionMagic, -642);
                        break;
                }
                break;
            case HATCH:
                switch(AcurrentPos){
                    case HOME:
                    arm.set(ControlMode.MotionMagic, -640);
                        break;
                    case GROUND:
                    arm.set(ControlMode.MotionMagic, -1055);
                        break;
                    case LOAD:
                    arm.set(ControlMode.MotionMagic, -978);   
                        break;
                    case FLIPLOAD:
                    arm.set(ControlMode.MotionMagic, 920);
                        break;
                    case HIGH:
                        break;
                }
                break;
            }
        
    }

    public void update_brake(boolean climbing){
        if((Math.abs(arm.getClosedLoopError()) < 75 && AcurrentMode != AMode.MANUAL) || climbing){
            diskbrake.set(true);
        }else{
            diskbrake.set(false);
        }
    }

    public boolean isProfileFinished(){
        if(arm.getClosedLoopError() < 28)
            return true;
        else
            return false;
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

    public int getClosedLoopError(){
        return arm.getClosedLoopError();
    }

    public boolean isArmFlipped(){
        if(arm.getSelectedSensorPosition() > 850 || arm.getSelectedSensorPosition() < -900){
            return true;
        }else{
            return false;
        }
    }

    public boolean isArmFlippingPastElevator(){
        if(arm.getActiveTrajectoryPosition() == 931 && arm.getSelectedSensorPosition() > 100){
            return true;
        }else{
            if(arm.getActiveTrajectoryPosition() == -1052 && arm.getSelectedSensorPosition() < -100){
                return true;
            }else{
                return false;
            }
        }
    }
        
}