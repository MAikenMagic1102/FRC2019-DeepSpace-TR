package frc.libs;

import edu.wpi.first.wpilibj.AnalogInput;

public class REVAnalogPressureSensor {

    double vCC = 5.0;
    AnalogInput pressureSensor;

    public REVAnalogPressureSensor(int analogPin){
        pressureSensor = new AnalogInput(analogPin);
    }

    void init(int bits){
        pressureSensor.setAverageBits(bits);
    }

    public double getPressure(){
        return 250 * pressureSensor.getAverageVoltage() / vCC - 25;
    }

}