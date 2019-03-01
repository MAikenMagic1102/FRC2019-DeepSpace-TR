package frc.robot.subsystems;

import frc.robot.Constants;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Solenoid;


public class Drivetrain{

    CANSparkMax LeftFront; 
    CANSparkMax LeftRear; 
    CANSparkMax RightFront; 
    CANSparkMax RightRear;
    
    CANEncoder LeftDriveEncoder;
    CANEncoder RightDriveEncoder;

    Solenoid shift_solenoid;

    public Drivetrain(){
        RightFront = new CANSparkMax(2, MotorType.kBrushless);
        LeftRear = new CANSparkMax(4, MotorType.kBrushless);
        LeftFront = new CANSparkMax(3, MotorType.kBrushless);
        RightRear = new CANSparkMax(5, MotorType.kBrushless);

        //LeftFront.setInverted(true);
        //LeftRear.setInverted(true);
        RightFront.setInverted(true);
        RightRear.setInverted(true);

        LeftDriveEncoder = LeftRear.getEncoder();
        RightDriveEncoder = RightRear.getEncoder();

        shift_solenoid = new Solenoid(1, 3);
    }

    public double getLeftRearEncoder_Pos(){
        return LeftDriveEncoder.getPosition();
    }

    public double getRightRearEncoder_Pos(){
        return RightDriveEncoder.getPosition();
    }

    public Boolean getShiftSolenoidState(){
        return shift_solenoid.get();
    }

    //scales the joystick input to cause the robot to have a less steep acceleration curve.
    //This works well for us in VEX... testing needed to verify FRC use.
    double drive_math(double input){
        double out;
        double sing;
       
        if(input < 0){
            sing = -1;
            input = -input;
        }else {
            sing = 1;
        }
       
        if (input < .2){
            out = 0;
        }else{
            if(input < .80){
                out = input * 0.5;
            }else {
                out = input;
            }
        } 
        return out*sing;
    }

    double drive_math_mec(double input){
        double out;
        double sing;
       
        if(input < 0){
            sing = -1;
            input = -input;
        }else {
            sing = 1;
        }
       
        if (input < .2){
            out = 0;
        }else{
            if(input < .80){
                out = input * 0.3;
            }else {
                out = input;
            }
        } 
        return out*sing;
    }

    public void arcade_drive_openloop(double inversion, double leftY, double rightX){
        
        shift_solenoid.set(false);

        if(inversion == -1){
            rightX = -rightX;
        }
        
        double leftPower =  inversion * (leftY - rightX);
        double rightPower = inversion * (leftY + rightX);

        LeftFront.set(drive_math(leftPower));
        LeftRear.set(drive_math(leftPower));
        RightFront.set(drive_math(rightPower));
        RightRear.set(drive_math(rightPower));

    }
    

    public void mecanum_drive_openloop(double inversion, double triggers, double rightX, double leftY){

        rightX = rightX * -1;
        
        if(inversion == -1){
            rightX = -rightX;
        }
        
        double _FrontRight = leftY - rightX - triggers;
        double _RearRight = leftY - rightX + triggers;
        double _FrontLeft = leftY + rightX + triggers;
        double _RearLeft = leftY + rightX - triggers;

        RightRear.set(inversion * drive_math_mec(_RearRight));
        RightFront.set(inversion * drive_math_mec(_FrontRight));
        LeftRear.set(inversion * drive_math_mec(_RearLeft));
        LeftFront.set(inversion * drive_math_mec(_FrontLeft));

        shift_solenoid.set(true);
      
    }


}