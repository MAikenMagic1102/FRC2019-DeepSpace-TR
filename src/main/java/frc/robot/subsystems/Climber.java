package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;


public class Climber {

    Spark wings;
    Talon wing_wheels;

    Solenoid climber;


    public Climber(){
        wings = new Spark(0);
        wing_wheels = new Talon(1);
        climber = new Solenoid(1, 0);
    }

    public void wing_forward(){
        wings.set(1.0);
    }

    public void wing_stop(){
        wings.set(0.0);
    }

    public void wing_reverse(){
        wings.set(-1.0);
    }

    public void set_wingwheels(double ouput){
        wing_wheels.set(ouput);
    }

    public void wing_wheel_forward(){
        wing_wheels.set(1.0);
    }

    public void wing_wheel_stop(){
        wing_wheels.set(0.0);
    }

    public void wing_wheel_reverse(){
        wing_wheels.set(-1.0);
    }

    public void climb_cylinder_forward(){
        climber.set(true);
    
    }
    public void climb_cylinder_reverse(){
        climber.set(false);
    }
    
}