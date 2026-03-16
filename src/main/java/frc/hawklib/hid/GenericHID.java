package frc.hawklib.hid;

import edu.wpi.first.math.MathUtil;

public class GenericHID extends edu.wpi.first.wpilibj.GenericHID{
        private double mAxisDeadzoneThreshold = 0.10;
    
    public GenericHID(int port) { super(port); }

	public void configAxisDeadzone(double value) { mAxisDeadzoneThreshold = value; }

    @Override public double getRawAxis(int axis) { return MathUtil.applyDeadband(super.getRawAxis(axis), mAxisDeadzoneThreshold); }
}
