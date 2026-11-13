package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.SharedClasses.MecanumDrive_Smooth;
import org.firstinspires.ftc.teamcode.SharedClasses.RobotHardware;


@TeleOp(name = "TeleOp", group = "Robot")
//@Disabled
public class Teleop_NoCamera extends OpMode {
    //Import Hardware Class & Mecanum Drive Class
    RobotHardware robot = new RobotHardware();
    MecanumDrive_Smooth drive;

    //Variables
    private boolean previousResetButton = false;

    /////////////////////     VARIABLES for Velocity Stepper     /////////////////////
    // These variables live OUTSIDE loop() so their values survive each loop.

    // START OF Velocity Toggling and Adjustment Variables
    private static final double RPM_STEP = 100.0; // RPM changed per D-pad press
    private static final double MAX_RPM = 6000.0; // Adjustable upper safety limit
    private static final double READY_TOLERANCE = 0.05; // Within 5% of target

    // These values must persist between loop() calls; local loop variables reset
    // on every iteration and would not remember the selected speed or ON/OFF state.
    private double targetRPM = 0.0; //Starting Target RPM

    // Remember the PREVIOUS button state to detect a NEW press (rising edge).
    private boolean previousUp = false; //Checks to see if D Pad UP Button is still held or was released and then pressed again
    private boolean previousDown = false; //Checks to see if D Pad Down Button is still held or was released and then pressed again

    /////////////////////     VARIABLES for Toggle Button    /////////////////////
    // These variables live OUTSIDE loop() so their values survive each loop.

    private boolean flywheelOn = false; //Starting flywheel State is Off
    // Remember the PREVIOUS button state to detect a NEW press (rising edge).
    private boolean previousA = false; //Checks to see if A button is still held or was released and then pressed again
    // END OF Velocity Toggling and Adjustment Variables


    @Override
    //////////////////////// Initialization //////////////////////////////
    public void init() {
    //Initialize Hardware and Mecanum Drive Classes
        robot.init(hardwareMap);
        drive = new MecanumDrive_Smooth(robot);

   }

