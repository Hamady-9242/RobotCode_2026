package frc.hawklib.dashboard;

import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import frc.hawklib.Utility;

/**
 * A simple, type-locked entry on a {@link NetworkTable}
 */
@SuppressWarnings("unchecked")
public class DashboardValue<ValueType> {
    private final NetworkTableEntry ENTRY;

    private final Supplier<ValueType> GETTER;
    private final Consumer<ValueType> SETTER;

    /**
     * Constructs a reference to a value on the Network Table
     * @param table Parent table that does/will contain the value
     * @param title Label used to identify the value
     */
    public DashboardValue(NetworkTable table, String title) { this(table, title, null, null, null); }

    /**
     * Constructs a reference to a value on the Network Table
     * @param table Parent table that does/will contain the value
     * @param title Label used to identify the value
     * @param getter Functional interface to automatically retrieve the value from code and update it in the Dashboard
     */
    public DashboardValue(NetworkTable table, String title, Supplier<ValueType> getter) { this(table, title, getter, null, null); }
    
    /**
     * Constructs a reference to a value on the Network Table
     * @param table Parent table that does/will contain the value
     * @param title Label used to identify the value
     * @param setter Functional interface to automatically retrieve the value from the Dashboard and use it in code
     */
    public DashboardValue(NetworkTable table, String title, Consumer<ValueType> setter) { this(table, title, null, setter, null); }
    
    /**
     * Constructs a reference to a value on the Network Table
     * @param table Parent table that does/will contain the value
     * @param title Label used to identify the value
     * @param getter Functional interface to automatically retrieve the value from code and update it in the Dashboard
     * @param setter Functional interface to automatically retrieve the value from the Dashboard and use it in code
     */
    public DashboardValue(NetworkTable table, String title, Supplier<ValueType> getter, Consumer<ValueType> setter) { this(table, title, getter, setter, null); }

    /**
     * Constructs a reference to a value on the Network Table
     * @param table Parent table that does/will contain the value
     * @param title Label used to identify the value
     * @param defaultValue Value to initialize into the value at robot startup
     */
    public DashboardValue(NetworkTable table, String title, ValueType defaultValue) { this(table, title, null, null, defaultValue); }
    
    /**
     * Constructs a reference to a value on the Network Table
     * @param table Parent table that does/will contain the value
     * @param title Label used to identify the value
     * @param getter Functional interface to automatically retrieve the value from code and update it in the Dashboard
     * @param defaultValue Value to initialize into the value at robot startup
     */
    public DashboardValue(NetworkTable table, String title, Supplier<ValueType> getter, ValueType defaultValue) { this(table, title, getter, null, defaultValue); }
    
    /**
     * Constructs a reference to a value on the Network Table
     * @param table Parent table that does/will contain the value
     * @param title Label used to identify the value
     * @param setter Functional interface to automatically retrieve the value from the Dashboard and use it in code
     * @param defaultValue Value to initialize into the value at robot startup
     */
    public DashboardValue(NetworkTable table, String title, Consumer<ValueType> setter, ValueType defaultValue) { this(table, title, null, setter, defaultValue); }

    /**
     * Constructs a reference to a value on the Network Table
     * @param table Parent table that does/will contain the value
     * @param title Label used to identify the value
     * @param getter Functional interface to automatically retrieve the value from code and update it in the Dashboard
     * @param setter Functional interface to automatically retrieve the value from the Dashboard and use it in code
     * @param defaultValue Value to initialize into the value at robot startup
     */
    public DashboardValue(NetworkTable table, String title, Supplier<ValueType> getter, Consumer<ValueType> setter, ValueType defaultValue) {
        Utility.waitForNetworkTableConnection();

        ENTRY = table.getEntry(title); 
        GETTER = getter;
        SETTER = setter;

        if(defaultValue != null) 
            set(defaultValue);

        DashboardManager.add(this);
    }

    /**
     * Get the type-locked value from the Network Table
     * @return
     */
    public ValueType get() { return (ValueType) ENTRY.getValue().getValue(); }

    /**
     * Set to a new value
     * @param value
     */
    public void set(ValueType value) { ENTRY.setValue(value); }

    /**
     * Poll for updates to/from getter/setter
     */
    public void update() {
        if(SETTER != null) SETTER.accept(get());
        if(GETTER != null) set(GETTER.get());
    }

    /**
     * Close the value and remove it from the Network Table
     */
    public void delete() { ENTRY.unpublish(); }
}
