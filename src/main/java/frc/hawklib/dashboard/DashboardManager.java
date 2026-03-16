package frc.hawklib.dashboard;

import java.util.ArrayList;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.TimedRobot;

/**
 * Utility class for managing all interactions with Network Tables
 */
@SuppressWarnings("rawtypes")
public class DashboardManager {
    private static final ArrayList<DashboardSelector> SELECTORS = new ArrayList<DashboardSelector>();
    private static final ArrayList<DashboardValue> VALUES = new ArrayList<DashboardValue>();

    private DashboardManager() {}

    public static void startPeriodic(TimedRobot robot) {
        robot.addPeriodic(DashboardManager::updateAll, 0.001);
    }

    public static void add(DashboardSelector selector){ SELECTORS.add(selector); }
    public static void remove(DashboardSelector selector) { 
        SELECTORS.remove(selector); 
        selector.close();
    }

    public static void add(DashboardValue value) { VALUES.add(value); }
    public static void remove(DashboardValue value) {
        VALUES.remove(value);
        value.delete();
    }

    public static void updateAll() { 
        for(DashboardSelector selector : SELECTORS) SendableRegistry.update(selector); 
        for(DashboardValue value : VALUES) value.update();
    }

}
