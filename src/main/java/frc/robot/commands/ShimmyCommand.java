package frc.robot.commands;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.DriverConstants;
import frc.robot.Constants.SwerveConstants;
import frc.robot.subsystems.Swerve;

public class ShimmyCommand extends Command {
    private final Swerve sysSwerve;
    private final Timer tmrPeriod = new Timer();

    public ShimmyCommand(Swerve sysSwerve) {
        this.sysSwerve = sysSwerve;
        addRequirements(sysSwerve);
    }

    @Override
    public void initialize() { tmrPeriod.restart(); }

    @Override
    public void execute() {
        if(tmrPeriod.get() % DriverConstants.SHIMMY_PERIOD * 2.0 < DriverConstants.SHIMMY_PERIOD)
            sysSwerve.drive(new Translation2d(SwerveConstants.maxSpeed, 0.0), 0.0, false, true);
        else
            sysSwerve.drive(new Translation2d(-SwerveConstants.maxSpeed, 0.0), 0.0, false, true);
    }
}
