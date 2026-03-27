package frc.robot.commands;

import static frc.robot.Dashboard.tblDrivingOptions;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.hawklib.dashboard.DashboardValue;
import frc.robot.Constants.DriverConstants;
import frc.robot.Constants.SwerveConstants;
import frc.robot.subsystems.Swerve;

public class ShimmyCommand extends Command {
    private final Swerve sysSwerve;
    private final Timer tmrPeriod = new Timer();

    private double mShimmyTime = 0.5;

    public ShimmyCommand(Swerve sysSwerve) {
        this.sysSwerve = sysSwerve;
        addRequirements(sysSwerve);

        new DashboardValue<Double>(tblDrivingOptions, "Shimmy Time", val -> mShimmyTime = val, mShimmyTime);
    }

    @Override
    public void initialize() { tmrPeriod.restart(); }

    @Override
    public void execute() {
        if(tmrPeriod.get() % mShimmyTime * 2.0 < mShimmyTime)
            sysSwerve.drive(new Translation2d(SwerveConstants.maxSpeed, 0.0), 0.0, false, true);
        else
            sysSwerve.drive(new Translation2d(-SwerveConstants.maxSpeed, 0.0), 0.0, false, true);
    }
}
