package frc.hawklib;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;

public class Utility {
    private static Timer tmrNetworkTimeout = new Timer();
    public static void waitForNetworkTableConnection() {
        if(!tmrNetworkTimeout.isRunning()) {
            tmrNetworkTimeout.restart();
            Logger.printMessage("Waiting for NetworkTables to connect...");
            while(!NetworkTableInstance.getDefault().isConnected()) {
                if(tmrNetworkTimeout.get() > 15.0) {
                    Logger.printError("Network Tables took too long to connect. Dashboard data might not work correctly.");
                    break;
                }
            }
        }
    }
}
