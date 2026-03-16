package frc.hawklib.button;

import java.util.ArrayList;

/**
 * Utility class for managing all Button triggers
 */
public class ButtonManager {
    private static final ArrayList<Button> BUTTON_LIST = new ArrayList<Button>();

    public static void add(Button button) { BUTTON_LIST.add(button); }

    public static void remove(Button button) {
        BUTTON_LIST.remove(button);
    }

    public static void clearAll() { for(Button button : BUTTON_LIST) button.clear(); }

    public static void updateAll() { for(Button button : BUTTON_LIST) button.update(); }
}
