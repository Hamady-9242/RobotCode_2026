package frc.robot.subsystems;

import static frc.robot.Dashboard.tblSubsystems;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.hawklib.dashboard.DashboardValue;

public class Climber  extends SubsystemBase{
    private final NetworkTable tblClimber = tblSubsystems.getSubTable("Climber");

    private final SparkMax mtrWinch = new SparkMax (9, MotorType.kBrushless);

    private double mWinchPower = 0.35;
    
    public Climber(){
        new DashboardValue<Double>(tblClimber, "Extend Power", val -> mWinchPower = val, mWinchPower);

        mtrWinch.configure(
            new SparkMaxConfig().idleMode(IdleMode.kBrake),
            ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters
        );

        setDefaultCommand(
            runOnce(this::disable)
            .andThen(idle())
        );
    }

    public void setWinch(double power){
        mtrWinch.set(power);
    }
    
    public void disable(){
        mtrWinch.disable();

    }

    public Command extend() {
        return run(()->{
            setWinch(mWinchPower);
        });
    }

    public Command retract() {
        return run(()->{
            setWinch(-mWinchPower);
        });
    }
    
}
