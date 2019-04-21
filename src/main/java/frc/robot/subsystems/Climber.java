package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;


public class Climber {

    //Spark wing_wheels;

    DoubleSolenoid climber;


    public Climber(){
        //wing_wheels = new Spark(0);
        //climber = new Solenoid(1, 0);
        climber = new DoubleSolenoid(1, 0, 6);
    }

    public void wing_forward(){
    }

    public void wing_stop(){
    }

    public void wing_reverse(){
    }

    // public void set_wingwheels(double ouput){
    //     wing_wheels.set(ouput);
    // }

    // public void wing_wheel_forward(){
    //     wing_wheels.set(1.0);
    // }

    public void wing_wheel_stop(){
        //wing_wheels.set(0.0);
    }

    public void wing_wheel_reverse(){
        //wing_wheels.set(-1.0);
    }

    public void climb_cylinder_forward(){
        //climber.set(true);
        climber.set(Value.kForward);
    
    }

    public void climb_cylinder_reverse(){
        //climber.set(false);
        climber.set(Value.kReverse);
    }

    public void climb_cylinder_off(){
        climber.set(Value.kOff);
    }
    
}