package org.firstinspires.ftc.teamcode.SharedClasses;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class RobotHardware {
    //DcMotorEx has extended capabilities like velocity control

    //Drive Motors     Format  - public DcMotor name; or public DcMotorEx name;
    public DcMotor frontLeftDrive;
    public DcMotor frontRightDrive;
    public DcMotor backLeftDrive;
    public DcMotor backRightDrive;


    //Mechanism Motors
    public DcMotor motorIntake;
    public DcMotor motorTurret;
    public DcMotor motorFlywheel;

    //Servos -  Format  - public Servo name; - or public CRServo name; -
    public Servo servoIB;
    public Servo servoIF;


    //Sensors   FORMAT public Sensor Type name;
    public GoBildaPinpointDriver odo;

    //Static Variables


    //////////////////////////    INITIALIZE ALL HARDWARE    /////////////////////////////
    public void init(HardwareMap hardwareMap) {

        // Drive Motors
        frontLeftDrive = hardwareMap.get(DcMotor.class, "motorFL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "motorFR");
        backLeftDrive = hardwareMap.get(DcMotor.class, "motorBL");
        backRightDrive = hardwareMap.get(DcMotor.class, "motorBR");


        // Mechanism Motors
        motorIntake = hardwareMap.get(DcMotor.class, "motorIntake");
        motorTurret = hardwareMap.get(DcMotor.class, "motorTurret");
        motorFlywheel = hardwareMap.get(DcMotor.class, "motorFlywheel");

        // Servos
        servoIB = hardwareMap.get(Servo.class, "servoIB");
        servoIF = hardwareMap.get(Servo.class, "servoIF");

        // Sensors
        odo = hardwareMap.get(
                GoBildaPinpointDriver.class, "odo");

        // Motor Directions
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);

        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        // Brake Mode
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        // Servo Directions


        // Launcher Configuration
        motorFlywheel.setDirection(DcMotorSimple.Direction.REVERSE);


        // Initialize Servos


        // Reset Odometry
        odo.resetPosAndIMU();

    }//Closes Initialization


}// Closes Public Class
