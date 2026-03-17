package frc.hawklib.dashboard;

import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import frc.hawklib.Utility;

/**
 * A simple type-locked entry on the Network Table
 */
@SuppressWarnings("unchecked")
public class DashboardValue<ValueType> {
    private final NetworkTableEntry ENTRY;

    private final Supplier<ValueType> GETTER;
    private final Consumer<ValueType> SETTER;

    public DashboardValue(NetworkTable table, String title) { this(table, title, null, null, null); }
    public DashboardValue(NetworkTable table, String title, Supplier<ValueType> getter) { this(table, title, getter, null, null); }
    public DashboardValue(NetworkTable table, String title, Consumer<ValueType> setter) { this(table, title, null, setter, null); }
    public DashboardValue(NetworkTable table, String title, Supplier<ValueType> getter, Consumer<ValueType> setter) { this(table, title, getter, setter, null); }

    public DashboardValue(NetworkTable table, String title, ValueType defaultValue) { this(table, title, null, null, defaultValue); }
    public DashboardValue(NetworkTable table, String title, Supplier<ValueType> getter, ValueType defaultValue) { this(table, title, getter, null, defaultValue); }
    public DashboardValue(NetworkTable table, String title, Consumer<ValueType> setter, ValueType defaultValue) { this(table, title, null, setter, defaultValue); }

    public DashboardValue(NetworkTable table, String title, Supplier<ValueType> getter, Consumer<ValueType> setter, ValueType defaultValue) {
        Utility.waitForNetworkTableConnection();

        ENTRY = table.getEntry(title); 
        GETTER = getter;
        SETTER = setter;

        if(defaultValue != null) 
            set(defaultValue);

        DashboardManager.add(this);
    }

    public ValueType get() { return (ValueType) ENTRY.getValue().getValue(); }
    public void set(ValueType value) { ENTRY.setValue(value); }

    public void update() {
        if(SETTER != null) SETTER.accept(get());
        if(GETTER != null) set(GETTER.get());
    }

    public void delete() { ENTRY.unpublish(); }
}
