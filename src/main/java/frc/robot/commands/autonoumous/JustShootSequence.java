package frc.robot.commands.autonoumous;

import static frc.robot.Dashboard.tblAutonomous;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.hawklib.dashboard.DashboardValue;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Swerve;

public class JustShootSequence extends Command {
    private final Swerve sysSwerve;
    private final Shooter sysShooter;
    private final Climber sysClimber;

    private final Timer tmrSequence = new Timer();

    private double mFlywheelPower = 0.7;
    private double mFeedPower = 0.75;

    private final NetworkTable tblAutonomous_JustShoot = tblAutonomous.getSubTable("Just Shoot");

    public JustShootSequence(Swerve sysSwerve, Shooter sysShooter, Climber sysClimber) {
        setName("Autonomous Sequence [Just Shoot]");
        this.sysSwerve = sysSwerve;
        this.sysShooter = sysShooter;
        this.sysClimber = sysClimber;
        addRequirements(sysSwerve, sysShooter, sysClimber);

        new DashboardValue<Double>(tblAutonomous_JustShoot, "Flywheel Power", val -> mFlywheelPower = val, mFlywheelPower);
        new DashboardValue<Double>(tblAutonomous_JustShoot, "Feed Power", val -> mFeedPower = val, mFeedPower);
    }

    @Override
    public void initialize() {
        sysSwerve.disable();
        sysShooter.disable();
        sysClimber.disable();

        tmrSequence.restart();
    }

    @Override
    public void execute() {
        /*if(tmrSequence.get() > 0.5)
            if(tmrSequence.get() % DriverConstants.SHIMMY_PERIOD * 2.0 < DriverConstants.SHIMMY_PERIOD)
                sysSwerve.drive(new Translation2d(Constants.SwerveConstants.maxSpeed, 0.0), 0.0, false, true);
            else
                sysSwerve.drive(new Translation2d(-Constants.SwerveConstants.maxSpeed, 0.0), 0.0, false, true);
        else
            sysSwerve.disable();
*/
        sysShooter.setFlywheel(mFlywheelPower);

        if(tmrSequence.get() < 0.5) 
            sysShooter.disableFeed();
        else
            sysShooter.setFeed(mFeedPower);
    }
}
