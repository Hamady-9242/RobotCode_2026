package frc.hawklib.button;

import java.util.function.Supplier;

/**
 * Trigger based button tracking
 */
public class Button {
    private boolean mPressed = false;
    private boolean mReleased = false;
    private boolean mLastValue = false;

    private final Supplier<Boolean> TRIGGER;

    /**
     * 
     * @param trigger
     */
    public Button(Supplier<Boolean> trigger) { 
        TRIGGER = trigger;
        ButtonManager.add(this); 
    }

    /**
     * 
     * @return
     */
    public boolean get() { return TRIGGER.get(); }

    /**
     * 
     * @return
     */
    public boolean getPressed() {
        boolean tempPressed = mPressed;
        mPressed = false;
        return tempPressed;
    }

    /**
     * 
     * @return
     */
    public boolean getReleased() {
        boolean tempReleased = mReleased;
        mReleased = false;
        return tempReleased;
    }

    /**
     * 
     */
    public void clear() {
        mPressed = false;
        mReleased = false;
    }

    /**
     * 
     */
    public void update() {
        boolean currentValue = get();

        if(currentValue && !mLastValue) mPressed = true;
        if(!currentValue && mLastValue) mReleased = true;

        mLastValue = currentValue;
    }
}
