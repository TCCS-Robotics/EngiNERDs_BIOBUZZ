package org.firstinspires.ftc.teamcode.TESTING;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.SharedClasses.MecanumDrive_Smooth;
import org.firstinspires.ftc.teamcode.SharedClasses.RobotHardware;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///                        USE THIS TO STEAL THE TOGGLE CODE & HOW TO UE D PAD TO INCREASE VELOCITY                          //
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Disabled
@TeleOp(name = "Toggle and Increase Velocity", group = "Test")
public class Toggle_and_Increase_Velocity extends OpMode {

/////////////////////     HARDWARE     /////////////////////
    // Shared hardware class owns all hardwareMap and motor setup.
    RobotHardware robot = new RobotHardware(); // Loads the hardware Map


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

    // Remember the PREVIOUS button state to detect a NEW press (rising edge).

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


        telemetry.addLine("flywheel Velocity Test Ready");
        telemetry.addLine("Gamepad 2: A toggles; D-pad Up/Down changes RPS");
        telemetry.update();
    }

    @Override
    //////////////////////// TeleOp Loop /////////////////////////////////
    public void loop() {
        // loop() runs repeatedly after PLAY; put changing inputs, motor
        // commands, encoder readings, and live telemetry in this section.

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

    //////////////////////// TELEMETRY  /////////////////////////////////
        telemetry.addLine("=== flywheel ===");
        telemetry.addData("Motor", flywheelOn ? "ON" : "OFF");
        telemetry.addData("Selected RPM", "%.0f", targetRPM);
        telemetry.addData("Commanded RPM", "%.0f", commandedRPM);
        telemetry.addData("Actual RPM", "%.0f", actualRPM);
        telemetry.addData("Velocity Error RPM", "%.0f", errorRPM);
        telemetry.addData("Target TPS", "%.1f", flywheelOn ? targetTPS : 0.0);
        telemetry.addData("Actual TPS", "%.1f", actualTPS);
        telemetry.addData("Ready to Fire", ready ? "YES" : "NO");
        telemetry.addLine("Gamepad 2: A = On/Off, D-pad Up/Down = +/-100 RPM");
        telemetry.update();

    } // Closes TeleOp Loop

    ////////////////////////        STOP WHEN OP MODE ENDS ///////////////////////////////
    @Override
    public void stop() {

        robot.flywheel.setVelocity(0);
    }

    //////////////////////// ENTER FUNCTIONS HERE ////////////////////////
} // Closes OpMode
