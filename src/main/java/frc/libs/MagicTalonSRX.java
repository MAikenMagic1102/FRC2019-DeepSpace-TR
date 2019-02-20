package frc.libs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class MagicTalonSRX extends TalonSRX{

    protected double mLastSet = Double.NaN;
    protected ControlMode mLastControlMode = null;

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
}