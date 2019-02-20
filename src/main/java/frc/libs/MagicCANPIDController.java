package frc.libs;

import com.revrobotics.*;


public class MagicCANPIDController extends CANPIDController{

    double prevTarget = Double.NaN;
    
    public MagicCANPIDController(CANSparkMax device){
        super(device);
    }

    public double getPrevTarget(){
        return prevTarget;
    }

    @Override
    public CANError setReference(double value, ControlType ctrl) {
        if(prevTarget == value && ctrl == ControlType.kSmartMotion && value != 0)
            return CANError.kOK;
        else{
            prevTarget = value;
            return super.setReference(value, ctrl, 0);
        }    
    }

}




