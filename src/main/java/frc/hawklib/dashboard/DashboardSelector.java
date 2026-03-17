package frc.hawklib.dashboard;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;
import frc.hawklib.Utility;

/**
 * A dropdown selector preloaded with options provided through an enumeration
 */
public class DashboardSelector<Type extends Enum<Type>> implements Sendable, AutoCloseable {

    private final NetworkTable TABLE;
    private final Type DEFAULT_OPTION;
    private final Map<String, Type> OPTION_MAP = new LinkedHashMap<>();
    private final int INSTANCE;
    private static final AtomicInteger INSTANCES = new AtomicInteger();

    private Type mSelectedOption;

    /**
     * 
     * @param parentTable
     * @param title
     * @param defaultOption
     */
    public DashboardSelector(NetworkTable parentTable, String title, Type defaultOption){
        //Wait for NetworkTables to get connected
        Utility.waitForNetworkTableConnection();

        //Initalize values
        TABLE = parentTable.getSubTable(title);
        DEFAULT_OPTION = defaultOption;
        mSelectedOption = defaultOption;

        INSTANCE = INSTANCES.getAndIncrement();

        //Load all options into the map
        for(Type option : defaultOption.getDeclaringClass().getEnumConstants())
            OPTION_MAP.put(option.toString(), option);

        //Create the builder
        SendableBuilderImpl builder = new SendableBuilderImpl();
        builder.setTable(TABLE);
        builder.startListeners();

        //Add to the registry
        SendableRegistry.add(this, "DashboardChooser", INSTANCE);
        SendableRegistry.publish(this, builder);
        
        //Add to background manager
        DashboardManager.add(this);
    }

    /**
     * 
     * @return
     */
    public Type get() { return mSelectedOption; }

    @Override
    public void initSendable(SendableBuilder builder) {
        
        builder.setSmartDashboardType("String Chooser");
        builder.publishConstInteger(".instance", INSTANCE);
        builder.publishConstString("default", DEFAULT_OPTION.toString());
        builder.publishConstStringArray("options", OPTION_MAP.keySet().toArray(new String[0]));
        builder.addStringProperty("active", () -> {
            if(mSelectedOption != null) 
                return mSelectedOption.toString();
            else 
                return DEFAULT_OPTION.toString();
        }, null);
        builder.addStringProperty("selected", null, val -> {
            mSelectedOption = OPTION_MAP.get(val);
        });

        TABLE.getEntry("selected").setString(DEFAULT_OPTION.toString());
    }

    @Override
    public void close() {
        SendableRegistry.remove(this);
    }

    /**
     * 
     */
    public void update() { SendableRegistry.update(this); }
    
}
