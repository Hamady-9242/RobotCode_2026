package frc.hawklib.hid;

import edu.wpi.first.math.MathUtil;

/**
 * A minor wrapper class on {@link edu.wpi.first.wpilibj.XboxController} that provides additional functionality such as
 * built-in deadzones on axes, options to invert the joysticks axes, and the abililty to read the triggers as individual buttons
 * or one comined axis.
 */
public class XboxController extends edu.wpi.first.wpilibj.XboxController {
    private double mAxisDeadzoneThreshold = 0.10;
    private double mTriggerButtonThreshold = 0.90;

    private boolean mXAxisInverted = false;
    private boolean mYAxisInverted = false;
    
    /**
	 * Construct an instance of an Xbox Controller.
	 * @param port The port on the Driver Station that the controller is assigned to.
	 */
    public XboxController(int port) { super(port); }

    /**
     * Configure the deadzone on each joystick axis
     * @param value How much of the axis is ignored [0.0 to 1.0]
     * @return A reference to this controller to chain configuration methods
     */
	public XboxController configAxisDeadzone(double value) { 
        mAxisDeadzoneThreshold = Math.abs(value); 
        return this;
    }

    /**
     * Configure the deadzone on each trigger axis
     * @param value How much of the trigger axis is ignored [0.0 to 1.0]
     * @return A reference to this controller to chain configuration methods
     */
	public XboxController configTriggerThreshold(double value) { 
        mTriggerButtonThreshold = value; 
        return this;
    }

    /**
     * Invert the X-axis of the joysticks
     * @param isInverted True to invert, false to leave as-is
     * @return A reference to this controller to chain configuration methods
     */
	public XboxController configXAxisInverted(boolean isInverted) { 
        mXAxisInverted = isInverted; 
        return this;
    }

    /**
     * Invert the Y-axis of the joysticks
     * @param isInverted True to invert, false to leave as-is
     * @return A reference to this controller to chain configuration methods
     */
	public XboxController configYAxisInverted(boolean isInverted) { 
        mYAxisInverted = isInverted; 
        return this;
    }

    @Override public double getRawAxis(int axis) { return MathUtil.applyDeadband(super.getRawAxis(axis), mAxisDeadzoneThreshold); }

    @Override public double getLeftX() { return mXAxisInverted ? -super.getLeftX() : super.getLeftX(); }
    @Override public double getRightX() { return mXAxisInverted ? -super.getRightX() : super.getRightX(); }

    @Override public double getLeftY() { return mYAxisInverted ? -super.getLeftY() : super.getLeftY(); }
    @Override public double getRightY() { return mYAxisInverted ? -super.getRightY() : super.getRightY(); }

    /**
     * Treat the left trigger axis as a button as determined by the trigger button threshold
     * @return True if pressed further than the threshold
     * 
     * @see #configTriggerThreshold(double)
     */
	public boolean getLeftTriggerButton() { return getLeftTriggerAxis() >= mTriggerButtonThreshold; }

    /**
     * Treat the right trigger axis as a button as determined by the trigger button threshold
     * @return True if pressed further than the threshold
     * 
     * @see #configTriggerThreshold(double)
     */
	public boolean getRightTriggerButton() { return getRightTriggerAxis() >= mTriggerButtonThreshold; }

    /**
     * Treat both triggers as one
     * @return Right trigger axis value subtracted by the left trigger axis value
     */
	public double getTriggerAxis() { return getRightTriggerAxis() - getLeftTriggerAxis(); }
}
