package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Dashboard {
    public static final NetworkTable tblHawks = NetworkTableInstance.getDefault().getTable("Hawks");
    public static final NetworkTable tblSubsystems = tblHawks.getSubTable("Subsystems");
    public static final NetworkTable tblDrivingOptions = tblHawks.getSubTable("Driving Options");
}
