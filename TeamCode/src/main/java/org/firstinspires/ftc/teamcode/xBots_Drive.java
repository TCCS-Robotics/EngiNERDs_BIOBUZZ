
package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import static  org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.normalizeRadians;
import static  org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.normalizeRadians;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;



@TeleOp(name = "xBots_Drive_Pinpoint", group = "Robot")
//@Disabled
public class xBots_Drive extends OpMode {
    // This declares the four motors needed
    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;
    DcMotor flywheel;


    // goBILDA Pinpoint Odometry Computer
    GoBildaPinpointDriver odo;


    @Override
    public void init() {

        /////////////////////////////////Hardware Map///////////////////////////////
        //Motors
        frontLeftDrive = hardwareMap.get(DcMotor.class, "motorFL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "motorFR");
        backLeftDrive = hardwareMap.get(DcMotor.class, "motorBL");
        backRightDrive = hardwareMap.get(DcMotor.class, "motorBR");
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");
        //Servos

        //Sensors
        odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");


        /*Set Motor Directions so all wheels rotate forward with positive power values
            This will be DIFFERENT on EACH ROBOT depending on wheel and motor placement*/
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);

        // This uses RUN_USING_ENCODER to be more accurate.
        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Reset the Pinpoint position
        odo.resetPosAndIMU();

    }

    @Override
    //////////////////////////ADD TELEOP CODE HERE////////////////////////////////////
    public void loop() {
        telemetry.addLine("Press Back to reset Yaw");
        telemetry.addLine("Hold left bumper to drive in robot relative");
        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");
        telemetry.addLine("Right Trigger puts the robot into slow mode");

        //Update Pinpoint Telemetry
        odo.update();
        telemetry.addData("Heading",odo.getHeading(AngleUnit.DEGREES));

        telemetry.update();


        ////////////DRIVE CODE//////////////
        // If you press the A button, then you reset the Yaw to be zero from the way
        // the robot is currently pointing
        if (gamepad1.back) {
            odo.resetPosAndIMU();
        }
        // If you press the left bumper, you get a drive from the point of view of the robot
        // (much like driving an RC vehicle)
        if (gamepad1.left_bumper && gamepad1.right_trigger > 0) {
            drive(-gamepad1.left_stick_y *.10, gamepad1.left_stick_x *.10, gamepad1.right_stick_x *.10);
        }
        else if (gamepad1.left_bumper && gamepad1.right_trigger == 0){
            drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        }

        else if (!gamepad1.left_bumper && gamepad1.right_trigger > 0) {
            driveFieldRelative(-gamepad1.left_stick_y *.10, gamepad1.left_stick_x *.10, gamepad1.right_stick_x *.10);
        }
        else {
                driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        }// Ends Drive If else statements

    //////////////   LAUNCHER   //////////////
        flywheel.setPower(gamepad2.left_stick_y * -1);


    }// ends Teleop Loop


////////////////////////FUNCTIONS FOR TELEPOP////////////////////////////////////////////////

    // This routine drives the robot field relative
    private void driveFieldRelative(double forward, double right, double rotate) {
        // Convert joystick input to polar coordinates
        double theta = Math.atan2(forward, right);
        double r = Math.hypot(right, forward);

        // Get heading from Pinpoint
        double heading = odo.getHeading (AngleUnit.RADIANS);

        // Rotate joystick vector by robot heading
        theta = normalizeRadians(theta - heading);

        // Convert back to Cartesian coordinates
        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);

        // Drive robot-relative
        drive(newForward, newRight, rotate);

        // Finally, call the drive method with robot relative forward and right amounts
        drive(newForward, newRight, rotate);
    }

    // Thanks to FTC16072 for sharing this code!!
    public void drive(double forward, double right, double rotate) {
        // This calculates the power needed for each wheel based on the amount of forward,
        // strafe right, and rotate
        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backRightPower = forward + right - rotate;
        double backLeftPower = forward - right + rotate;

        double maxPower = 1.0;
        double maxSpeed = 1.0;  // make this slower for outreaches

        // This is needed to make sure we don't pass > 1.0 to any wheel
        // It allows us to keep all of the motors in proportion to what they should
        // be and not get clipped
        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));

        // We multiply by maxSpeed so that it can be set lower for outreaches
        // When a young child is driving the robot, we may not want to allow full
        // speed.
        frontLeftDrive.setPower(maxSpeed * (frontLeftPower / maxPower));
        frontRightDrive.setPower(maxSpeed * (frontRightPower / maxPower));
        backLeftDrive.setPower(maxSpeed * (backLeftPower / maxPower));
        backRightDrive.setPower(maxSpeed * (backRightPower / maxPower));
    }
}
