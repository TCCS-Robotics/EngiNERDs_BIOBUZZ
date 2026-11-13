package org.firstinspires.ftc.teamcode.TESTING;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.SharedClasses.AprilTagTargeting_Turret;
import org.firstinspires.ftc.teamcode.SharedClasses.MecanumDrive;
import org.firstinspires.ftc.teamcode.SharedClasses.RobotHardware;

@TeleOp(name = "Limelight Turret Aim Test", group = "Vision")
public class Turret_Aim_Test extends LinearOpMode {
    @Override public void runOpMode() {
        RobotHardware robot = new RobotHardware();
        robot.init(hardwareMap);
        MecanumDrive drive = new MecanumDrive(robot);
        // Turret motor is initialized by RobotHardware as "motorTurret".
        AprilTagTargeting_Turret targeting = new AprilTagTargeting_Turret(robot.limelight);
        AprilTagTargeting_Turret.Alliance alliance = AprilTagTargeting_Turret.Alliance.RED;
        AprilTagTargeting_Turret.AimMode mode = AprilTagTargeting_Turret.AimMode.CLUSTER_CENTER;

        robot.limelight.pipelineSwitch(0); // Set pipeline 0 to AprilTag 36h11.
        telemetry.addLine("Gamepad2 D-pad UP = RED, DOWN = BLUE");
        telemetry.addLine("Gamepad2 A = center, B = scoring offset");
        telemetry.addLine("Hold gamepad2 RB to auto-aim turret");
        telemetry.addLine("Camera must rotate with turret; calibrate tag offsets");
        telemetry.addLine("Test at low power; no turret travel limits configured");
        telemetry.update();
        waitForStart();
        if (isStopRequested()) return;
        robot.limelight.start();
        try {
            while (opModeIsActive()) {
                robot.odo.update();
                if (gamepad2.dpad_up) alliance = AprilTagTargeting_Turret.Alliance.RED;
                if (gamepad2.dpad_down) alliance = AprilTagTargeting_Turret.Alliance.BLUE;
                if (gamepad2.a) mode = AprilTagTargeting_Turret.AimMode.CLUSTER_CENTER;
                if (gamepad2.b) mode = AprilTagTargeting_Turret.AimMode.SCORING_OPENING;

                // Chassis rotation always belongs to gamepad1.
                drive.driveFieldRelative(-gamepad1.left_stick_y,
                        gamepad1.left_stick_x, gamepad1.right_stick_x);

                AprilTagTargeting_Turret.AimResult aim = targeting.calculate(alliance, mode);
                double turretPower = gamepad2.right_bumper && aim.valid ? aim.motorPower : 0.0;
                robot.turret.setPower(turretPower);

                telemetry.addData("Alliance", alliance);
                telemetry.addData("Mode", mode);
                telemetry.addData("Selected cluster first ID", aim.firstTagId);
                telemetry.addData("Selected cluster tags", aim.count);
                telemetry.addData("Status", aim.status);
                telemetry.addData("Aim error (deg)", "%.2f", aim.errorDeg);
                telemetry.addData("Turret motor power", "%.2f", turretPower);
                telemetry.update();
            }
        } finally {
            robot.turret.setPower(0);
            drive.stop();
            robot.limelight.stop();
        }
    }
}
