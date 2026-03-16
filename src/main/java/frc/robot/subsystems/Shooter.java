package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {
    private final SparkMax mtrFlywheel = new SparkMax (51, MotorType.kBrushless);
    private final SparkMax mtrFeed = new SparkMax (50, MotorType.kBrushless);
    private final Timer tmrDelay = new Timer ();

    private double mPower_Intake_Flywheel = 0.5;
    private double mPower_Intake_Feed = -0.75;

    private double mPower_Shoot_Flywheel = 0.75;
    private double mPower_Shoot_Feed = 1.0;
    private double mShootingDelay = 0.25;

    private double mPower_Flush_Flywheel = -1.0;
    private double mPower_Flush_Feed = 1.0;
    
    public Shooter() {
        setDefaultCommand(
            runOnce(this::disable)
            .andThen(run(() -> {}))
        );
    }

    public void setFlywheel(double power) {
        mtrFlywheel.set(power);
    }

    public void setFeed(double power) {
        mtrFeed.set(power);
    }

    public void disable() {
        mtrFlywheel.disable();
        mtrFeed.disable();
    }

    public Command cmdIntake = run(()->{
        setFlywheel(mPower_Intake_Flywheel);
        setFeed(mPower_Intake_Feed);
    });

    public Command cmdShoot = runOnce(() -> {
        setFlywheel(mPower_Shoot_Flywheel);
        tmrDelay.restart();
    }).andThen(() -> {
        while(tmrDelay.get() < mShootingDelay);
    }).andThen(run(() -> {
        setFeed(mPower_Shoot_Feed);

    }));

    public Command cmdFlush = run(() -> {
        setFlywheel(mPower_Flush_Flywheel);
        setFeed(mPower_Flush_Feed);
    });
}
