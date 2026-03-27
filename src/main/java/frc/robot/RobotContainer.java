package frc.robot;

import static frc.robot.Dashboard.tblDrivingOptions;

import java.util.function.DoubleSupplier;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.hawklib.dashboard.DashboardSelector;
import frc.hawklib.hid.XboxController;
import frc.robot.Constants.DriverConstants;
import frc.robot.commands.ShimmyCommand;
import frc.robot.commands.SwerveCommand;
import frc.robot.commands.autonoumous.DoNothingSequence;
import frc.robot.commands.autonoumous.JustShootSequence;
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
public class RobotContainer {

    //Selector Options
    private enum DriveStyle {
        FIELD_CENTRIC("Field Centric"),
        ROBOT_CENTRIC("Robot Centric");

        private static final String TITLE = "Drive Style";
        private final String LABEL;
        private DriveStyle(String label) { LABEL = label; }

        public String toString() { return LABEL; }
    }

    private enum InputCurve {
        NONE("None"), 
        SQUARED("Squared"), 
        CUBED("Cubed");

        private static final String TITLE = "Input Curve";
        private final String LABEL;
        private InputCurve(String label) { LABEL = label; }
        
        public String toString() { return LABEL; }
    }

    /* Selectors */
    private final DashboardSelector<DriveStyle> dshDriveStyle = new DashboardSelector<DriveStyle>(tblDrivingOptions, DriveStyle.TITLE, DriveStyle.ROBOT_CENTRIC);
    private final DashboardSelector<InputCurve> dshInputCurve = new DashboardSelector<InputCurve>(tblDrivingOptions, InputCurve.TITLE, InputCurve.NONE);

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

    //Driver Axis Mapping
    private final DoubleSupplier axsDriveTranslation = () -> curveInput(ctlDriver.getLeftY()) * getSpeedMult();
    private final DoubleSupplier axsDriveStrafe = () -> curveInput(ctlDriver.getLeftX()) * getSpeedMult();
    private final DoubleSupplier axsDriveRotation = () -> curveInput(ctlDriver.getRightX()) * getSpeedMult();

    //Operator Button Mapping
    private final Trigger btnIntake = new Trigger(ctlOperator::getRightBumperButton);
    private final Trigger btnShoot = new Trigger(ctlOperator::getLeftBumperButton);
    private final Trigger btnFlush = new Trigger(ctlOperator::getBButton);

    private final Trigger btnShimmy = new Trigger(ctlOperator::getLeftTriggerButton);

    private final Trigger btnAnticlimb = new Trigger(() -> (ctlOperator.getPOV() == 0));
    private final Trigger btnClimb = new Trigger (() -> (ctlOperator.getPOV() == 180));

    /* Subsystems */
    private final PoseEstimator s_PoseEstimator = new PoseEstimator();
    private final Swerve sysSwerve = new Swerve(s_PoseEstimator);
    private final Shooter sysShooter = new Shooter();
    private final Climber sysClimber = new Climber();

    /* Camera */
    private final UsbCamera camIntake;

    /* Basic Swerve Commands */
    private final SwerveCommand cmdDrive_Stop = new SwerveCommand(sysSwerve, ()->0.0, ()->0.0, ()->0.0, ()->true);
    private final ShimmyCommand cmdDrive_Shimmy = new ShimmyCommand(sysSwerve);

    /* Autonomous Sequences */
    private final DoNothingSequence autDoNothing = new DoNothingSequence(sysSwerve, sysShooter, sysClimber);
    private final JustShootSequence autJustShoot = new JustShootSequence(sysSwerve, sysShooter, sysClimber);

    /* Autonomous Chooser */
    private final SendableChooser<Command> chsAutonomousSequence = new SendableChooser<Command>();

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        sysSwerve.setDefaultCommand(
            new SwerveCommand(
                sysSwerve, 
                axsDriveTranslation, 
                axsDriveStrafe, 
                axsDriveRotation, 
                () -> dshDriveStyle.get() == DriveStyle.ROBOT_CENTRIC
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

        /* Add options to Autonomous Sequence choose */
        chsAutonomousSequence.setDefaultOption("Do Nothing", autDoNothing);
        chsAutonomousSequence.addOption("Just Shoot", autJustShoot);
        SmartDashboard.putData("Autonomous Sequence", chsAutonomousSequence);
    }

    /**
     * Bind your triggers to their respective Commands
     */
    private void configureButtonBindings() {
        /* Driver Buttons */
        btnZeroGyro.onTrue(new InstantCommand(() -> sysSwerve.zeroHeading()));

        // Operator Buttons
        btnIntake.whileTrue(sysShooter.intake());
        btnShoot.whileTrue(sysShooter.shoot());

        btnAnticlimb.whileTrue(sysClimber.extend());
        btnClimb.whileTrue(sysClimber.retract());
        btnFlush.whileTrue(sysShooter.flush());

        btnShimmy.whileTrue(cmdDrive_Shimmy);
            //.onFalse(cmdDrive_Stop);
    }

    private double curveInput(double inputValue) {
        InputCurve selectedCurve = dshInputCurve.get();
        if(selectedCurve == InputCurve.NONE)
            return inputValue;
        else if(selectedCurve == InputCurve.SQUARED)
            return inputValue * inputValue * (inputValue < 0.0 ? -1.0 : 1.0);
        else 
            return inputValue * inputValue * inputValue;
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
        return chsAutonomousSequence.getSelected();
    }
}