package frc.hawklib.hid;

import edu.wpi.first.math.MathUtil;

public class XboxController extends edu.wpi.first.wpilibj.XboxController {
    private double mAxisDeadzoneThreshold = 0.10;
    private double mTriggerButtonThreshold = 0.90;

    private boolean mXAxisInverted = false;
    private boolean mYAxisInverted = false;
    
    public XboxController(int port) { super(port); }

	public XboxController configAxisDeadzone(double value) { 
        mAxisDeadzoneThreshold = value; 
        return this;
    }
	public XboxController configTriggerThreshold(double value) { 
        mTriggerButtonThreshold = value; 
        return this;
    }

	public XboxController configXAxisInverted(boolean isInverted) { 
        mXAxisInverted = isInverted; 
        return this;
    }

	public XboxController configYAxisInverted(boolean isInverted) { 
        mYAxisInverted = isInverted; 
        return this;
    }

    @Override public double getRawAxis(int axis) { return MathUtil.applyDeadband(super.getRawAxis(axis), mAxisDeadzoneThreshold); }

    @Override public double getLeftX() { return mXAxisInverted ? -super.getLeftX() : super.getLeftX(); }
    @Override public double getRightX() { return mXAxisInverted ? -super.getRightX() : super.getRightX(); }

    @Override public double getLeftY() { return mYAxisInverted ? -super.getLeftY() : super.getLeftY(); }
    @Override public double getRightY() { return mYAxisInverted ? -super.getRightY() : super.getRightY(); }

	public boolean getLeftTriggerButton() { return getLeftTriggerAxis() >= mTriggerButtonThreshold; }
	public boolean getRightTriggerButton() { return getRightTriggerAxis() >= mTriggerButtonThreshold; }
	public double getTriggerAxis() { return getRightTriggerAxis() - getLeftTriggerAxis(); }
}