    @Override
    /////////////// TeleOp Loop ////////////////////////////////////
    public void loop() {

        // Update Pinpoint
        robot.odo.update();

/////////////////////////////////   INTAKE   /////////////////////////////////////////////
        if (gamepad2.right_trigger > 0 && gamepad1.left_trigger == 0) {
            robot.intake.setPower(1);
        }
        else if (gamepad2.left_trigger > 0 && gamepad1.right_trigger == 0) {
            robot.intake.setPower(-1);
        }
        else {
            robot.intake.setPower(0);
        }
        // Servos to Lift and Lower Arm to move elemnts into Flywheel
        if (gamepad2.x) {      // Lowered Position
            robot.AL.setPosition(-0.9);
            robot.AR.setPosition(-0.9);
        }
        if (gamepad2.y) {  // Raised Position
            robot.AL.setPosition(0.25);
            robot.AR.setPosition(0.25);
        }
/////////////////////////////////////////////////////////////////////////////////////
//////////////////             Turret & Slider                      /////////////////////
///////////////////////////////////////////////////////////////////////////////////



/////////////////////////////////////////////////////////////////////////////////////
//////////////////              Flywheel                       /////////////////////
///////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////
    ///           Velocity Increase/Decrease using Dpad Up and Down          ///
    ////////////////////////////////////////////////////////////////////////////
        // Check TWO conditions:
        // 1. gamepad2.dpad_up/down is TRUE (the button is being pressed).
        // 2. !previousUp/Down is TRUE (the button was NOT pressed last loop).

        //The ! operator reverses a boolean value.

        // Increase target RPM when D-pad UP is newly pressed. (not held down - rising edge)
        if (gamepad2.dpad_up && !previousUp) {
            targetRPM = Math.min(MAX_RPM, targetRPM + RPM_STEP);
        }

        // Decrease target RPM when D-pad DOWN is newly pressed. (not held down - rising edge)
        if (gamepad2.dpad_down && !previousDown) {
            targetRPM = Math.max(0, targetRPM - RPM_STEP);
        }

        // Save button states for the next loop.
        previousUp = gamepad2.dpad_up;
        previousDown = gamepad2.dpad_down;

        // Convert target RPM into encoder ticks per second.
        // FTC setVelocity() uses ticks per second, not RPM.
        //FORMULA:  TPS = RPM * TICKS_PER_REV / 60
        //We multiply by ticks per revolution to convert revolutions into encoder ticks. We divide by 60 because there are 60 seconds in one minute.
        double targetTPS = targetRPM * RobotHardware.flywheel_TICKS_PER_REV / 60.0;
        /////////////////////////////////////////////////////////////////////////////
        ///                Toggle Flywheel on and Off using A button             ///
        ////////////////////////////////////////////////////////////////////////////

        // Toggle flywheel ON or OFF with each new A button press. (not held down - rising edge)
        //The ! operator reverses a boolean value.
        if (gamepad2.a && !previousA) {
            flywheelOn = !flywheelOn;
        }

        // Save A button state for the next loop
        previousA = gamepad2.a;

        // Determine the commanded RPM based on ON/OFF state.
        // If ON, use targetRPM. If OFF, use zero.
        double commandedRPM = flywheelOn ? targetRPM : 0.0; //
        robot.flywheel.setVelocity(flywheelOn ? targetTPS : 0.0); //reads as If flywheel on

        /////////////////////////////////////////////////////////////////////////////
        ///                   FLYWHEEL ENCODER FEEDBACK                          ///
        ////////////////////////////////////////////////////////////////////////////
        // Read actual motor speed in encoder ticks per second.
        double actualTPS = robot.flywheel.getVelocity();
        // Convert actual motor speed to RPM.
        double actualRPM = actualTPS * 60.0 / RobotHardware.flywheel_TICKS_PER_REV;
        // Calculate difference between commanded and actual RPM.
        double errorRPM = commandedRPM - actualRPM;
        // Check whether flywheel is within the allowed speed range. If it is it's "Ready"
        boolean ready = flywheelOn && commandedRPM > 0
                && Math.abs(errorRPM) <= commandedRPM * READY_TOLERANCE;






/////////////////////////////////////////////////////////////////////////////////////
////////////////////////    Drive Code - Game Pad 1       //////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
   // Back = Reset Position
        if (gamepad1.back && !previousResetButton) {
            robot.odo.resetPosAndIMU();
        }
        previousResetButton = gamepad1.back;


    // Read gamepad 1
        double forward = -gamepad1.left_stick_y;
        double right = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        // Slow mode while holding right trigger
        if (gamepad1.right_trigger > 0) {
            drive.setMaxSpeed(0.10);
        } else {
            drive.setMaxSpeed(1.0);
        }

        // Left bumper switches to robot-relative
        if (gamepad1.left_bumper) {
            drive.driveRobotRelative(forward, right, rotate);
        } else {
            drive.driveFieldRelative(forward, right, rotate);
        }

////////////////////////    TELEMETRY        //////////////////////////

        telemetry.addData("Heading",robot.odo.getHeading(AngleUnit.RADIANS));
        telemetry.addData("Motor", flywheelOn ? "ON" : "OFF");
        telemetry.addData("Selected RPM", "%.0f", targetRPM);
        telemetry.addData("Commanded RPM", "%.0f", commandedRPM);
        telemetry.addData("Actual RPM", "%.0f", actualRPM);
        telemetry.addLine("Gamepad 2: A toggles Flywheel on/off");
        telemetry.addLine("Gamepad 2: D-pad Up/Down changes RPS");
        telemetry.addLine("Game Pad 1: Press Back to reset Yaw");
        telemetry.addLine("Game Pad 1: Hold Left Bumper for Robot Centric");
        telemetry.addLine("Game Pad 1: Hold Right Trigger for Slow Mode");
        telemetry.update();


    }//Closes TeleOp Loop

////////////////////////////////// ENTER FUNCTIONS HERE//////////////////////////////////////



}//Closes Op Mode
