package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "Flywheel", group = "Robot")
@Disabled
public class Flywheel_Test extends OpMode {
    // This declares the four motors needed
    DcMotor flywheel;


    @Override
    //////////////////////// Initialization //////////////////////////////
    public void init() {
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");
    }

    @Override

    /////////////// TeleOp Loop ////////////////////////////////////
    public void loop() {
        flywheel.setPower(gamepad1.left_stick_y * -1);
    }// Closes TeleOp Loop

////////////////////// ENTER FUNCTIONS HERE//////////////////////////

}//Closes OpMode
