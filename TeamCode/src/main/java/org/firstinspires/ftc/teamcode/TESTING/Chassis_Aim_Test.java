package org.firstinspires.ftc.teamcode.TESTING;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.SharedClasses.MecanumDrive;
import org.firstinspires.ftc.teamcode.SharedClasses.RobotHardware;

//@Disabled
@TeleOp(name = "Limelight Chassis Aim Test", group = "Vision")
public class Chassis_Aim_Test extends LinearOpMode {
    @Override public void runOpMode() {
        RobotHardware robot = new RobotHardware();
        robot.init(hardwareMap);
        MecanumDrive drive = new MecanumDrive(robot);
        AprilTagTargeting targeting = new AprilTagTargeting(robot.limelight);
        AprilTagTargeting.Cluster cluster = AprilTagTargeting.Cluster.RED_SCORING;
        AprilTagTargeting.AimMode mode = AprilTagTargeting.AimMode.CLUSTER_CENTER;

        robot.limelight.pipelineSwitch(0); // Configure pipeline 0 for AprilTag 36h11.
        telemetry.addLine("RB hold: auto aim | A: cluster | B: scoring (after calibration)");
        telemetry.addLine("D-pad U red field, R red audience, D blue field, L blue audience");
        telemetry.update();
        waitForStart();
        if (isStopRequested()) return;
        robot.limelight.start();
        try {
            while (opModeIsActive()) {
                robot.odo.update(); // Keep Pinpoint current for field-relative driving.
                if (gamepad1.dpad_up) cluster = AprilTagTargeting.Cluster.RED_SCORING;
                else if (gamepad1.dpad_right) cluster = AprilTagTargeting.Cluster.RED_AUDIENCE;
                else if (gamepad1.dpad_down) cluster = AprilTagTargeting.Cluster.BLUE_SCORING;
                else if (gamepad1.dpad_left) cluster = AprilTagTargeting.Cluster.BLUE_AUDIENCE;
                if (gamepad1.a) mode = AprilTagTargeting.AimMode.CLUSTER_CENTER;
                if (gamepad1.b) mode = AprilTagTargeting.AimMode.SCORING_OPENING;

                double forward = -gamepad1.left_stick_y;
                double right = gamepad1.left_stick_x;
                double rotate = gamepad1.right_stick_x;
                AprilTagTargeting.AimResult aim = targeting.calculate(cluster, mode);
                if (gamepad2.right_bumper) rotate = aim.valid ? aim.rotation : 0.0;
                drive.driveFieldRelative(forward, right, rotate);

                telemetry.addData("Cluster", cluster);
                telemetry.addData("Aim mode", mode);
                telemetry.addData("Detected tags", aim.count);
                telemetry.addData("Status", aim.status);
                telemetry.addData("Error (deg)", "%.2f", aim.errorDeg);
                telemetry.addData("Range approx (in)", "%.1f", aim.estimatedRangeIn);
                telemetry.addData("Rotation command", "%.2f", rotate);
                telemetry.update();
            }
        } finally {
            drive.stop();
            robot.limelight.stop();
        }
    }
}
