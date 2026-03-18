package frc.robot.subsystems;

import static frc.robot.Dashboard.tblSubsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.hawklib.dashboard.DashboardValue;

@SuppressWarnings("unused")
public class Shooter extends SubsystemBase {
    private final NetworkTable tblShooter = tblSubsystems.getSubTable("Shooter");

    private final NetworkTable tblIntakeValues = tblShooter.getSubTable("Intake Values");
    private final NetworkTable tblShootValues = tblShooter.getSubTable("Shoot Values");
    private final NetworkTable tblFlushValues = tblShooter.getSubTable("Flush Values");

    private final SparkMax mtrFlywheel = new SparkMax (51, MotorType.kBrushless);
    private final SparkMax mtrFeed = new SparkMax (50, MotorType.kBrushless);
    private final Timer tmrDelay = new Timer ();

    private double mIntakeValue_FlywheelPower = 0.5;
    private double mIntakeValue_FeedPower = -0.75;

    private double mShootValue_FlywheelPower = 0.75;
    private double mShootValue_FeedPower = 1.0;
    private double mShootValue_Delay = 0.25;

    private double mFlushValue_FlywheelPower = -1.0;
    private double mFlushValue_FeedPower = 1.0;

/*
    private final DashboardValue<Double> dshIntakeValue_FlywheelPower = new DashboardValue<Double>(tblIntakeValues, "Flywheel", val -> mIntakeValue_FlywheelPower = val, mIntakeValue_FlywheelPower);
    private final DashboardValue<Double> dshIntakeValue_FeedPower = new DashboardValue<Double>(tblIntakeValues, "Feed", val -> mIntakeValue_FeedPower = val, mIntakeValue_FeedPower);

    private final DashboardValue<Double> dshShootValue_FlywheelPower = new DashboardValue<Double>(tblShootValues, "Flywheel", val -> mShootValue_FlywheelPower = val, mShootValue_FlywheelPower);
    private final DashboardValue<Double> dshShootValue_FeedPower = new DashboardValue<Double>(tblShootValues, "Feed", val -> mShootValue_FeedPower = val, mShootValue_FeedPower);
    private final DashboardValue<Double> dshShootValue_Delay = new DashboardValue<Double>(tblShootValues, "Delay", val -> mShootValue_Delay = val, mShootValue_Delay);

    private final DashboardValue<Double> dshFlushValue_FlywheelPower = new DashboardValue<Double>(tblFlushValues, "Flywheel", val -> mFlushValue_FlywheelPower = val, mFlushValue_FlywheelPower);
    private final DashboardValue<Double> dshFlushValue_FeedPower = new DashboardValue<Double>(tblFlushValues, "Feed", val -> mFlushValue_FeedPower = val, mFlushValue_FeedPower);
*/

    public Shooter() {
        new DashboardValue<Double>(tblIntakeValues, "Flywheel", val -> mIntakeValue_FlywheelPower = val, mIntakeValue_FlywheelPower);
        new DashboardValue<Double>(tblIntakeValues, "Feed", val -> mIntakeValue_FeedPower = val, mIntakeValue_FeedPower);

        new DashboardValue<Double>(tblShootValues, "Flywheel", val -> mShootValue_FlywheelPower = val, mShootValue_FlywheelPower);
        new DashboardValue<Double>(tblShootValues, "Feed", val -> mShootValue_FeedPower = val, mShootValue_FeedPower);
        new DashboardValue<Double>(tblShootValues, "Delay", val -> mShootValue_Delay = val, mShootValue_Delay);

        new DashboardValue<Double>(tblFlushValues, "Flywheel", val -> mFlushValue_FlywheelPower = val, mFlushValue_FlywheelPower);
        new DashboardValue<Double>(tblFlushValues, "Feed", val -> mFlushValue_FeedPower = val, mFlushValue_FeedPower);
        
        setDefaultCommand(
            runOnce(this::disable)
            .andThen(idle())
        );
    }

    /**
     * Set power to the Flywheel motor
     * @param power Power value [-1.0 to 1.0]
     */
    public void setFlywheel(double power) {
        mtrFlywheel.set(power);
    }

    /**
     * Set power to the Feed motor
     * @param power Power value [-1.0 to 1.0]
     */
    public void setFeed(double power) {
        mtrFeed.set(power);
    }

    /**
     * Stop all motors in the system
     */
    public void disable() {
        mtrFlywheel.disable();
        mtrFeed.disable();
    }

    /**
     * Constructs a command to run the flywheel and feed to intake balls
     * @return
     */
    public Command intake() {
        return run(()->{
            setFlywheel(mIntakeValue_FlywheelPower);
            setFeed(mIntakeValue_FeedPower);
        });
    }

    /**
     * Constructs a command that starts the flywheel, waits a moment to get up to speed, the starts the feed to shoot balls
     * @return The new command
     */
    public Command shoot() {
        return runOnce(() -> {
            setFlywheel(mShootValue_FlywheelPower);
            tmrDelay.restart();
        }).andThen(() -> {
            while(tmrDelay.get() < mShootValue_Delay);
        }).andThen(run(() -> {
            setFeed(mShootValue_FeedPower);
        }));
    }

    /**
     * Constructs a command to run both motors in full reverse to outtake balls
     * @return The new command
     */
    public Command flush() {
        return run(() -> {
            setFlywheel(mFlushValue_FlywheelPower);
            setFeed(mFlushValue_FeedPower);
        });
    }
}
