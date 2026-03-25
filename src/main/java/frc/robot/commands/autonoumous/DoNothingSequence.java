package frc.robot.commands.autonoumous;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Swerve;

public class DoNothingSequence extends Command {
    private final Swerve sysSwerve;
    private final Shooter sysShooter;
    private final Climber sysClimber;

    public DoNothingSequence(Swerve sysSwerve, Shooter sysShooter, Climber sysClimber) {
        this.sysSwerve = sysSwerve;
        this.sysShooter = sysShooter;
        this.sysClimber = sysClimber;
        addRequirements(sysSwerve, sysShooter, sysClimber);
    }

    @Override
    public void execute() {
        sysSwerve.disable();
        sysShooter.disable();
        sysClimber.disable();
    }
}
