package frc.hawklib.hid;

import edu.wpi.first.math.MathUtil;

public class PS4Controller extends edu.wpi.first.wpilibj.PS4Controller{
    private double mAxisDeadzoneThreshold = 0.10;

    private boolean mYAxisInverted = false;
    
    public PS4Controller(int port) { super(port); }

	public void configAxisDeadzone(double value) { mAxisDeadzoneThreshold = value; }

	public void configYAxisInverted(boolean isInverted) { mYAxisInverted = isInverted; }

    @Override public double getRawAxis(int axis) { return MathUtil.applyDeadband(super.getRawAxis(axis), mAxisDeadzoneThreshold); }

    @Override public double getLeftY() { return mYAxisInverted ? -super.getLeftY() : super.getLeftY(); }
    @Override public double getRightY() { return mYAxisInverted ? -super.getRightY() : super.getRightY(); }

	public double getTriggerAxis() { return getR2Axis() - getL2Axis(); }
}
