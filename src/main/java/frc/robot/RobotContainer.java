package frc.robot;

import static frc.robot.Dashboard.tblDrivingOptions;
import static frc.robot.Dashboard.tblHawks;

import java.util.function.Supplier;

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.hawklib.dashboard.DashboardSelector;
import frc.hawklib.dashboard.DashboardValue;
import frc.hawklib.hid.XboxController;
import frc.robot.Constants.DriverConstants;
import frc.robot.commands.SwerveCommand;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.PoseEstimator;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Swerve;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
@SuppressWarnings("unused")
public class RobotContainer {

    //Selector Options
    private enum DriveStyle {
        FIELD_CENTRIC("Field Centric"),
        ROBOT_CENTRIC("Robot Centric");

        private final String LABEL;
        private DriveStyle(String label) { LABEL = label; }

        public String toString() { return LABEL; }
    }

    private enum InputScale {
        NONE("No Scaling") {
            public double scale(double inputValue) { return inputValue; }
        }, SQUARED("Squared") {
            public double scale(double inputValue) { return inputValue * inputValue * (inputValue < 0.0 ? -1.0 : 1.0); }
        }, CUBED("Cubed") {
            public double scale(double inputValue) { return inputValue * inputValue * inputValue; }
        };

        private final String LABEL;
        private InputScale(String label) { LABEL = label; }
        
        public String toString() { return LABEL; }

        public abstract double scale(double inputValue);
    }

    /* Selectors */
    private final SendableChooser<Command> autoChooser;
    private final DashboardSelector<DriveStyle> dshDriveStyle = new DashboardSelector<DriveStyle>(tblDrivingOptions, "Drive Style", DriveStyle.ROBOT_CENTRIC);
    private final DashboardSelector<InputScale> dshInputScale = new DashboardSelector<InputScale>(tblDrivingOptions, "Input Scale", InputScale.NONE);

    /* Controllers */
    private final XboxController ctlDriver = new XboxController(0)
        .configAxisDeadzone(0.2)
        .configXAxisInverted(true)
        .configYAxisInverted(true);

    private final XboxController ctlOperator = new XboxController(1);


    //Driver Button Mapping
    private final Trigger btnZeroGyro = new Trigger(ctlDriver::getStartButton);

    private final Trigger btnDriver_LowSpeed = new Trigger(ctlDriver::getLeftTriggerButton);
    private final Trigger btnDriver_HighSpeed = new Trigger(ctlDriver::getRightTriggerButton);

    private final Trigger btnIntake = new Trigger(ctlDriver::getRightBumperButton);
    private final Trigger btnShoot = new Trigger(ctlDriver::getLeftStickButton)
                                        .and(ctlDriver::getRightStickButton);

    //Operator Button Mapping
    private final Trigger btnAnticlimb = new Trigger(() -> (ctlDriver.getPOV() == 0) || (ctlOperator.getPOV() == 0));
    private final Trigger btnClimb = new Trigger (() -> (ctlDriver.getPOV() == 180) || (ctlOperator.getPOV() == 180));
    private final Trigger btnFlush = new Trigger(() -> ctlDriver.getBButton() || ctlOperator.getBButton());

    //Driver Axis Mapping
    private final Supplier<Double> axsDriver_Translation = () -> dshInputScale.get().scale(ctlDriver.getLeftY());
    private final Supplier<Double> axsDriver_Strafe = () -> dshInputScale.get().scale(ctlDriver.getLeftX());
    private final Supplier<Double> axsDriver_Rotation = () -> dshInputScale.get().scale(ctlDriver.getRightX());

    /* Subsystems */
    private final PoseEstimator s_PoseEstimator = new PoseEstimator();
    private final Swerve sysSwerve = new Swerve(s_PoseEstimator);
    private final Shooter sysShooter = new Shooter();
    private final Climber sysClimber = new Climber();

    private final UsbCamera camIntake;

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        sysSwerve.setDefaultCommand(
            new SwerveCommand(
                sysSwerve, 
                () -> axsDriver_Translation.get() * getSpeedMult(), 
                () -> axsDriver_Strafe.get() * getSpeedMult(), 
                () -> axsDriver_Rotation.get() * getSpeedMult(), 
                () -> dshDriveStyle.get() == DriveStyle.ROBOT_CENTRIC,
                () -> 0.0 // Dynamic heading placeholder
            )
        );

        
        try{
            camIntake = CameraServer.startAutomaticCapture("Intake Camera", 0);
            camIntake.setFPS(15);
            camIntake.setResolution(128, 80);
            camIntake.setBrightness(50);
        } finally {
            //Just ignore camera if it fails
        }

        // Configure the button bindings
        configureButtonBindings();
        
        //Auto chooser
        autoChooser = AutoBuilder.buildAutoChooser("Do Nothing"); // Default auto will be `Commands.none()`
        SmartDashboard.putData("Autonmous Sequence", autoChooser);

        new DashboardValue<Boolean>(tblHawks, "Low Speed", () -> btnDriver_LowSpeed.getAsBoolean() && !btnDriver_HighSpeed.getAsBoolean());
        new DashboardValue<Boolean>(tblHawks, "High Speed", () -> btnDriver_HighSpeed.getAsBoolean());
    }

    private double squareInput(double inputValue) {
        double sign = inputValue < 0.0 ? -1.0 : 1.0;
        return inputValue * inputValue * sign;
    }

    private double cubeInput(double inputValue) {
        return inputValue * inputValue * inputValue;
    }

    /**
     * Bind your triggers to their respective Commands
     */
    private void configureButtonBindings() {
        /* Driver Buttons */
        btnZeroGyro.onTrue(new InstantCommand(() -> sysSwerve.zeroHeading()));

        btnIntake.whileTrue(sysShooter.intake());
        btnShoot.whileTrue(sysShooter.shoot())
            .whileTrue(new SwerveCommand(sysSwerve, () -> 0.0, () -> 0.0, () -> 0.0, () -> false, () -> 0.0));

        btnAnticlimb.whileTrue(sysClimber.extend());
        btnClimb.whileTrue(sysClimber.retract());
        btnFlush.whileTrue(sysShooter.flush());
    }

    private double getSpeedMult() {
        return (btnDriver_HighSpeed.getAsBoolean() ? DriverConstants.HIGH_SPEED : (btnDriver_LowSpeed.getAsBoolean() ? DriverConstants.SLOW_SPEED : DriverConstants.STANDARD_SPEED));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An ExampleCommand will run in autonomous
        return autoChooser.getSelected();
    }
}