package frc.robot;

import java.util.function.Supplier;

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.hawklib.Console;
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
public class RobotContainer {
	public static final NetworkTable tblHawks = NetworkTableInstance.getDefault().getTable("Hawks");
    public static final NetworkTable tblSubsystems = tblHawks.getSubTable("Subsystems");

    /* Controllers */
    private final XboxController ctlDriver = new XboxController(0)
        .configAxisDeadzone(0.1)
        .configXAxisInverted(true)
        .configYAxisInverted(true);


    //Driver Button Mapping
    private final Trigger btnZeroGyro = new Trigger(ctlDriver::getStartButton)
                                            .and(ctlDriver::getBackButton);
    private final Trigger btnFieldCentric = new Trigger(() -> false);

    private final Trigger btnDriver_LowSpeed = new Trigger(ctlDriver::getLeftBumperButton);
    private final Trigger btnDriver_HighSpeed = new Trigger(ctlDriver::getRightBumperButton);

    private final Trigger btnIntake = new Trigger(ctlDriver::getLeftTriggerButton);
    private final Trigger btnShooter = new Trigger(ctlDriver::getRightTriggerButton);
    private final Trigger btnFlush = new Trigger(ctlDriver::getBButton);

    private final Trigger btnAnticlimb = new Trigger(()->ctlDriver.getPOV()==0);
    private final Trigger btnClimb = new Trigger (()->ctlDriver.getPOV()==180);

    //Driver Axis Mapping
    private final Supplier<Double> axsDriver_Translation = () -> squareInput(ctlDriver.getLeftY());
    private final Supplier<Double> axsDriver_Strafe = () -> squareInput(ctlDriver.getLeftX());
    private final Supplier<Double> axsDriver_Rotation = () -> squareInput(ctlDriver.getRightX());

    private final Supplier<Double> SPEED_MULT = () -> (btnDriver_HighSpeed.getAsBoolean() ? DriverConstants.HIGH_SPEED : (btnDriver_LowSpeed.getAsBoolean() ? DriverConstants.SLOW_SPEED : DriverConstants.STANDARD_SPEED));

    /* Subsystems */
    private final PoseEstimator s_PoseEstimator = new PoseEstimator();
    private final Swerve sysSwerve = new Swerve(s_PoseEstimator);
    private final Shooter sysShooter = new Shooter();
    private final Climber sysClimber = new Climber();
    //private final Vision s_Vision = new Vision(s_PoseEstimator);

    private final UsbCamera camIntake;

    /* AutoChooser */
    private final SendableChooser<Command> autoChooser;

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        sysSwerve.setDefaultCommand(
            new SwerveCommand(
                sysSwerve, 
                () -> axsDriver_Translation.get() * SPEED_MULT.get(), 
                () -> axsDriver_Strafe.get() * SPEED_MULT.get(), 
                () -> axsDriver_Rotation.get() * SPEED_MULT.get(), 
                () -> !btnFieldCentric.getAsBoolean(),
                () -> 0 // Dynamic heading placeholder
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
    }

    @SuppressWarnings("unused")
    private double squareInput(double inputValue) {
        double sign = inputValue < 0.0 ? -1.0 : 1.0;
        return inputValue * inputValue * sign;
    }

    @SuppressWarnings("unused")
    private double cubeInput(double inputValue) {
        return inputValue * inputValue * inputValue;
    }

    /**
     * Use this method to define your button->command mappings. Buttons can be created by
     * instantiating a {@link GenericHID} or one of its subclasses ({@link
     * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
     * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */
    private void configureButtonBindings() {
        /* Driver Buttons */
        btnZeroGyro.onTrue(new InstantCommand(() -> sysSwerve.zeroHeading()));

        btnShooter.whileTrue(sysShooter.cmdShoot);
        btnIntake.whileTrue(sysShooter.cmdIntake);
        btnFlush.whileTrue(sysShooter.cmdFlush);

        btnAnticlimb.whileTrue(sysClimber.cmdExtend);
        btnClimb.whileTrue(sysClimber.cmdRetrack);
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