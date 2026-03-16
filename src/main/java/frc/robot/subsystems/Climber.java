package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Climber  extends SubsystemBase{
    private final SparkMax mtrWinch = new SparkMax (9, MotorType.kBrushless);
    
    public Climber(){
        setDefaultCommand(
            runOnce(this::disable)
            .andThen(run(()->{}))
        );
    }

    public void setWinch(double power){
        mtrWinch.set(power);
    }
    
    public void disable(){
        mtrWinch.disable();

    }

    public Command cmdExtend = run(()->{
        setWinch(0.2);

    });
    public Command cmdRetrack = run(()->{
        setWinch(-0.2);
    });
    
}
