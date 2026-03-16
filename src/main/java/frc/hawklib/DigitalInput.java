package frc.hawklib;

/**
 * Wrapper Class on {@link edu.wpi.first.wpilibj.DigitalInput} to add inversion options
 */
public class DigitalInput extends edu.wpi.first.wpilibj.DigitalInput{
    private boolean mIsInverted = false;

    public DigitalInput(int channel) { this(channel, false); }
    public DigitalInput(int channel, boolean isInverted) {
        super(channel);
        mIsInverted = isInverted;
    }

    public void configInverted(boolean isInverted) { mIsInverted = isInverted; }

    public boolean get() { return mIsInverted ? !super.get() : super.get(); }
}
