package org.firstinspires.ftc.teamcode.TESTING;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.SharedClasses.MecanumDrive;
import org.firstinspires.ftc.teamcode.SharedClasses.RobotHardware;


@Disabled
@TeleOp(name = "April Tag Detection Test", group = "Robot")
//@Disabled
public class AprilTag_DetectionTest extends OpMode {
    //Import Hardware Class & Mecanum Drive Class
    RobotHardware robot = new RobotHardware();
    MecanumDrive drive;

    //Variables



    @Override
    //////////////////////// Initialization //////////////////////////////
    public void init() {
    //Initialize Hardware and Mecanum Drive Classes
        robot.init(hardwareMap);
        drive = new MecanumDrive(robot);

    //Initialize pipeline & Start Limelight camera

    //Let us know we are initialized and Ready
        telemetry.addLine("Robot Initialized");
               telemetry.update();

   }

    @Override
    /////////////// TeleOp Loop ////////////////////////////////////
    public void loop() {

    ///////////////////////// Limelight Tag Detection ////////////////////////////////////


    ////////////////////////    TELEMETRY        //////////////////////////


    }//Closes TeleOp Loop

    @Override
    public void stop() {
        robot.limelight.stop();
        drive.stop();
    }

////////////////////////////////// ENTER FUNCTIONS HERE//////////////////////////////////////

// Cluster Index List


// Cluster Name List



}//Closes Op Mode
