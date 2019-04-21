package frc.libs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class MagicTalonSRX extends TalonSRX{

    protected double mLastSet = Double.NaN;
    protected ControlMode mLastControlMode = null;

    int lastValue;

    public MagicTalonSRX(int devicenumber){
        super(devicenumber);
    }

    public double getLastSet() {
        return mLastSet;
    }

    @Override
    public void set(ControlMode mode, double value) {
        if (value != mLastSet || mode != mLastControlMode) {
            mLastSet = value;
            mLastControlMode = mode;
            super.set(mode, value);
        }
    }

    public int getSelectedSensorPosition(){
        if(lastValue != super.getSelectedSensorPosition()){
            lastValue = super.getSelectedSensorPosition();
            return lastValue;
        }
        else{
            return lastValue;
        }
    }
}