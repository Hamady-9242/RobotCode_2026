package frc.hawklib.dashboard;

import java.util.ArrayList;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.TimedRobot;

/**
 * Utility class for managing all interactions with {@link DashboardValue} and {@link DashboardSelector}
 */
@SuppressWarnings("rawtypes")
public class DashboardManager {
    private static final ArrayList<DashboardSelector> SELECTORS = new ArrayList<DashboardSelector>();
    private static final ArrayList<DashboardValue> VALUES = new ArrayList<DashboardValue>();

    private DashboardManager() {}

    /**
     * Automatically poll for updates in the background of the main Robot loop
     * @param robot reference to the active Robot instance
     */
    public static void startPeriodic(TimedRobot robot) {
        robot.addPeriodic(DashboardManager::updateAll, 0.001);
    }

    /**
     * Add a new item to the manager
     * @param selector A new {@link DashboardSelector}
     */
    public static void add(DashboardSelector selector){ SELECTORS.add(selector); }

    /**
     * Remove an item from the manager
     * @param selector Existing {@link DashboardSelector} to be removed
     */
    public static void remove(DashboardSelector selector) { 
        SELECTORS.remove(selector); 
        selector.close();
    }

    /**
     * Add a new item to the manager
     * @param value A new {@link DashboardValue}
     */
    public static void add(DashboardValue value) { VALUES.add(value); }

    /**
     * Remove an item from the manager
     * @param value Existing {@link DashboardValue} to be removed
     */
    public static void remove(DashboardValue value) {
        VALUES.remove(value);
        value.delete();
    }

    /**
     * Poll all existing Selectors and Values for updates
     */
    public static void updateAll() { 
        for(DashboardSelector selector : SELECTORS) SendableRegistry.update(selector); 
        for(DashboardValue value : VALUES) value.update();
    }

}
